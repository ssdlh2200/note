:::tips
<font style="color:rgb(31, 35, 40);">Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients.</font>

<font style="color:rgb(31, 35, 40);">Netty 是一个异步事件驱动的网络应用框架，用于快速开发可维护的高性能协议服务器和客户端。</font>

+ <font style="color:rgb(31, 35, 40);">event-driven（事件驱动）：IO多路复用</font>
+ <font style="color:rgb(31, 35, 40);">这里的异步指的是Netty采用了多线程，将方法调用和处理结果相分离</font>


# Netty初认识
服务端

```c
@Slf4j
public class Server {
    public static void main(String[] args) {
        // 服务器端启动器，负责组装netty组件
        new ServerBootstrap()
                /*
                    loop 循环想等于 while(true)
                    添加组件 NioEventLoopGroup
                    与Boss-Worker模型原理类似
                    NioEventLoopGroup = BossEventLoopGroup + WorkerEventLoop(selector, thread)
                */
                .group(new NioEventLoopGroup())

                /*
                    选择channel实现NioServerSocketChannel基于原生的包装
                */
                .channel(NioServerSocketChannel.class)

                /*
                    告诉worker的EventLoop将来应该做什么事
                */
                .childHandler(
                        // 和客户端进行读写的 channel
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 添加具体 handler
                        ch.pipeline().addLast(new StringDecoder()); // 将bytebuffer转换为字符串
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() { //在读事件发生后的处理
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.info(msg.toString());
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
```

客户端

```c
public class Client {
    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder()); //将字符串转换为byteBuffer
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync() //阻塞方法，直到连接建立
                .channel() //连接对象，代表socketChannel
                .writeAndFlush("hello world"); // 发送数据，走到initChannel里面
    }
}
```

+ 把 channel 理解为数据的通道
+ 把msg理解为流动的数据，最开始输入是ByteBuffer，但经过 pipeline 的加工，会变成其它类型对象，最后输出又变成 ByteBuffer
+ 把handler理解为数据的处理工序
    - 工序有多道，合在一起就是 pipeline，pipeline 负责发布事件（读、读取完成.…）传播给每个handler，handler对自己感兴趣的事件进行处理（重写了相应事件处理方法）
    - handler 分为 Inbound 和 Outbound 两类
+ 把 eventLoop 理解为处理数据的工人
    - 工人可以管理多个 channel 的 io 操作（类似于selector），并且一旦工人负责了某个channel，就要负责到底（绑定）
    - 工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个channel的待处理任务，任务分为普通任务、定时任务
    - 工人按照pipeline顺序，依次按照handler的规划（代码）处理数据，可以为每道工序指定不同的工人

# Netty组件
## EventLoop
```java
                    +-------------------------------+
                    |        EventLoopGroup         |
                    |-------------------------------|
                    |    EventLoop 1   EventLoop 2  |
                    |       |             |         |
                    +-------|-------------|---------+
                            |             |
            +---------------+-------------+-------------+
            |               |                           |
       +----v----+    +-----v----+                 +----v----+
       |EventLoop|    | EventLoop|                 |EventLoop|
       +----|----+    +-----|----+                 +----|----+
            |               |                           |
            |               |                           |
        +---v-----+     +---v-----+                 +---v-----+
        | Channel |     | Channel |                 | Channel |
        +---------+     +---------+                 +---------+

```

```java
/*
Will handle all the I/O operations for a Channel once registered.
One EventLoop instance will usually handle more than one Channel
but this may depend on implementation details and internals.

注册后将处理通道（Channel）的所有 I/O 操作。
一个 `EventLoop` 实例通常会处理多个通道，但这也可能取决于具体的实现细节和内部机制。
*/

public interface EventLoop extends OrderedEventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}
```

**EventLoop** 本质是一个单线程执行器（里面维护了一个Selector)，里面有 run 方法处理Channel上源源不断的 io事件（accept，read，write等事件）

它的继承关系比较复杂

+ 一条线是继承自j.u.c.ScheduledExecutorService 因此包含了线程池中所有的方法
+ 另一条线是继承自netty自己的OrderedEventExecutor,
    - 提供了 boolean inEventLoop(Thread thread) 方法判断一个线程是否属于此 EventLoop
    - 提供了 parent 方法来看看自己属于哪个 EventLoopGroup

**EventLoopGroup **是一组 EventLoop，Channel 一般会调用 EventLoopGroup 的 register 方法来绑定其中一个 EventLoop，后续这个 Channel 上的 IO 事件都由此 EventLoop 来处理（保证了io事件处理时的线程安全）

+ 继承自netty自己的EventExecutorGroup
+ 实现了Iterable接口提供遍历EventLoop的能力
+ 另有next方法获取集合中下一个EventLoop

### 普通任务，定时任务
```java
@Slf4j
public class Test {
    public static void main(String[] args) {
        /*
         * 默认创建线程数为 NettyRuntime.availableProcessors()*2 电脑CPU的核心数*2
         *
         * */
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(2); // io事件，普通任务，定时任务


        // 执行普通任务，当前线程不想处理这件事，交给另外一个线程
        loopGroup.next().submit(() -> {
            log.info("hello");
        });

        // 执行定时任务
        loopGroup.next().scheduleAtFixedRate(() -> {
            log.info("定时任务");
        }, 0, 2, TimeUnit.SECONDS);
        
    }
}
```

### 处理IO事件
服务端

```java
@Slf4j
public class Test {
    public static void main(String[] args) {
       /*
            类似于boss worker 模型
            第一个 eventLoopGroup 就是 Boss 处理 accept 事件
            第二个 eventLoopGroup 就是 Worker 处理 client 中的 read 和 write事件

            问：new NioEventLoopGroup(1) Boss 模型中的线程是否设置为 1？
            答：设置与不设置的效果一样，因为 NioServerSocketChannel 只有一个绑定在 boosEventLoop
        */

        // Boss Group 处理 accept 事件，设置为1因为NioServerSocketChannel只有一个
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // Worker Group 处理客户端的读写事件
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        //只能处理普通任务和定时任务，将channel中需要长时间处理的任务交付到这里
        DefaultEventLoop tediousGroup = new DefaultEventLoop();

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    // 添加 Boss Group 中的处理器
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //当客户端建立连接时，ctx就是serverSocketChannel
                                //msg就是SocketChannel
                                SocketChannel client = (SocketChannel) msg;
                                log.info("客户端：[" + client.remoteAddress() + "]建立连接");
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                })
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    // 添加 Worker Group 中的处理器
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //处理客户端读事件，交给tediousLoop线程
                        ch.pipeline()
                                .addLast("handler-0", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("处理简单任务由worker处理");
                                        ctx.fireChannelRead(msg);//将消息传递给下一个handler
                                    }
                                })
                                .addLast(tediousGroup, "handler-1", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg; //与Nio中ByteBuffer不同注意！
                                        if (buf.getByte(buf.readableBytes() - 1) == '\n'){
                                            buf = buf.slice(0, buf.readableBytes() - 1);
                                        }
                                        log.info("处理复杂任务由tedious处理" + ctx.channel().remoteAddress() + "发来消息：" + buf.toString(StandardCharsets.UTF_8));
                                    }
                                });
                    }
                })
                .bind(8080);
    }
}
```

### 源码分析在handler处理中，如何切换eventLoop对象
在**<font style="color:#C99103;background-color:#ffffff;">AbstractChannelHandlerContext类</font>**<font style="color:#000000;background-color:#ffffff;">的</font>**<font style="color:#00627a;background-color:#ffffff;">invokeChannelRead()方法</font>**<font style="background-color:#ffffff;">中</font>

```java
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);

    
    EventExecutor executor = next.executor(); // 返回下一个 handler 的 eventLoop

    // 下一个 handler 的 eventLoop 是否与当前的 eventLoop 是同一个线程
    // 是，直接调用
    if (executor.inEventLoop()) {
        next.invokeChannelRead(m);
    } else {

        // 不是，将要执行的代码作为任务提交给下一个 eventLoop（换人）
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRead(m);
            }
        });
    }
}
```

如果两个 handler 绑定的是同一个线程，那么就直接调用，否则，把要调用的代码封装为一个任务对象，由下一个 handler 的线程来调用

## Channel
channel的主要作用

+ close() 可以用来关闭channel
+ closeFuture() 用来处理channel的关闭
    - sync方法作用是同步等待channel关闭
    - 而addListener方法是异步等待channel关闭
+ pipeline() 方法添加处理器
+ write() 方法将数据写入
+ writeAndFlush() 方法将数据写入并刷出

### 两种处理异步结果的方式
```java
public static void main(String[] args) throws InterruptedException {
    // future ，promise 一般都是需要异步处理
    ChannelFuture channelFuture = new Bootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder()); //将字符串转换为byteBuffer
                }
            })
            // connect 方法是异步非阻塞调用，交给另外一个线程进行connect(nio 里面的线程)
            .connect(new InetSocketAddress("localhost", 8080));

/*        // 处理异步结果方式一：阻塞当前线程，等待建立连接
    channelFuture.sync();
    Channel client = channelFuture.channel();
    client.writeAndFlush("hello this is client00");*/

    // 处理异步结果方式二：使用回调对象方法处理
    channelFuture.addListener(new ChannelFutureListener() {
        // 在建立连接之后会调用这个方法
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            Channel client = channelFuture.channel();
            client.writeAndFlush("hello this is client01");
        }
    });

}
```

### 关闭客户端
```java
public static void main(String[] args) throws InterruptedException {
    NioEventLoopGroup group = new NioEventLoopGroup();
    ChannelFuture channelFuture = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG)); //将调试信息输出,需要在log4j2xml中配置
                    ch.pipeline().addLast(new StringEncoder()); //将字符串转换为byteBuffer
                }
            })
            .connect(new InetSocketAddress("localhost", 8080));

    Channel client = channelFuture.sync().channel();
    new Thread(() -> {
        Scanner sc = new Scanner(System.in);

        while (true){
            String input = sc.nextLine();
            if ("q".equals(input)){
                log.info("关闭客户端");
                client.close(); // 这里close操作也是异步，close()是交给其nio中的线程去关闭
                break;
            }
            client.writeAndFlush(input);
        }
    }, "write").start();

    ChannelFuture closeFuture = client.closeFuture();
    
/*        //处理关闭方式1：同步处理关闭
    closeFuture.sync();
    log.info("关闭客户端成功！");*/
    
    //处理关闭方式2：异步关闭
    closeFuture.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            log.info("关闭客户端成功！");
            group.shutdownGracefully();
        }
    });
}
```

### 为什么要用异步
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725286792061-6851e31c-3e5e-477b-bd6c-634690d5f412.png)![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725286811959-0b0a2085-e206-4a92-9160-39ec52a98471.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725286845310-45059fd4-94f3-4f4d-bef8-f6701289b9f6.png)

要点

+ 单线程没法异步提高效率，必须配合多线程、多核CU才能发挥异步的优势
+ 异步并没有缩短响应时间，反而有所增加
+ 合理进行任务拆分，也是利用异步的关键
+ 提高的是单位时间内任务的吞吐

## Future&Promise
在异步处理时，经常用到这两个接口

首先要说明netty中的Future与jdk中的Future同名，但是是两个接口，netty的Future继承自jdk的Future,而Promise又对netty Future进行了扩展

+ jdk Future只能同步等待任务结束（或成功、或失败）才能得到结果
+ netty Future可以同步等待任务结束得到结果，也可以异步方式得到结果，但都是要等任务结束
+ netty Promise不仅有netty Future的功能，而且脱离了任务独立存在，只作为两个线程间传递结果的容器

| **功能/名称** | **jdk Future** | **netty Future** | **Promise** |
| --- | --- | --- | --- |
| **cancel** | 取消任务 |  |  |
| **isCanceled** | 任务是否取消 |  |  |
| **isDone** | 任务是否完成，不能区分成功失败 |  |  |
| **get** | 获取任务结果，阻塞等待 |  |  |
| **getNow** |  | 获取任务结果，非阻塞，还未产生结果时返回null |  |
| **await** |  | 等待任务结束，如果任务失败，不会抛异常，而是 通过isSuccess判断 |  |
| **sync** |  | 等待任务结束，如果任务失败，抛出异常 |  |
| **isSuccess** |  | 判断任务是否成功 |  |
| **cause** |  | 获取失败信息，非阻塞，如果没有失败，返回null |  |
| **addLinstener** |  | 添加回调，异步接收结果 |  |
| **setSuccess** |  |  | 设置成功 结果 |
| **setFailure** |  |  | 设置失败 结果 |


### jdk future
```java
// 线程池
ExecutorService service = Executors.newFixedThreadPool(2);

// 提交任务
Future<Integer> future = service.submit(new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        log.info("执行计算");
        Thread.sleep(3000);
        return 1 + 1;
    }
});

//获取结果
log.info("等待结果");
Integer result = future.get();
log.info("{}", result);
```

结果为：

```java
2024-09-03 02:52:40.690 [main] [INFO ] - 等待结果
2024-09-03 02:52:40.690 [pool-2-thread-1] [INFO ] - 执行计算
2024-09-03 02:52:43.711 [main] [INFO ] - 2
```

### netty future
#### 同步方式获取结果
```java
NioEventLoopGroup group = new NioEventLoopGroup(1);
EventLoop eventLoop = group.next(); // 拿到一个eventLoop = 单线程 + selector

//这里的Future extends java.util.concurrent.Future<V>
Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        log.info("执行一些计算");
        Thread.sleep(2000);
        return 1000;
    }
});
log.info("阻塞等待结果");
Integer result = future.get();
log.info("结果为：{}", result);
```

结果为：

```java
2024-09-03 02:52:13.838 [main] [INFO ] - 阻塞等待结果
2024-09-03 02:52:13.838 [nioEventLoopGroup-2-1] [INFO ] - 执行一些计算
2024-09-03 02:52:15.859 [main] [INFO ] - 结果为：1000
```

#### 异步方式获取结果
```java
NioEventLoopGroup group = new NioEventLoopGroup(1);
EventLoop eventLoop = group.next(); // 拿到一个eventLoop = 单线程 + selector

//这里的Future extends java.util.concurrent.Future<V>
Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
    @Override
    public Integer call() throws Exception {
        log.info("执行一些计算");
        Thread.sleep(2000);
        return 1000;
    }
});
future.addListener(new GenericFutureListener<Future<? super Integer>>() {
    @Override
    public void operationComplete(Future<? super Integer> future) throws Exception {
        log.info("结果为：{}", future.getNow());//getNow()立刻获取结果
    }
});
log.info("线程不会阻塞");
```

结果为：

```java
2024-09-03 02:51:17.662 [main] [INFO ] - 线程不会阻塞
2024-09-03 02:51:17.662 [nioEventLoopGroup-2-1] [INFO ] - 执行一些计算
2024-09-03 02:51:19.682 [nioEventLoopGroup-2-1] [INFO ] - 结果为：1000
```

### netty promise
```java
EventLoop eventLoop = new NioEventLoopGroup(1).next();

//promise 就是一个接收结果的容器
DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

new Thread(() -> {
    log.info("进行一些计算");
    try {
        int i = 1 / (new Random().nextInt(2)); // 1 / [0,2)
        Thread.sleep(2000);
        promise.setSuccess(80);
    } catch (Exception e) {
        //当计算发生异常时
        promise.setFailure(e);
    }
    //将结果放入promise容器中

}).start();

//处理结果
log.info("等待结果");
promise.await(); // 阻塞等待结果
if (promise.isSuccess()){
    log.info("结果是：{}", promise.getNow());
} else {
    log.error("发生错误：{}", promise.cause().getMessage());
}
```

运行结果：

```java
2024-09-03 04:11:42.623 [Thread-0] [INFO ] - 进行一些计算
2024-09-03 04:11:42.623 [main] [INFO ] - 等待结果
2024-09-03 04:11:44.653 [main] [INFO ] - 结果是：80
```

或者

```java
2024-09-03 04:13:00.265 [main] [INFO ] - 等待结果
2024-09-03 04:13:00.265 [Thread-0] [INFO ] - 进行一些计算
2024-09-03 04:13:00.281 [main] [ERROR] - 发生错误：/ by zero
```

## Handler&Pipeline
### 入站和出站处理器
ChannelHandler 用来处理 Channel 上的各种事件，分为入站、出站两种。所有 ChannelHandler 被连成一串，

就是Pipeline

+ 入站处理器通常是 ChannellnboundHandlerAdapter 的子类，主要用来读取客户端数据，写回结果
+ 出站处理器通常是 ChannelOutboundHandlerAdapter 的子类，主要对写回结果进行加工

打个比喻，每个 Channel 是一个产品的加工车间，Pipeline 是车间中的流水线，ChannelHandler 就是流水线上

的各道工序，而后面要讲的ByteBuf是原材料，经过很多工序的加工：先经过一道道入站工序，再经过一道道出站工序最终变成产品

```java
ChannelFuture channelFuture = new ServerBootstrap()
        .group(new NioEventLoopGroup())
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {

                //通过 channel 拿到 pipeline
                ChannelPipeline pipeline = ch.pipeline();
                /*
                    netty 会自动帮我们添加两个handler
                    headHandler 和 tailHandler
                    addLast() 实际上在 head 和 tail之间添加 handler
                    head -> h1 -> h2 -> h3 -> h4 ->  h5 -> tail 底层是一个双向链表
                */
                pipeline.addLast("h1", new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        ByteBuf buf = (ByteBuf) msg;
                        String message = buf.toString(StandardCharsets.UTF_8);
                        message = message.substring(0, message.length() - 1);
                        log.info("经过入站处理器handler1处理，结果{}", (message + ",h1"));
                        super.channelRead(ctx, message); //交给下一个入站处理器

                    }
                });
                pipeline.addLast("h2", new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                        log.info("经过入站处理器handler2处理，结果{}", msg.toString() + ",h2");
                        super.channelRead(ctx, msg);//交给下一个入站处理器
                    }
                });
                pipeline.addLast("h3", new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("经过入站处理器handler3处理，结果{}", msg.toString() + ",h3");

                        // 注意，ctx.writeAndFlush 此方法是从当前处理器向前寻找出站处理器
                        // ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("服务器接收到数据".getBytes()));
                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes("服务器接收到数据".getBytes()));
                    }
                });

                /*
                出站是 tail -> h5 -> h4
                */
                pipeline.addLast("h4", new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("经过出站处理器handler4处理");
                        super.write(ctx, msg, promise);
                    }
                });
                pipeline.addLast("h5", new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("经过出站处理器handler5处理");
                        super.write(ctx, msg, promise);
                    }
                });

            }
        })
        .bind(8080);
```

### 有关处理器顺序
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725350261967-a6a01d8c-cc2c-4b28-8180-57f1c3ddbbbd.png)

### 如何得知Handler是否为线程安全的？
如果handler类上有@Sharable注解就说明是线程安全的

### 测试channel，EmbeddedChannel
```java
// 入站处理器 in1
ChannelInboundHandlerAdapter in1 = new ChannelInboundHandlerAdapter(){
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("1");
        super.channelRead(ctx, msg);
    }
};
// 入站处理器 in2
ChannelInboundHandlerAdapter in2 = new ChannelInboundHandlerAdapter(){
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("2");
        super.channelRead(ctx, msg);
    }
};
// 出站处理器 out1
ChannelOutboundHandlerAdapter out1 = new ChannelOutboundHandlerAdapter(){
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("3");
        super.write(ctx, msg, promise);
    }
};
// 出站处理器 out2
ChannelOutboundHandlerAdapter out2 = new ChannelOutboundHandlerAdapter(){
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        log.info("4");
        super.write(ctx, msg, promise);
    }
};
EmbeddedChannel channel = new EmbeddedChannel(in1, in2, out1, out2);
// 模拟入站操作
// channel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes()));
// 模拟出站操作
channel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes()));
```

# <font style="color:#000000;background-color:#ffffff;">netty.buffer</font>
## ByteBuf
### 简介
`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);"> 是 Netty 框架中用于高效处理二进制数据的核心组件之一。它用于替代传统的 </font>`<font style="color:rgb(44, 44, 54);">byte[]</font>`<font style="color:rgb(44, 44, 54);"> 数组，提供了更好的内存管理机制和更高的性能。</font>`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);"> 的内部实现依赖于具体的 </font>`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);"> 实现类。Netty 提供了几种不同的 </font>`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);"> 实现，每一种都有其特点和适用场景。</font>

<font style="color:rgb(44, 44, 54);">主要的 ByteBuf 实现：</font>

1. **<font style="color:rgb(44, 44, 54);">HeapByteBuf</font>**<font style="color:rgb(44, 44, 54);">：</font>
    - <font style="color:rgb(44, 44, 54);">这是最常见的</font><font style="color:rgb(44, 44, 54);"> </font>`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);"> </font><font style="color:rgb(44, 44, 54);">实现之一，它基于 Java 堆内存中的数组（通常是</font><font style="color:rgb(44, 44, 54);"> </font>`<font style="color:rgb(44, 44, 54);">byte[]</font>`<font style="color:rgb(44, 44, 54);">）来存储数据。</font>
    - <font style="color:rgb(44, 44, 54);">它适用于大多数常规的应用场景，提供了较好的通用性和易用性。</font>
2. **<font style="color:rgb(44, 44, 54);">DirectByteBuf</font>**<font style="color:rgb(44, 44, 54);">：</font>
    - <font style="color:rgb(44, 44, 54);">这种实现使用直接内存（非堆内存）来存储数据。</font>
    - <font style="color:rgb(44, 44, 54);">直接内存由 JVM 分配，通常比堆内存更高效，尤其是在大量读写操作时，因为可以直接与操作系统交互，减少数据复制。</font>
    - <font style="color:rgb(44, 44, 54);">适用于需要高性能网络传输的场景。</font>
3. **<font style="color:rgb(44, 44, 54);">CompositeByteBuf</font>**<font style="color:rgb(44, 44, 54);">：</font>
    - <font style="color:rgb(44, 44, 54);">这种实现可以聚合多个</font><font style="color:rgb(44, 44, 54);"> </font>`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);"> </font><font style="color:rgb(44, 44, 54);">对象，使其看起来像是一个单一的</font><font style="color:rgb(44, 44, 54);"> </font>`<font style="color:rgb(44, 44, 54);">ByteBuf</font>`<font style="color:rgb(44, 44, 54);">。</font>
    - <font style="color:rgb(44, 44, 54);">适用于需要将多个数据片段合并成一个整体进行处理的场景。</font>

`<font style="color:rgb(44, 44, 54);">getInt</font>`<font style="color:rgb(44, 44, 54);"> 方法实际上是调用了 </font>`<font style="color:rgb(44, 44, 54);">getByte</font>`<font style="color:rgb(44, 44, 54);"> 方法多次，以读取四个连续的字节，并将它们组合成一个 32 位的整数。这个过程涉及字节序（endianness）的转换，即确定高位字节和低位字节的顺序。</font>

<font style="color:rgb(44, 44, 54);">ByteBuf优势</font>

+ <font style="color:rgb(44, 44, 54);">池化，可以重用池中ByteBuf实例，更节约内存，减少内存滋出的可能</font>
+ <font style="color:rgb(44, 44, 54);">读写指针分高，不需要像ByteBuf什er一样切晚读写模式</font>
+ <font style="color:rgb(44, 44, 54);">可以自动扩容</font>
+ <font style="color:rgb(44, 44, 54);">支持链式调用。使用更流畅</font>

<font style="color:rgb(44, 44, 54);">很多地方体现零拷贝，如slice、duplicate、CompositeByteBuf</font>

### 自定义的调试工具类
```java
import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

private static void log(ByteBuf buffer) {
    int length = buffer.readableBytes();
    int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;

    StringBuilder buf = new StringBuilder(rows * 80 * 2)
            .append("read index:").append(buffer.readerIndex())
            .append("write index:").append(buffer.writerIndex())
            .append("capacity:").append(buffer.capacity())
            .append(NEWLINE); //import static io.netty.util.internal.StringUtil.NEWLINE;
    appendPrettyHexDump(buf, buffer); //import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
    System.out.println(buf);
}
```

### 底层原理与剖析
#### 动态扩容
扩容规则是

+ 如何写入后数据大小未超过512，则选择下一个16的整数倍，例如写入后大小为12，则扩容后capacity是

16

+ 如果写入后数据大小超过512，则选择下一个2n，例如写入后大小为513，则扩容后capacity 2^10=1024(2^9=512已经不够了)
+ 扩容不能超过max capacity会报错

```java
ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();// 默认容量是256，可以动态扩容
StringBuilder str = new StringBuilder();
for (int i = 0; i < 257; i++) {
    str.append("a");
}
log(buffer);
buffer.writeBytes(str.toString().getBytes());
log(buffer);
```

```java
read index:0write index:0capacity:256

read index:0write index:257capacity:512
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000010| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000020| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000030| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000040| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000050| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000060| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000070| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000080| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000090| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|000000a0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|000000b0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|000000c0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|000000d0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|000000e0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|000000f0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
|00000100| 61                                              |a               |
+--------+-------------------------------------------------+----------------+
```

#### ByteBuf内存组成
ByteBuf由四个区域组成

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725441169585-a315dc33-1449-41e1-b85d-b9a87134703b.png)

最开始读写指针都在0位置，

1. 橙色部分是可扩容区域，每次需要时就扩容。
    - 绿色：读指针
    - 蓝色：写指针

> 相比NIO中的ByteBuffer，只有一个指针用来读写，每次需要flip()切换
>

2. 蓝色区域：可写区域
3. 绿色区域：可读区域
4. 灰色区域：废弃区域
+ 直接内存创建和销毁的代价昂贵，但读写性能高（少一次内存复制），适合配合池化功能一起用
+ 直接内存对GC压力小，因为这部分内存不受VM垃圾回收的管理，但也要注意及时主动释放

### 内存回收
#### 直接内存VS堆内存
由于Netty中有堆外内存的 ByteBuf 实现，堆外内存最好是手动来释放，而不是等GC垃圾回收。

+ UnpooledHeapByteBuf 使用的是JVM内存，只需等GC回收内存即可
+ UnpooledDirectByteBuf 使用的就是直接内存了，需要特殊的方法来回收内存
+ PooledByteBuf和它的子类使用了池化机制，需要更复杂的规则来回收内存

#### 堆内存回收
回收内存的源码实现，请关注下面方法的不同实现

```java
protected abstract void deallocate()
```

Netty 这里采用了引用计数法来控制回收内存，每个ByteBuf都实现了 ReferenceCounted 接口

+ 每个ByteBuf对象的初始计数为1
+ 调用release方法计数减1，如果计数为0，ByteBuf内存被回收
+ 调用retain  方法计数加1，表示调用者没用完之前，其它handler即使调用了release也不会造成回收
+ 当计数为 0 时，底层内存会被回收，这时即使ByteBuf 对象还在，其各个方法均无法正常使用

```java
ByteBuf buf = ...
try{
    
} finally{
    buf.release();
}
```

因为 pipeline 的存在，一般需要将 ByteBuf 传递给下一个ChannelHandler，如果在随便一个handler中的 finally 代码块下 release 了，就失去了传递性（当然，如果在这个ChannelHandler内这个ByteBuf已完成了它的使命，那么便无须再传递)

基本规则是，谁是最后使用者，谁负责release,详细分析如下

+ 起点，对于NlO实现来讲，在io.netty.channel.nio.AbstractNioByteChannel.NioByteUnsafe#read方法中首次创建ByteBuf 放入 pipeline(line163 pipeline.fireChannelRead(byte Buf))

##### a. 出站处理器释放 <font style="color:#000000;background-color:#ffffff;">TailContext</font>
```java
final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler
```

我们可以看到 

```java
// TailContext#channelRead --> 
// TailContext#onUnhandledInboundMessage(ctx, msg) ---> 
// TailContext#onUnhandledInboundMessage(msg)
// release()释放内存
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) {
    onUnhandledInboundMessage(ctx, msg);
}
protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
    onUnhandledInboundMessage(msg);
    if (logger.isDebugEnabled()) {
        logger.debug("Discarded message pipeline : {}. Channel : {}.",
                     ctx.pipeline().names(), ctx.channel());
    }
}

protected void onUnhandledInboundMessage(Object msg) {
    try {
        logger.debug(
                "Discarded inbound message {} that reached at the tail of the pipeline. " +
                        "Please check your pipeline configuration.", msg);
    } finally {
        ReferenceCountUtil.release(msg);
    }
}

public static boolean release(Object msg) {
    if (msg instanceof ReferenceCounted) { // 因为ByteBuf 实现了 ReferenceCounted 接口
        return ((ReferenceCounted) msg).release();
    }
    return false;
}
```

##### b. 入站处理器释放
```java
final class HeadContext extends AbstractChannelHandlerContext
            implements ChannelOutboundHandler, ChannelInboundHandler {}
```

```java
@Override
public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    unsafe.write(msg, promise); // 找到AbstractChannel实现
}
//等待分析.....，好长啊，其中有释放内的地方，具体逻辑等多学习一些再查看吧
```



### 池化管理
池化的最大意义在于可以重用ByteBuf，优点有

+ 没有池化，则每次都得创建新的ByteBuf实例，这个操作对直接内存代价昂贵，就算是堆内存，也会增加GC压力
+ 有了池化，则可以重用池中ByteBuf实例，并且采用了与jemalloc类似的内存分配算法提升分配效率
+ 高并发时，池化功能更节约内存，减少内存溢出的可能

池化功能是否开启，可以通过下面的系统环境变量来设置

```java
-Dio.netty.allocator.type={unpooledl|pooled}
```

+ 4.1以后，非Android平台默认启用池化实现，Android平台启用非池化实现
+ 4.1之前，池化功能还不成熟，默认是非池化实现

#### 验证
```java
ByteBuf headBuf = ByteBufAllocator.DEFAULT.heapBuffer(10);
ByteBuf directBuf = ByteBufAllocator.DEFAULT.directBuffer(10);

System.out.println(headBuf.getClass());
System.out.println(directBuf.getClass());
```

运行结果

```java
class io.netty.buffer.PooledUnsafeHeapByteBuf
class io.netty.buffer.PooledUnsafeDirectByteBuf
```

### 常见方法
#### 写入方法
| 方法签名 | 含义 | 备注 |
| --- | --- | --- |
| writeBoolean(boolean value) | 写入boolean 值 | 用一字节01|00代表true|false |
| writeByte(intvalue) | 写入byte值 | |
| writeShort(int value) | 写入short值 | |
| writelnt(int value) | 写入int值 | Big Endian，即0x250，写入后 00 00 02 50 |
| writelntLE(int value) | 写入int值 | Little Endian，即0x250，写入后 50 02 00 00 |
| writeLong(long value) | 写入long值 | |
| writeChar(int value) | 写入char值 | |
| writeFloat(float value) | 写入float值 | |
| writeDouble(double value) 牛 | 写入double值 | |
| writeBytes(ByteBuf src) | 写入netty的 ByteBuf |  |
| writeBytes(byte src) | 写入byte |  |
| writeBytes(ByteBuffer src) | 与人nio中的 ByteBuffer |  |
| int writeCharSequence(CharSequence sequence,Charset charset) | 写入字符串 |  |


注意

+ 这些方法的未指明返回值的，其返回值都是ByteBuf，意味着可以链式调用
+ 网络传输，默认习惯是Big Endian

##### 实践
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
buf.writeInt(2);
log(buf);
```

结果

```java
read index:0write index:4capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 02                                     |....            |
+--------+-------------------------------------------------+----------------+
```

#### 读取方法
##### a. readByte()
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
buf.writeInt(2);
log(buf);
byte b1 = buf.readByte();
byte b2 = buf.readByte();
byte b3 = buf.readByte();
byte b4 = buf.readByte();
log(buf);
System.out.println(b1);
System.out.println(b2);
System.out.println(b3);
System.out.println(b4);
```

结果

```java
read index:0write index:4capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 02                                     |....            |
+--------+-------------------------------------------------+----------------+
read index:4write index:4capacity:256

0
0
0
2
```

读取后我们的index变为了4，每次调用readByte()，index就加1

##### b.readInt()
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
buf.writeInt(2);
log(buf);
int i = buf.readInt();
log(buf);
System.out.println(i);
```

运行结果

```java
read index:0write index:4capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 02                                     |....            |
+--------+-------------------------------------------------+----------------+
read index:4write index:4capacity:256

2
```

我们调用readInt()，其中index+4。刚好和int的字节长度相匹配



##### 重复读
###### 方式一 mark+rest
```java
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
buf.writeInt(2);

buf.markReaderIndex();
System.out.println(buf.readInt());
log(buf);
buf.resetReaderIndex();

log(buf);
```

运行结果

```java
2
read index:4write index:4capacity:256

read index:0write index:4capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 02                                     |....            |
+--------+-------------------------------------------------+----------------+
```

###### 方式二 get
get方法不会改变index指针的位置

#### 零拷贝
##### slice() 将大的切小
netty中零拷贝的体现之一，减少在Java层面的数据复制

> Netty中的零拷贝指的不是在操作系统中将文件传输给网络socket减少文件传输次数
>

对原始 ByteBuf 进行切片成多个ByteBuf，切片后的 ByteBuf 并没有发生内存复制，还是使用原始ByteBuf的内存，切片后的ByteBuf 维护独立的read,write指针

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725447060484-67c4f11c-842c-45b4-890c-90b359804857.png)

```java
ByteBuf oldBuf = ByteBufAllocator.DEFAULT.buffer(4);
oldBuf.writeBytes("abcd".getBytes());

System.out.println("旧数据----------");
ByteBuf sliceBuf1 = oldBuf.slice(0, 2);
ByteBuf sliceBuf2 = oldBuf.slice(2, 2);
log(oldBuf);
log(sliceBuf1);
log(sliceBuf2);

System.out.println("新数据----------");
//在切片过程中不会发生数据复制,所以当我们修改其中一个时，其它的也会改变
oldBuf.setByte(0, 'z');//将第一个替换为'z'
log(oldBuf);
log(sliceBuf1);
log(sliceBuf2);
```

```java
旧数据----------
read index:0write index:4capacity:4
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64                                     |abcd            |
+--------+-------------------------------------------------+----------------+
read index:0write index:2capacity:2
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62                                           |ab              |
+--------+-------------------------------------------------+----------------+
read index:0write index:2capacity:2
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 64                                           |cd              |
+--------+-------------------------------------------------+----------------+
新数据----------
read index:0write index:4capacity:4
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7a 62 63 64                                     |zbcd            |
+--------+-------------------------------------------------+----------------+
read index:0write index:2capacity:2
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7a 62                                           |zb              |
+--------+-------------------------------------------------+----------------+
read index:0write index:2capacity:2
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 64                                           |cd              |
+--------+-------------------------------------------------+----------------+
```

###### a. 注意1：切片后数组无法添加新内容
**<font style="color:#DF2A3F;">我们切片后的buf数组，是无法添加新内容的，当我们调用sliceBuf1.writeBytes()方法时会报错</font>**

+ <font style="color:#262626;">为什么呢？</font>
+ <font style="color:#262626;">因为：sliceBuf 和原有的 buf 是共享内存的，当我们追加更多的数据时，会影响到原有的 buf，所以开发者做了对 sliceBuf 的最大容量限制是无法扩容的</font>

```java
ByteBuf oldBuf = ByteBufAllocator.DEFAULT.buffer(4);
oldBuf.writeBytes("abcd".getBytes());
ByteBuf sliceBuf = oldBuf.slice(0, 2);
sliceBuf.writeByte('e');
```

运行结果

```java
Exception in thread "main" java.lang.IndexOutOfBoundsException: writerIndex(2) + minWritableBytes(1) exceeds maxCapacity(2): UnpooledSlicedByteBuf(ridx: 0, widx: 2, cap: 2/2, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 4, cap: 4))
	at io.netty.buffer.AbstractByteBuf.ensureWritable0(AbstractByteBuf.java:294)
	at io.netty.buffer.AbstractByteBuf.writeByte(AbstractByteBuf.java:984)
	at com.setty.d7.ByteBufTest4.main(ByteBufTest4.java:11)
```

###### b. 注意2：对原有buf进行release，影响新的 buf
**<font style="color:#DF2A3F;">如果我们对原有的 buf 进行release操作，会影响切片后的buf，当我们想操作切片后的buf时也会抛出异常</font>**

```java
ByteBuf oldBuf = ByteBufAllocator.DEFAULT.buffer(4);
oldBuf.writeBytes("abcd".getBytes());
ByteBuf sliceBuf = oldBuf.slice(0, 2);

oldBuf.release();
sliceBuf.setByte(0, 'm');
```

运行结果

```java
Exception in thread "main" io.netty.util.IllegalReferenceCountException: refCnt: 0
	at io.netty.buffer.AbstractByteBuf.ensureAccessible(AbstractByteBuf.java:1454)
	at io.netty.buffer.AbstractByteBuf.checkIndex(AbstractByteBuf.java:1383)
	at io.netty.buffer.AbstractByteBuf.checkIndex(AbstractByteBuf.java:1379)
	at io.netty.buffer.AbstractByteBuf.setByte(AbstractByteBuf.java:525)
	at io.netty.buffer.AbstractUnpooledSlicedByteBuf.setByte(AbstractUnpooledSlicedByteBuf.java:257)
	at com.setty.d7.ByteBufTest4.main(ByteBufTest4.java:13)
```

如何解决这个问题呢？只需要在切片后让 refCount + 1即可

```java
ByteBuf oldBuf = ByteBufAllocator.DEFAULT.buffer(4);
oldBuf.writeBytes("abcd".getBytes());
ByteBuf sliceBuf = oldBuf.slice(0, 2);

sliceBuf.retain();
oldBuf.release();
sliceBuf.setByte(0, 'm');
```

****

##### duplicate() 整体拷贝
零拷贝的体现之一，就好比截取了原始ByteBuf所有内容，并且没有max capacity的限制，也是与原始

ByteBuf使用同一块底层内存，只是读写指针是独立的。

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725448711057-7f050be1-49c7-4f44-b1e5-2f83f78ec9e8.png)

##### composite() 将小合并为大
```java
ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(2);
ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(2);
buf1.writeBytes("ab".getBytes());
buf2.writeBytes("cd".getBytes());

CompositeByteBuf compositeBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
//compositeBuf如果想要自动增长读写指针，需要传入true参数
compositeBuf.addComponent(true, buf1);
compositeBuf.addComponent(true, buf2);

log(compositeBuf);
```

运行结果

```java
read index:0write index:4capacity:4
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64                                     |abcd            |
+--------+-------------------------------------------------+----------------+
```

#### copy()方法深拷贝
会将底层内存数据进行深拷贝，因此无论读写，都与原始ByteBuf无关

## <font style="color:#000000;background-color:#ffffff;">ByteBufAllocator</font>
# <font style="color:#000000;background-color:#ffffff;">netty.util</font>
## <font style="color:#000000;background-color:#ffffff;">AttributeKey</font>
# <font style="color:#000000;background-color:#ffffff;">netty.handler</font>
## <font style="color:#ED740C;background-color:#ffffff;">handler.ssl</font>
```java
@Slf4j
public class HttpsServer {
    public static void main(String[] args) throws InterruptedException, URISyntaxException, CertificateException, SSLException {

        /*
            java HttpServer localhost 8080
            java -jar xxx.har localhost 8080
            idea 中 program arguments 输入 localhost 8080

            # 生成私钥
            openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem
            # 生成csr文件
            openssl req -new -key private.pem -out cert.csr
            # 生成自签名证书
            openssl x509 -req -days 700 -in cert.csr -sha256 -signkey private.pem -extfile <(printf "subjectAltName=DNS:ssdlhw.com,IP:192.168.56.1") -out cert.crt

            [TLSv1.3, TLSv1.2, TLSv1.1, TLSv1, SSLv3, SSLv2Hello]
        */

        /*
            ---------------------------------------------------------------------------------------------------
            |
            |   待解决的问题，postman访问没有出错，但是浏览器访问出现
            |   javax.net.ssl.SSLHandshakeException: Received fatal alert: certificate_unknown
            |
            |   解决了，首选在操作系统host文件中添加域名映射
            |   其次根据添加的域名生成证书
            |   再者根据在浏览器受信任的根证书区域添加证书
            ---------------------------------------------------------------------------------------------------
        */

        String workDir = System.getProperty("user.dir");
        log.info("workDir--->{}", workDir);

        //私钥
        Path certKeyPath = Paths.get(workDir + File.separator + "cert-key.pem");
        //证书
        Path certPath = Paths.get(workDir + File.separator + "cert.pem");

        if (!Files.exists(certKeyPath)) {
            log.info("not exists--->" + certKeyPath);
            System.exit(1);
        }
        log.info("exists--->" + certKeyPath);
        if (!Files.exists(certPath)) {
            log.info("not exists--->" + certPath);
            System.exit(1);
        }
        log.info("exists--->" + certPath);


        SslContext sslCtx = SslContextBuilder
                .forServer(certPath.toFile(), certKeyPath.toFile())
                .protocols("TLSv1.2")
                .ciphers(Collections.singleton("TLS_RSA_WITH_AES_128_CBC_SHA")) //选择此加密套件是为了能够使得服务端解密
                .build();
        
        SslHandler sslHandler = sslCtx.newHandler(ByteBufAllocator.DEFAULT);
        SSLEngine engine = sslHandler.engine();
        log.info("server support Protocols(加密协议)--->" + Arrays.toString(engine.getSupportedProtocols()));
        log.info("server support CipherSuites(加密套件)--->");
        System.out.println(Arrays.toString(engine.getSupportedCipherSuites()));

        startServer(args, sslCtx);
    }

    private static void startServer(String[] args, SslContext sslCtx) throws InterruptedException {
        log.info("{}", args[0]);
        log.info("{}", args[1]);
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                //.addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(sslCtx.newHandler(ch.alloc()))
                                //.addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new HttpServerCodec())
                                .addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                        log.info("client arrive");
                                        DefaultFullHttpResponse response
                                                = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                        byte[] bytes = "https server".getBytes();
                                        // 浏览器在不知道返回长度的时候会一直转圈等待加载
                                        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                                        response.content().writeBytes(bytes);
                                        ctx.writeAndFlush(response);
                                    }
                                });

                    }
                })
                .bind(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
        channelFuture.sync();
        log.info("server start ...");
    }
}
```

## <font style="color:#ED740C;background-color:#ffffff;">handler.codec</font>
解码器作用是处理byteBuf中的数据

**常见的基本解码器如下（按照长度，换行符，自定义字符等bytebuf）**

+ FixedLengthFrameDecoder：按照固定长度来处理接收到的 bytebuf
+ LineBasedFrameDecoder，按照行分割符来处理接收到的 bytebuf
+ <font style="background-color:#ffffff;">DelimiterBasedFrameDecoder</font>
+ <font style="background-color:#ffffff;">LengthFieldBasedFrameDecoder</font>

**<font style="background-color:#ffffff;">常见的协议解码器（按照一定的格式处理bytebuf）</font>**

+ <font style="background-color:#ffffff;">HttpServerCode：把 bytebuf 中的数据按照 http 协议的格式进行处理</font>

### <font style="background-color:#ffffff;">string</font>
#### <font style="background-color:#ffffff;">StringEncoder&StringDecoder(编码解码)</font>
### 按照协议处理bytebuf
#### 使用redis协议，向其发送命令
自己实现版

```java
/*
    redis协议
        set name zhangSan

        *3(代表set，name，zhangSan三个参数)
        $3(代表接下来三个字节)
        set
        $4(代表接下来4个字符)
        name
        $8(代表接下来8个字符)
        zhangSan

*/
ChannelFuture channelFuture = new Bootstrap()
        .group(new NioEventLoopGroup())
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            }
        })
        .connect(new InetSocketAddress("192.168.75.128", 6379));
channelFuture.sync();
log.info("redis连接成功");

Channel redisClient = channelFuture.channel();

ByteBuf authPassBuf = redisClient.alloc().buffer();
// 验证密码
final String LINE = "\r\n";
String authPass =
        "*2" + LINE +
        "$4" + LINE + "auth" + LINE +
        "$4" + LINE + "root" + LINE;
authPassBuf.writeBytes(authPass.getBytes());
redisClient.writeAndFlush(authPassBuf).sync();

//原来的byteBuf已经被垃圾回收
ByteBuf setValueBuf = redisClient.alloc().buffer();
// set name zhangSan
StringBuilder setValue = new StringBuilder();
setValue.append("*3").append(LINE); //接下来有3个参数
setValue.append("$3").append(LINE).append("set").append(LINE);
setValue.append("$4").append(LINE).append("name").append(LINE);
setValue.append("$8").append(LINE).append("zhangWuu").append(LINE);
setValueBuf.writeBytes(setValue.toString().getBytes());
redisClient.writeAndFlush(setValueBuf);

redisClient.closeFuture().sync();
```

运行结果

```java
2024-09-06 18:49:43.180 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] ACTIVE
2024-09-06 18:49:43.180 [main] [INFO ] - redis连接成功
2024-09-06 18:49:43.207 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] WRITE: 24B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 2a 32 0d 0a 24 34 0d 0a 61 75 74 68 0d 0a 24 34 |*2..$4..auth..$4|
|00000010| 0d 0a 72 6f 6f 74 0d 0a                         |..root..        |
+--------+-------------------------------------------------+----------------+
2024-09-06 18:49:43.208 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] FLUSH
2024-09-06 18:49:43.218 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] READ: 5B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 2b 4f 4b 0d 0a                                  |+OK..           |
+--------+-------------------------------------------------+----------------+
2024-09-06 18:49:43.219 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] READ COMPLETE
2024-09-06 18:49:43.219 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] WRITE: 37B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 2a 33 0d 0a 24 33 0d 0a 73 65 74 0d 0a 24 34 0d |*3..$3..set..$4.|
|00000010| 0a 6e 61 6d 65 0d 0a 24 38 0d 0a 7a 68 61 6e 67 |.name..$8..zhang|
|00000020| 57 75 75 0d 0a                                  |Wuu..           |
+--------+-------------------------------------------------+----------------+
2024-09-06 18:49:43.219 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] FLUSH
2024-09-06 18:49:43.221 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] READ: 5B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 2b 4f 4b 0d 0a                                  |+OK..           |
+--------+-------------------------------------------------+----------------+
2024-09-06 18:49:43.221 [nioEventLoopGroup-2-1] [DEBUG] - [id: 0x73184ba9, L:/192.168.75.1:5331 - R:/192.168.75.128:6379] READ COMPLETE
```

#### <font style="color:#A58F04;background-color:#ffffff;">codec.http</font>
##### <font style="color:#74B602;">a. 简单使用</font>
```java
new ServerBootstrap()
        .group(new NioEventLoopGroup(1), new NioEventLoopGroup(4))
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new LoggingHandler(LogLevel.DEBUG))
                        .addLast(new HttpServerCodec());
                //处理请求头 + 请求行
                ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                        System.out.println("处理请求头+请求行");
                        System.out.println(msg.uri());
                        DefaultFullHttpResponse response
                                = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                        byte[] bytes = "<h1>hello</h1>".getBytes();
                        // 浏览器在不知道返回长度的时候会一直转圈等待加载
                        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                        response.content().writeBytes(bytes);
                        ctx.writeAndFlush(response);
                    }
                });
                //处理请求体
                ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpContent>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
                    }
                });

            }
        })
        .bind(8080);
```

在浏览器中访问 localhost:8080，我们可以发现浏览器请求了两次

+ 一次是localhost:8080/
+ 一次是localhost:8080/favicon.ico

```java
2024-09-06 21:57:54.376 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] READ: 733B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 47 45 54 20 2f 20 48 54 54 50 2f 31 2e 31 0d 0a |GET / HTTP/1.1..|
|00000010| 48 6f 73 74 3a 20 6c 6f 63 61 6c 68 6f 73 74 3a |Host: localhost:|
|00000020| 38 30 38 30 0d 0a 43 6f 6e 6e 65 63 74 69 6f 6e |8080..Connection|
|00000030| 3a 20 6b 65 65 70 2d 61 6c 69 76 65 0d 0a 73 65 |: keep-alive..se|
|00000040| 63 2d 63 68 2d 75 61 3a 20 22 43 68 72 6f 6d 69 |c-ch-ua: "Chromi|
|00000050| 75 6d 22 3b 76 3d 22 31 32 38 22 2c 20 22 4e 6f |um";v="128", "No|
|00000060| 74 3b 41 3d 42 72 61 6e 64 22 3b 76 3d 22 32 34 |t;A=Brand";v="24|
|00000070| 22 2c 20 22 47 6f 6f 67 6c 65 20 43 68 72 6f 6d |", "Google Chrom|
|00000080| 65 22 3b 76 3d 22 31 32 38 22 0d 0a 73 65 63 2d |e";v="128"..sec-|
|00000090| 63 68 2d 75 61 2d 6d 6f 62 69 6c 65 3a 20 3f 30 |ch-ua-mobile: ?0|
|000000a0| 0d 0a 73 65 63 2d 63 68 2d 75 61 2d 70 6c 61 74 |..sec-ch-ua-plat|
|000000b0| 66 6f 72 6d 3a 20 22 57 69 6e 64 6f 77 73 22 0d |form: "Windows".|
|000000c0| 0a 55 70 67 72 61 64 65 2d 49 6e 73 65 63 75 72 |.Upgrade-Insecur|
|000000d0| 65 2d 52 65 71 75 65 73 74 73 3a 20 31 0d 0a 55 |e-Requests: 1..U|
|000000e0| 73 65 72 2d 41 67 65 6e 74 3a 20 4d 6f 7a 69 6c |ser-Agent: Mozil|
|000000f0| 6c 61 2f 35 2e 30 20 28 57 69 6e 64 6f 77 73 20 |la/5.0 (Windows |
|00000100| 4e 54 20 31 30 2e 30 3b 20 57 69 6e 36 34 3b 20 |NT 10.0; Win64; |
|00000110| 78 36 34 29 20 41 70 70 6c 65 57 65 62 4b 69 74 |x64) AppleWebKit|
|00000120| 2f 35 33 37 2e 33 36 20 28 4b 48 54 4d 4c 2c 20 |/537.36 (KHTML, |
|00000130| 6c 69 6b 65 20 47 65 63 6b 6f 29 20 43 68 72 6f |like Gecko) Chro|
|00000140| 6d 65 2f 31 32 38 2e 30 2e 30 2e 30 20 53 61 66 |me/128.0.0.0 Saf|
|00000150| 61 72 69 2f 35 33 37 2e 33 36 0d 0a 41 63 63 65 |ari/537.36..Acce|
|00000160| 70 74 3a 20 74 65 78 74 2f 68 74 6d 6c 2c 61 70 |pt: text/html,ap|
|00000170| 70 6c 69 63 61 74 69 6f 6e 2f 78 68 74 6d 6c 2b |plication/xhtml+|
|00000180| 78 6d 6c 2c 61 70 70 6c 69 63 61 74 69 6f 6e 2f |xml,application/|
|00000190| 78 6d 6c 3b 71 3d 30 2e 39 2c 69 6d 61 67 65 2f |xml;q=0.9,image/|
|000001a0| 61 76 69 66 2c 69 6d 61 67 65 2f 77 65 62 70 2c |avif,image/webp,|
|000001b0| 69 6d 61 67 65 2f 61 70 6e 67 2c 2a 2f 2a 3b 71 |image/apng,*/*;q|
|000001c0| 3d 30 2e 38 2c 61 70 70 6c 69 63 61 74 69 6f 6e |=0.8,application|
|000001d0| 2f 73 69 67 6e 65 64 2d 65 78 63 68 61 6e 67 65 |/signed-exchange|
|000001e0| 3b 76 3d 62 33 3b 71 3d 30 2e 37 0d 0a 53 65 63 |;v=b3;q=0.7..Sec|
|000001f0| 2d 46 65 74 63 68 2d 53 69 74 65 3a 20 6e 6f 6e |-Fetch-Site: non|
|00000200| 65 0d 0a 53 65 63 2d 46 65 74 63 68 2d 4d 6f 64 |e..Sec-Fetch-Mod|
|00000210| 65 3a 20 6e 61 76 69 67 61 74 65 0d 0a 53 65 63 |e: navigate..Sec|
|00000220| 2d 46 65 74 63 68 2d 55 73 65 72 3a 20 3f 31 0d |-Fetch-User: ?1.|
|00000230| 0a 53 65 63 2d 46 65 74 63 68 2d 44 65 73 74 3a |.Sec-Fetch-Dest:|
|00000240| 20 64 6f 63 75 6d 65 6e 74 0d 0a 41 63 63 65 70 | document..Accep|
|00000250| 74 2d 45 6e 63 6f 64 69 6e 67 3a 20 67 7a 69 70 |t-Encoding: gzip|
|00000260| 2c 20 64 65 66 6c 61 74 65 2c 20 62 72 2c 20 7a |, deflate, br, z|
|00000270| 73 74 64 0d 0a 41 63 63 65 70 74 2d 4c 61 6e 67 |std..Accept-Lang|
|00000280| 75 61 67 65 3a 20 7a 68 2d 43 4e 2c 7a 68 3b 71 |uage: zh-CN,zh;q|
|00000290| 3d 30 2e 39 2c 65 6e 3b 71 3d 30 2e 38 0d 0a 43 |=0.9,en;q=0.8..C|
|000002a0| 6f 6f 6b 69 65 3a 20 49 64 65 61 2d 37 35 31 32 |ookie: Idea-7512|
|000002b0| 38 61 61 66 3d 36 38 65 63 32 32 31 35 2d 65 61 |8aaf=68ec2215-ea|
|000002c0| 61 66 2d 34 64 61 36 2d 61 34 62 63 2d 36 34 61 |af-4da6-a4bc-64a|
|000002d0| 32 39 38 33 61 30 39 33 31 0d 0a 0d 0a          |2983a0931....   |
+--------+-------------------------------------------------+----------------+
处理请求头+请求行
/
2024-09-06 21:57:54.411 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] WRITE: 53B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 54 54 50 2f 31 2e 31 20 32 30 30 20 4f 4b 0d |HTTP/1.1 200 OK.|
|00000010| 0a 63 6f 6e 74 65 6e 74 2d 6c 65 6e 67 74 68 3a |.content-length:|
|00000020| 20 31 34 0d 0a 0d 0a 3c 68 31 3e 68 65 6c 6c 6f | 14....<h1>hello|
|00000030| 3c 2f 68 31 3e                                  |</h1>           |
+--------+-------------------------------------------------+----------------+
2024-09-06 21:57:54.413 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] FLUSH
2024-09-06 21:57:54.414 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] READ COMPLETE
2024-09-06 21:57:54.471 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] READ: 659B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 47 45 54 20 2f 66 61 76 69 63 6f 6e 2e 69 63 6f |GET /favicon.ico|
|00000010| 20 48 54 54 50 2f 31 2e 31 0d 0a 48 6f 73 74 3a | HTTP/1.1..Host:|
|00000020| 20 6c 6f 63 61 6c 68 6f 73 74 3a 38 30 38 30 0d | localhost:8080.|
|00000030| 0a 43 6f 6e 6e 65 63 74 69 6f 6e 3a 20 6b 65 65 |.Connection: kee|
|00000040| 70 2d 61 6c 69 76 65 0d 0a 73 65 63 2d 63 68 2d |p-alive..sec-ch-|
|00000050| 75 61 3a 20 22 43 68 72 6f 6d 69 75 6d 22 3b 76 |ua: "Chromium";v|
|00000060| 3d 22 31 32 38 22 2c 20 22 4e 6f 74 3b 41 3d 42 |="128", "Not;A=B|
|00000070| 72 61 6e 64 22 3b 76 3d 22 32 34 22 2c 20 22 47 |rand";v="24", "G|
|00000080| 6f 6f 67 6c 65 20 43 68 72 6f 6d 65 22 3b 76 3d |oogle Chrome";v=|
|00000090| 22 31 32 38 22 0d 0a 73 65 63 2d 63 68 2d 75 61 |"128"..sec-ch-ua|
|000000a0| 2d 6d 6f 62 69 6c 65 3a 20 3f 30 0d 0a 55 73 65 |-mobile: ?0..Use|
|000000b0| 72 2d 41 67 65 6e 74 3a 20 4d 6f 7a 69 6c 6c 61 |r-Agent: Mozilla|
|000000c0| 2f 35 2e 30 20 28 57 69 6e 64 6f 77 73 20 4e 54 |/5.0 (Windows NT|
|000000d0| 20 31 30 2e 30 3b 20 57 69 6e 36 34 3b 20 78 36 | 10.0; Win64; x6|
|000000e0| 34 29 20 41 70 70 6c 65 57 65 62 4b 69 74 2f 35 |4) AppleWebKit/5|
|000000f0| 33 37 2e 33 36 20 28 4b 48 54 4d 4c 2c 20 6c 69 |37.36 (KHTML, li|
|00000100| 6b 65 20 47 65 63 6b 6f 29 20 43 68 72 6f 6d 65 |ke Gecko) Chrome|
|00000110| 2f 31 32 38 2e 30 2e 30 2e 30 20 53 61 66 61 72 |/128.0.0.0 Safar|
|00000120| 69 2f 35 33 37 2e 33 36 0d 0a 73 65 63 2d 63 68 |i/537.36..sec-ch|
|00000130| 2d 75 61 2d 70 6c 61 74 66 6f 72 6d 3a 20 22 57 |-ua-platform: "W|
|00000140| 69 6e 64 6f 77 73 22 0d 0a 41 63 63 65 70 74 3a |indows"..Accept:|
|00000150| 20 69 6d 61 67 65 2f 61 76 69 66 2c 69 6d 61 67 | image/avif,imag|
|00000160| 65 2f 77 65 62 70 2c 69 6d 61 67 65 2f 61 70 6e |e/webp,image/apn|
|00000170| 67 2c 69 6d 61 67 65 2f 73 76 67 2b 78 6d 6c 2c |g,image/svg+xml,|
|00000180| 69 6d 61 67 65 2f 2a 2c 2a 2f 2a 3b 71 3d 30 2e |image/*,*/*;q=0.|
|00000190| 38 0d 0a 53 65 63 2d 46 65 74 63 68 2d 53 69 74 |8..Sec-Fetch-Sit|
|000001a0| 65 3a 20 73 61 6d 65 2d 6f 72 69 67 69 6e 0d 0a |e: same-origin..|
|000001b0| 53 65 63 2d 46 65 74 63 68 2d 4d 6f 64 65 3a 20 |Sec-Fetch-Mode: |
|000001c0| 6e 6f 2d 63 6f 72 73 0d 0a 53 65 63 2d 46 65 74 |no-cors..Sec-Fet|
|000001d0| 63 68 2d 44 65 73 74 3a 20 69 6d 61 67 65 0d 0a |ch-Dest: image..|
|000001e0| 52 65 66 65 72 65 72 3a 20 68 74 74 70 3a 2f 2f |Referer: http://|
|000001f0| 6c 6f 63 61 6c 68 6f 73 74 3a 38 30 38 30 2f 0d |localhost:8080/.|
|00000200| 0a 41 63 63 65 70 74 2d 45 6e 63 6f 64 69 6e 67 |.Accept-Encoding|
|00000210| 3a 20 67 7a 69 70 2c 20 64 65 66 6c 61 74 65 2c |: gzip, deflate,|
|00000220| 20 62 72 2c 20 7a 73 74 64 0d 0a 41 63 63 65 70 | br, zstd..Accep|
|00000230| 74 2d 4c 61 6e 67 75 61 67 65 3a 20 7a 68 2d 43 |t-Language: zh-C|
|00000240| 4e 2c 7a 68 3b 71 3d 30 2e 39 2c 65 6e 3b 71 3d |N,zh;q=0.9,en;q=|
|00000250| 30 2e 38 0d 0a 43 6f 6f 6b 69 65 3a 20 49 64 65 |0.8..Cookie: Ide|
|00000260| 61 2d 37 35 31 32 38 61 61 66 3d 36 38 65 63 32 |a-75128aaf=68ec2|
|00000270| 32 31 35 2d 65 61 61 66 2d 34 64 61 36 2d 61 34 |215-eaaf-4da6-a4|
|00000280| 62 63 2d 36 34 61 32 39 38 33 61 30 39 33 31 0d |bc-64a2983a0931.|
|00000290| 0a 0d 0a                                        |...             |
+--------+-------------------------------------------------+----------------+
处理请求头+请求行
/favicon.ico
2024-09-06 21:57:54.472 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] WRITE: 53B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 48 54 54 50 2f 31 2e 31 20 32 30 30 20 4f 4b 0d |HTTP/1.1 200 OK.|
|00000010| 0a 63 6f 6e 74 65 6e 74 2d 6c 65 6e 67 74 68 3a |.content-length:|
|00000020| 20 31 34 0d 0a 0d 0a 3c 68 31 3e 68 65 6c 6c 6f | 14....<h1>hello|
|00000030| 3c 2f 68 31 3e                                  |</h1>           |
+--------+-------------------------------------------------+----------------+
2024-09-06 21:57:54.473 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] FLUSH
2024-09-06 21:57:54.473 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x2fe5e487, L:/[0:0:0:0:0:0:0:1]:8080 - R:/[0:0:0:0:0:0:0:1]:25338] READ COMPLETE
```

##### <font style="color:#5C8D07;">b. </font><font style="color:#74B602;">HttpServerCodec 和 HttpObjectAggregator 作用</font>
模拟一次客户端发送一个很大的HTTP请求，客户端的请求如下：

1. 请求头部（通常是一个 HttpRequest 对象）

```java
POST /upload HTTP/1.1
Host: example.com
Content-Length: 50
```

2. 第一个数据块（HttpContent 对象）

```java
[DataChunk1]
```

3. 第二个数据块（HttpContent 对象）

```java
[DataChunk2]
```

4. 最后的数据块（LastHttpContent 对象）

```java
[DataChunk3]
```

处理流程：

1. 解码请求：**HttpServerCodec** 会解码 HTTP 请求的头部，并将其转换为 HttpRequest 对象。随后的数据块会被解码为 HttpContent 对象。
2. 聚合数据：**HttpObjectAggregator** 会接收这些 HttpContent 和 LastHttpContent 对象，并将它们合并成一个完整的 HttpRequest 对象。合并后的 HttpRequest 对象包含了完整的请求头部和请求体。

在经过 HttpObjectAggregator 之后，处理器会得到一个完整的 HttpRequest 对象，这个对象包含了完整的请求体（由多个 HttpContent 实例合并而成）。这样，后续的处理逻辑（比如业务逻辑处理）就可以直接操作这个完整的 HttpRequest 对象，而无需处理分块数据的合并细节。

这种方式使得处理 HTTP 请求变得更简单和一致，因为所有的消息部分已经被聚合成一个完整的对象，不再需要在后续处理过程中考虑如何合并多个数据块。

#### <font style="color:#A58F04;background-color:#ffffff;">websocketx</font>
##### a. 简单使用
```java
@Slf4j
public class Server {
    private static final EventLoopGroup boss = new NioEventLoopGroup(1);
    private static final EventLoopGroup worker = new NioEventLoopGroup();

    public static void main(String[] args) {
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
            .group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .handler(new ChannelInitializer<ServerSocketChannel>() {
                @Override
                protected void initChannel(ServerSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                }
            })
            .childHandler(
                new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline
                        .addLast(new LoggingHandler(LogLevel.DEBUG))
                        .addLast(new HttpServerCodec()) //对消息进行编解码
                        .addLast(new LoggingHandler(LogLevel.DEBUG))
                        .addLast(new WebSocketServerProtocolHandler("/ws"))
                        .addLast(new LoggingHandler(LogLevel.DEBUG))

                        .addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                super.channelRead(ctx, msg);
                            }
                        });


                    }
                }
            )
            .bind(new InetSocketAddress("192.168.75.128", 8895)); 
            channelFuture.sync();
            log.info("netty 启动成功");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("netty 启动关闭");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
```



##### 事件传递：HandshakeComplete
在前面处理器建立完连接之后，会向后续的处理器传递建立完成的事件

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1726148619988-861ee3d6-1bde-4fda-bf08-aac60084fc63.png)

## <font style="color:#ED740C;background-color:#ffffff;">handler.timeout</font>
### <font style="color:#000000;background-color:#ffffff;">IdleStateHandler</font>
[https://openatomworkshop.csdn.net/664edecbb12a9d168eb6fc1f.html](https://openatomworkshop.csdn.net/664edecbb12a9d168eb6fc1f.html)

## <font style="color:#ED740C;">netty黏包半包</font>
### netty黏包现象
客户端

```java
new Bootstrap()
        .group(new NioEventLoopGroup())
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        for (int i = 0; i < 5; i++) {
                            ByteBuf buf = ctx.alloc().buffer(5);
                            buf.writeBytes("abcde".getBytes());
                            ctx.writeAndFlush(buf);
                        }
                    }
                });
            }
        })
        .connect(new InetSocketAddress("localhost", 8080));
}
```

服务端

```java
new ServerBootstrap()
        .group(new NioEventLoopGroup())
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            }
        })
        .bind(8080);
```

服务端接受的消息

```java
2024-09-05 16:15:00.017 [nioEventLoopGroup-2-2] [DEBUG] - [id: 0x8afd94bc, L:/127.0.0.1:8080 - R:/127.0.0.1:20376] 
READ: 25B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 61 62 63 64 65 61 62 63 64 65 61 |abcdeabcdeabcdea|
|00000010| 62 63 64 65 61 62 63 64 65                      |bcdeabcde       |
+--------+-------------------------------------------------+----------------+
```

服务端将接受的消息合并到一起处理

### netty半包现象
客户端

```java
new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                for (int i = 0; i < 5; i++) {
                                    ByteBuf buf = ctx.alloc().buffer(5);
                                    buf.writeBytes("abcde".getBytes());
                                    ctx.writeAndFlush(buf);
                                }
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
```

服务端

```java
new ServerBootstrap()
        .group(new NioEventLoopGroup())
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_RCVBUF, 6)//将接受缓冲区大小设置为3个字节
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            }
        })
        .bind(8080);
```

服务端接收到的消息

```java
2024-09-05 16:43:07.497 [nioEventLoopGroup-2-2] [DEBUG] - [id: 0xb81d2279, L:/127.0.0.1:8080 - R:/127.0.0.1:21844] READ: 21B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 61 62 63 64 65 61 62 63 64 65 61 |abcdeabcdeabcdea|
|00000010| 62 63 64 65 61                                  |bcdea           |
+--------+-------------------------------------------------+----------------+
2024-09-05 16:43:07.498 [nioEventLoopGroup-2-2] [DEBUG] - [id: 0xb81d2279, L:/127.0.0.1:8080 - R:/127.0.0.1:21844] READ: 4B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 62 63 64 65                                     |bcde            |
+--------+-------------------------------------------------+----------------+
```

### 解决方案
#### 短链接
首先我们需要知道在客户端断开连接之后，服务端会读取到-1。我们可以利用这个特性，当客户端发送完全部数据后，就立马断开连接，此时服务端会把-1之前的数据都当做一个完整的数据来处理，此方法只能解决粘包问题

+ 服务端

```java
new ServerBootstrap()
        .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
        .channel(NioServerSocketChannel.class)
        //设置接受缓冲区byteBuf的容量
        .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16))
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            }
        })
        .bind(8080);

```

+ 客户端

```java
public static void main(String[] args) throws InterruptedException {

    for (int i = 0; i < 10; i++) {
        send();
    }
    System.out.println("发送完毕");
}

private static void send() {
    NioEventLoopGroup client = new NioEventLoopGroup();
    try{
        ChannelFuture channelFuture = new Bootstrap()
                .group(client)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            //建立连接触发的方法
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ByteBuf buf = ctx.alloc().buffer(5);
                                buf.writeBytes("123456789abcdef12".getBytes());
                                ctx.writeAndFlush(buf);
                                ctx.channel().close();
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        channelFuture.sync();
        channelFuture.channel().closeFuture().sync();

    } catch (InterruptedException e){
        log.error("{}", e.getMessage());
    } finally {
        client.shutdownGracefully();
    }
}
```

#### 定长解码器 FixedLengthFrameDecoder
固定消息的长度

缺点比较浪费空间

```java
EmbeddedChannel testChannel = new EmbeddedChannel(
        new FixedLengthFrameDecoder(10),
        new LoggingHandler(LogLevel.DEBUG)
);

Random random = new Random();
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
for (int i = 0; i < 5; i++) {
    StringBuilder toBytes = new StringBuilder();
    int randomNum = random.nextInt(0, 10);
    toBytes.append(String.valueOf(randomNum).repeat(randomNum));
    while (toBytes.length() < 10){
        toBytes.append("-");
    }
    buf.writeBytes(toBytes.toString().getBytes());
    System.out.println(toBytes);
}

testChannel.writeInbound(buf);
```

发送的消息结果

```java
2024-09-06 17:54:44.105 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] ACTIVE
7777777---
----------
4444------
1---------
7777777---
2024-09-06 17:54:44.147 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 37 37 37 37 37 37 37 2d 2d 2d                   |7777777---      |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:54:44.147 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 2d 2d 2d 2d 2d 2d 2d 2d 2d 2d                   |----------      |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:54:44.147 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 34 34 34 34 2d 2d 2d 2d 2d 2d                   |4444------      |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:54:44.147 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 31 2d 2d 2d 2d 2d 2d 2d 2d 2d                   |1---------      |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:54:44.147 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 37 37 37 37 37 37 37 2d 2d 2d                   |7777777---      |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:54:44.149 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ COMPLETE
```

+ 服务端

```java
new ServerBootstrap()
        .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            }
        })
        .bind(8080);
```

接受的消息结果

```java
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 38 38 38 38 38 38 38 38 2d 2d                   |88888888--      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 33 33 33 2d 2d 2d 2d 2d 2d 2d                   |333-------      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 39 39 39 39 39 39 39 39 39 2d                   |999999999-      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 39 39 39 39 39 39 39 39 39 2d                   |999999999-      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 35 35 35 35 35 2d 2d 2d 2d 2d                   |55555-----      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 39 39 39 39 39 39 39 39 39 2d                   |999999999-      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 37 37 37 37 37 37 37 2d 2d 2d                   |7777777---      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 32 32 2d 2d 2d 2d 2d 2d 2d 2d                   |22--------      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 37 37 37 37 37 37 37 2d 2d 2d                   |7777777---      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.407 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 39 39 39 39 39 39 39 39 39 2d                   |999999999-      |
+--------+-------------------------------------------------+----------------+
2024-09-06 16:41:44.408 [nioEventLoopGroup-3-1] [DEBUG] - [id: 0x877505f2, L:/127.0.0.1:8080 - R:/127.0.0.1:61103] READ COMPLETE

```

#### 行解码器 LineBasedFrameDecoder
用分割符来确定消息边界，以换行符LineBasedFrameDecoder来确定消息边界

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725612589431-65862e8c-f2bd-440c-b6ba-ec7b9553994d.png)

```java
EmbeddedChannel testChannel = new EmbeddedChannel(
        new LineBasedFrameDecoder(1024),
        new LoggingHandler(LogLevel.DEBUG)
);

Random random = new Random();
ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
for (int i = 0; i < 5; i++) {
    StringBuilder toBytes = new StringBuilder();
    int randomNum = random.nextInt(0, 10) + 1;
    toBytes.append(String.valueOf(randomNum).repeat(randomNum));
    toBytes.append("\n");
    buf.writeBytes(toBytes.toString().getBytes());
    System.out.print(toBytes);
}

testChannel.writeInbound(buf);
```

运行结果

```java
2024-09-06 17:45:22.073 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] ACTIVE
333
88888888
55555
666666
55555
2024-09-06 17:45:22.126 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 3B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 33 33 33                                        |333             |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:45:22.126 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 8B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 38 38 38 38 38 38 38 38                         |88888888        |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:45:22.127 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 5B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 35 35 35 35 35                                  |55555           |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:45:22.128 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 6B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 36 36 36 36 36 36                               |666666          |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:45:22.128 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 5B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 35 35 35 35 35                                  |55555           |
+--------+-------------------------------------------------+----------------+
2024-09-06 17:45:22.128 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ COMPLETE
```

#### <font style="background-color:#ffffff;">自定义分割符 DelimiterBasedFrameDecoder</font>
感觉有点多-，-

#### 基于长度的帧解码器 LengthFieldBasedFrameDecoder
<font style="color:#DF2A3F;">有点长，可以去源码看举的例子说明每一个参数的作用</font>

##### <font style="color:#DF2A3F;">a. 正常的处理</font>
```java

EmbeddedChannel testChannel = new EmbeddedChannel(
        //这个handler用于打印最原始的数据
        new LoggingHandler(),
        //最多接收1024个字节，[0,4)字节为需要读取的长度，如果读取到的长度不够是不会交给下一个handler的
        new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
        //这个handler用于打印经过ltc解码器处理的数据
        new LoggingHandler()
);

ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
byte[] message = "hello,world,this,is,netty".getBytes();
buf.writeInt(message.length);
buf.writeBytes(message);
testChannel.writeInbound(buf);

```

+ <font style="color:#DF2A3F;">结果，我们可以看到在没有发生黏包和半包的情况下，数据处理完整</font>

```java
2024-09-08 03:42:55.768 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 29B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 19 68 65 6c 6c 6f 2c 77 6f 72 6c 64 2c |....hello,world,|
|00000010| 74 68 69 73 2c 69 73 2c 6e 65 74 74 79          |this,is,netty   |
+--------+-------------------------------------------------+----------------+
2024-09-08 03:42:55.782 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 29B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 19 68 65 6c 6c 6f 2c 77 6f 72 6c 64 2c |....hello,world,|
|00000010| 74 68 69 73 2c 69 73 2c 6e 65 74 74 79          |this,is,netty   |
+--------+-------------------------------------------------+----------------+
```

##### <font style="color:#DF2A3F;">b. 模拟半包</font>
```java
EmbeddedChannel testChannel = new EmbeddedChannel(
        //这个handler用于打印最原始的数据
        new LoggingHandler(),
        //最多接收1024个字节，[0,4)字节为需要读取的长度，如果读取到的长度不够是不会交给下一个handler的
        new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
        new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println("================================");
                System.out.println("==========处理后的消息===========");
                System.out.println("================================");

                super.channelRead(ctx, msg);
            }
        },
        //这个handler用于打印经过ltc解码器处理的数据
        new LoggingHandler()
);

ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
byte[] message = "hello,world,this,is,netty".getBytes();
buf.writeInt(message.length);
buf.writeBytes(message);


ByteBuf slice0 = buf.slice(0, 7);
ByteBuf slice1 = buf.slice(7, message.length);

buf.retain();//防止调用writeInbound被回收

testChannel.writeInbound(slice0);
testChannel.writeInbound(slice1);
```

+ <font style="color:#DF2A3F;">结果：LTC解码器将两个buf合并到一起后发给后面的handler</font>

```plain
2024-09-08 03:51:44.026 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 7B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 19 68 65 6c                            |....hel         |
+--------+-------------------------------------------------+----------------+
2024-09-08 03:51:44.039 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ COMPLETE
2024-09-08 03:51:44.039 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ COMPLETE
2024-09-08 03:51:44.040 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 25B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 6c 6f 2c 77 6f 72 6c 64 2c 74 68 69 73 2c 69 73 |lo,world,this,is|
|00000010| 2c 6e 65 74 74 79 00 00 00                      |,netty...       |
+--------+-------------------------------------------------+----------------+
================================
==========处理后的消息===========
================================
2024-09-08 03:51:44.045 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 29B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 19 68 65 6c 6c 6f 2c 77 6f 72 6c 64 2c |....hello,world,|
|00000010| 74 68 69 73 2c 69 73 2c 6e 65 74 74 79          |this,is,netty   |
+--------+-------------------------------------------------+----------------+
```

##### <font style="color:#DF2A3F;">c. 模拟黏包</font>
```java
EmbeddedChannel testChannel = new EmbeddedChannel(
    //这个handler用于打印最原始的数据
    new LoggingHandler(),
    //最多接收1024个字节，[0,4)字节为需要读取的长度，如果读取到的长度不够是不会交给下一个handler的
    new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
    new ChannelInboundHandlerAdapter() {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("================================");
            System.out.println("==========处理后的消息===========");
            System.out.println("================================");

            super.channelRead(ctx, msg);
        }
    },
    //这个handler用于打印经过ltc解码器处理的数据
    new LoggingHandler()
);

ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
byte[] message0 = "hello,world,this,is,netty".getBytes();
byte[] message1 = "i,love,you,this,world".getBytes();

buf.writeInt(message0.length);
buf.writeBytes(message0);
buf.writeInt(message1.length);
buf.writeBytes(message1);
testChannel.writeInbound(buf);
```

+ <font style="color:#DF2A3F;">结果：LTC解码将数据处理为两个buf后交给后面的handler</font>

```plain
2024-09-08 03:56:06.888 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 54B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 19 68 65 6c 6c 6f 2c 77 6f 72 6c 64 2c |....hello,world,|
|00000010| 74 68 69 73 2c 69 73 2c 6e 65 74 74 79 00 00 00 |this,is,netty...|
|00000020| 15 69 2c 6c 6f 76 65 2c 79 6f 75 2c 74 68 69 73 |.i,love,you,this|
|00000030| 2c 77 6f 72 6c 64                               |,world          |
+--------+-------------------------------------------------+----------------+
================================
==========处理后的消息===========
================================
2024-09-08 03:56:06.898 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 29B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 19 68 65 6c 6c 6f 2c 77 6f 72 6c 64 2c |....hello,world,|
|00000010| 74 68 69 73 2c 69 73 2c 6e 65 74 74 79          |this,is,netty   |
+--------+-------------------------------------------------+----------------+
================================
==========处理后的消息===========
================================
2024-09-08 03:56:06.898 [main] [DEBUG] - [id: 0xembedded, L:embedded - R:embedded] READ: 25B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 15 69 2c 6c 6f 76 65 2c 79 6f 75 2c 74 |....i,love,you,t|
|00000010| 68 69 73 2c 77 6f 72 6c 64                      |his,world       |
+--------+-------------------------------------------------+----------------+
```

# netty案例
## 双向通信的客户端和服务端
+ 服务端

```java
@Slf4j
public class Server {
    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                String clientMsg = msg.toString();
                                log.info("客户端发送来消息：{}", clientMsg);
                                Channel client = ctx.channel();
                                client.writeAndFlush("服务端收到消息：" + clientMsg); // 当当前handler向前寻找
                            }
                        });
                    }

                })
                .bind(8080);
    }
}
```

+ 客户端

```java
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        Object inputLock = new Object();
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8)); // 将 String 编码为 ByteBuf
                        ch.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8)); // 将 ByteBuf 解码为 String
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                String serverMsg = msg.toString();
                                log.info(serverMsg);
                                synchronized (inputLock){
                                    inputLock.notify();
                                }
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        /*
            这里不用channelFuture的原因是，处理channelFuture连接事件的线程是selector中的线程
            如果在连接成功后直接让线程
            while(true){
                //接收消息
                //发送消息
            }
            会导致selector无法处理其它事件，一直阻塞在这里
        */

        channelFuture.sync(); //等待连接到服务端
        log.info("连接成功");

        Channel client = channelFuture.channel();
        Scanner scanner = new Scanner(System.in);

        //这里让单独一个线程处理客户端输入
        Thread handleInput = new Thread(() -> {
            while (true) {
                System.out.print("请输入消息：");
                String input = scanner.nextLine();
                client.writeAndFlush(input);
                // 当前线程进入阻塞，等待服务端返回消息唤醒
                synchronized (inputLock){
                    try {
                        inputLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, "handle-input");
        handleInput.start();
    }
}
```



