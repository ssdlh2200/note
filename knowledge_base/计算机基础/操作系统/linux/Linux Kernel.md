# 进程&线程
## 进程
### 什么是进程？
引入多道程序技术之后为了方便操作系统管理，完成各程序并发执行，引入了进程、进程实体的概念。操作系统为每个运行的程序配置一个数据结构，称为进程控制块（PCB），用来描述进程的各种信息（如程序代码存放位置，正在使用什么IO设备等等）

进程 = 程序段+数据段+PCB，三部分组成了进程实体，一般情况下，我们把进程实体就简称为进程。

例如，所请创建进程，实质上是创建进程实体中的PCB；而撤销进程，实质上是撤销进程实体中PCB。**<font style="color:#E8323C;">PCB是进程存在的唯一标志！</font>**

在 Linux 中每一个进程都由 task_struct 来定义。（注：线程也由 task_struct 定义）

## 线程
### 参考[https://www.51cto.com/article/720464.html](https://www.51cto.com/article/720464.html)
### 用户级线程
个人观点：现在（2024年8月7日01:50:59）的语言中，协程更像是用户级线程

1. **纯用户级线程**：
    - 这种实现完全在用户空间进行，内核并不知道这些线程的存在。
    - 线程的创建、调度和上下文切换都由用户级线程库管理。
    - 一个典型的例子是 GNU Pth（Portable Threads），这是一个用户级线程库。
    - 优点是上下文切换开销小，速度快，但缺点是如果一个用户级线程执行了阻塞的系统调用，整个进程会被阻塞。
2. **内核级线程**：
    - 由内核管理，每个线程在内核中都有一个对应的 `task_struct`。
    - 线程的创建、调度和上下文切换由内核负责。
    - 典型的例子是 POSIX 线程（pthreads），在大多数现代操作系统中，pthreads 实现为内核级线程。
    - 优点是线程可以在多个处理器上并行执行，缺点是上下文切换开销较大。
3. **混合实现**（多对多模型）：
    - 结合了用户级线程和内核级线程的优点。
    - 用户级线程库在用户空间中管理多个用户级线程，这些用户级线程映射到一个或多个内核级线程上。
    - 这种实现允许在用户空间进行轻量级的线程管理，同时利用内核级线程在多处理器系统中的并行能力。
    - 典型的例子是 Solaris 操作系统中的线程实现。

**具体例子**

+ **GNU Pth**：纯用户级线程实现，所有线程在用户空间管理。
+ **POSIX pthreads**：大多数实现为内核级线程。
+ **Solaris Threads**：支持 M 模型，允许用户级线程映射到多个内核级线程上。

### 内核级线程
> _注：对操作系统来说，用户级线程具有__**不可见性**__，也称__**透明性**__。_
>

**内核级线程**

现在我们知道，许多操作系统都已经支持内核级线程了。为了实现线程，内核里就需要有用来记录系统里所有线程的线程表。当需要创建一个新线程的时候，就需要进行一个**系统调用**，然后由**操作系统**进行线程表的更新。当然了，传统的进程表也还是有的。你想想看，如果操作系统「看得见」线程，有什么好处？“

小白自信的回答：“操作系统内核如果知道线程的存在，就可以像调度多个进程一样，把这些线程放在好几个 CPU 核心上，就能做到实际上的**并行**了。”

“还有一点你没有说到，如果线程可见，那么**<font style="color:#DF2A3F;">假如线程 A 阻塞了，与他同属一个进程的线程也不会被阻塞。这是内核级线程的绝对优势。</font>**”

“那内核级线程就没有什么缺点吗？”

“缺点当然是有的，你想想看，让操作系统进行线程调度，那意味着每次切换线程，就需要「**陷入**」内核态，而操作系统从**用户态到内核态**的转变是有开销的，所以说**内核级线程切换的代价要比用户级线程大**。还有很重要的一点——线程表是存放在操作系统固定的**表格空间**或者**堆栈空间**里，所以内核级线程的数量是有限的，扩展性比不上用户级线程。”

"内核级线程就这么点东西，我最后给你留一张图，你要是能看得懂，就说明你理解今天的概念了。"

![画板](https://cdn.nlark.com/yuque/0/2023/jpeg/33704534/1679500435080-10b0b667-c7e9-4020-9692-94a7c84eae1b.jpeg)

### C  pthread 库中的线程
`POSIX Threads`库中创建的线程是内核级线程。操作系统的内核会为每个线程分配资源和管理其调度。这意味着每个线程都可以独立运行，操作系统可以在多个处理器上并行调度它们。  

在 Linux 下，线程底层数据结构和进程是一样的，唯一的区别就是，线程之间会共享内存、文件等资源，而进程之间是完全隔离的。

### Linux线程和进程历史
简单来讲：内核级线程就是操作系统内核支持，用户级就是函数库实现（也就是说，不管你操作系统是不是支持线程的，我都可以在你上面用多线程编程）。

好了，那么，我们首先明白一件事：不管Linux还是什么OS，都可以多线程编程的，怎么多线程编程呢？程序员要创建一个线程。当然需要使用xxx函数，这个函数如果是操作系统本身就提供的系统函数，当然没问题，操作系统创建的线程，自然是内核级的了。

如果操作系统没有提供“创建线程”的函数（比如Linux 2.4及以前的版本，因为Linux刚诞生那时候，还没有“线程”的概念，能处理多“进程”就不错了），当然你程序员也没办法在操作系统上创建线程。所以，Linux 2.4内核中不知道什么是“线程”，只有一个“task_struct”的数据结构，就是进程。那么，后来随着科学技术的发展，大家提出线程的概念，而且，线程有时候的确是个好东西，于是，我们希望Linux能加入“多线程”编程。

要修改一个操作系统，那是很复杂的事情，特别是当操作系统越来越庞大的时候。怎么才能让Linux支持“多线程”呢？

首先，最简单的，就是不去动操作系统的“内核”，而是写一个函数库来“模拟”线程。也就是说，我用C写一个函数，比如 create_thread，这个函数最终在Linux的内核里还是去调用了创建“进程”的函数去创建了一个进程（因为OS没变嘛，没有线程这个东西）。 如果有人要多线程编程，那么你就调用 这个create_thread 去创建线程吧，好了，这个线程，就是用库函数创建的线程，就是所谓的“**<font style="color:#DF2A3F;">用户级线程</font>**”了。等等，这是神马意思？赤裸裸的欺骗？也不是。

为什么不是？因为别人给你提供了这个线程函数，你创建了“线程”，那么，你的线程（虽然本质上还是进程）就有了“线程”的一些“特征”，比如可以共享变量啊什么的，咦？那怎么做到的？当然有一套机制，反正人家给你做好了，你用就行了。

这种欺骗自然是不“完美”的，有线程的“一些”特征，但不能完全符合理论上的“线程”的概念(POSIX的要求），比如，这种多线程不能被分配到多核上，用户创建的N个线程，对于着内核里面其实就一个“进程”，导致调度啊，管理啊麻烦.....

为什么要采用这种“模拟”的方式呢？改内核不是一天两天的事情，先将就用着吧。内核慢慢来改。

怎么干改内核这个艰苦卓越的工作？Linux是开源、免费的，谁愿意来干这个活？有两家公司参与了对LinuxThreads的改进（向他们致敬）：IBM启动的NGTP(Next Generation POSIX Threads)项目，以及红帽Redhat公司的NPTL（Native POSIX Thread Library），IBM那个项目，在2003年因为种种原因放弃了，大家都转到NPTL这个项目来了。

最终，当然是成功了，在Linux 2.6的内核版本中，这个NPTL项目怎么做的呢？并不是在Linux内核中加了一个“线程”，仍然和原来一样，进程（其实，进程线程就是个概念，对于计算机，只要能高效的实现这个概念就行，程序员能用就OK，管它究竟怎么实现的），不过，用的clone实现的[轻量级进程](https://www.zhihu.com/search?q=%E8%BD%BB%E9%87%8F%E7%BA%A7%E8%BF%9B%E7%A8%8B&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra=%7B%22sourceType%22%3A%22answer%22%2C%22sourceId%22%3A148038406%7D)，内核又增加了若干机制来保证线程的表现和POSIX相同，最关键的一点，用户调用pthread库创建的一个线程，会在内核创建一个“线程”，这就是所谓的1：1模型。所以，Linux下，是有“内核级”线程的，网上很多说Linux是用户级线程，都是不完整的，说的Linux很早以前的版本（现在Linux已经是4.X的版本了）。

还有个 pthread 的问题，pthread是个线程函数库，他提供了一些函数，让程序员可以用它来创建，使用线程。那么问题是，这个函数库里面的函数，比如 pthread_create 创建线程的函数，他是怎么实现的呢？他如果是用以前的方法，那程序员用它来创建的线程，还是“用户级”线程；如果它使用了NPTL方式创建线程，那么，它创建的线程，就是“内核级”线程。

OK，结论，如果你 1：使用2.6的内核的系统平台，2：你的gcc支持NPTL （现在一般都支持），那么你编译出来的多线程程序，就是“内核级”线程了。

所以，现在回答问题，只要你不是很古董级的电脑，Linux下用pthread创建的线程是“内核级线程”

最后，这NPTL也并不是完美的，还有一些小问题，像有一些商业操作系统，可以实现混合模型，如1:1，N:M等（就是内核线程和用户线程的对应关系），这就强大了，Linux仍有改进的空间

## <font style="color:rgb(0, 0, 0);">Linux-task_struct源代码详解</font>
[https://github.com/torvalds/linux/blob/master/include/linux/sched.h](https://github.com/torvalds/linux/blob/master/include/linux/sched.h)

位于 `<font style="color:rgb(51, 51, 51);">include/linux/sched.h</font>`

```c
struct task_struct 
{
    /* 
    1. state: 进程执行时，它会根据具体情况改变状态。进程状态是进程调度和对换的依据。Linux中的进程主要有如下状态:
        1) TASK_RUNNING: 可运行
        处于这种状态的进程，只有两种状态:
            1.1) 正在运行
            正在运行的进程就是当前进程(由current所指向的进程)
            1.2) 正准备运行
            准备运行的进程只要得到CPU就可以立即投入运行，CPU是这些进程唯一等待的系统资源，系统中有一个运行队列(run_queue)，用来容纳所有处于可运行状态的进程，调度程序执行时，从中选择一个进程投入运行 
        
        2) TASK_INTERRUPTIBLE: 可中断的等待状态，是针对等待某事件或其他资源的睡眠进程设置的，在内核发送信号给该进程表明事件已经发生时，进程状态变为TASK_RUNNING，它只要调度器选中该进程即可恢复执行 
        
        3) TASK_UNINTERRUPTIBLE: 不可中断的等待状态
         处于该状态的进程正在等待某个事件(event)或某个资源，它肯定位于系统中的某个等待队列(wait_queue)中，处于不可中断等待态的进程是因为硬件环境不能满足而等待，例如等待特定的系统资源，它任何情况下都不能被打断，只能用特定的方式来唤醒它，例如唤醒函数wake_up()等 
　　　　　它们不能由外部信号唤醒，只能由内核亲自唤醒        

        4) TASK_ZOMBIE: 僵死
        进程虽然已经终止，但由于某种原因，父进程还没有执行wait()系统调用，终止进程的信息也还没有回收。顾名思义，处于该状态的进程就是死进程，这种进程实际上是系统中的垃圾，必须进行相应处理以释放其占用的资源。

        5) TASK_STOPPED: 暂停
        此时的进程暂时停止运行来接受某种特殊处理。通常当进程接收到SIGSTOP、SIGTSTP、SIGTTIN或 SIGTTOU信号后就处于这种状态。例如，正接受调试的进程就处于这种状态
　　　　
　　　　　6) TASK_TRACED
　　　　　从本质上来说，这属于TASK_STOPPED状态，用于从停止的进程中，将当前被调试的进程与常规的进程区分开来
　　　　　　
　　　　　7) TASK_DEAD
　　　　　父进程wait系统调用发出后，当子进程退出时，父进程负责回收子进程的全部资源，子进程进入TASK_DEAD状态

         8) TASK_SWAPPING: 换入/换出
    */
    volatile long state;
    
    /*
    2. stack
    进程内核栈，进程通过alloc_thread_info函数分配它的内核栈，通过free_thread_info函数释放所分配的内核栈
    */     
    void *stack;
    
    /*
    3. usage
    进程描述符使用计数，被置为2时，表示进程描述符正在被使用而且其相应的进程处于活动状态
    */
    atomic_t usage;

    /*
    4. flags
    flags是进程当前的状态标志(注意和运行状态区分)
        1) #define PF_ALIGNWARN    0x00000001: 显示内存地址未对齐警告
        2) #define PF_PTRACED    0x00000010: 标识是否是否调用了ptrace
        3) #define PF_TRACESYS    0x00000020: 跟踪系统调用
        4) #define PF_FORKNOEXEC 0x00000040: 已经完成fork，但还没有调用exec
        5) #define PF_SUPERPRIV    0x00000100: 使用超级用户(root)权限
        6) #define PF_DUMPCORE    0x00000200: dumped core  
        7) #define PF_SIGNALED    0x00000400: 此进程由于其他进程发送相关信号而被杀死 
        8) #define PF_STARTING    0x00000002: 当前进程正在被创建
        9) #define PF_EXITING    0x00000004: 当前进程正在关闭
        10) #define PF_USEDFPU    0x00100000: Process used the FPU this quantum(SMP only)  
        #define PF_DTRACE    0x00200000: delayed trace (used on m68k)  
    */
    unsigned int flags;     

    /*
    5. ptrace
    ptrace系统调用，成员ptrace被设置为0时表示不需要被跟踪，它的可能取值如下： 
    linux-2.6.38.8/include/linux/ptrace.h  
        1) #define PT_PTRACED    0x00000001
        2) #define PT_DTRACE    0x00000002: delayed trace (used on m68k, i386) 
        3) #define PT_TRACESYSGOOD    0x00000004
        4) #define PT_PTRACE_CAP    0x00000008: ptracer can follow suid-exec 
        5) #define PT_TRACE_FORK    0x00000010
        6) #define PT_TRACE_VFORK    0x00000020
        7) #define PT_TRACE_CLONE    0x00000040
        8) #define PT_TRACE_EXEC    0x00000080
        9) #define PT_TRACE_VFORK_DONE    0x00000100
        10) #define PT_TRACE_EXIT    0x00000200
    */
    unsigned int ptrace;
    unsigned long ptrace_message;
    siginfo_t *last_siginfo; 

    /*
    6. lock_depth
    用于表示获取大内核锁的次数，如果进程未获得过锁，则置为-1
    */
    int lock_depth;         

    /*
    7. oncpu
    在SMP上帮助实现无加锁的进程切换(unlocked context switches)
    */
#ifdef CONFIG_SMP
#ifdef __ARCH_WANT_UNLOCKED_CTXSW
    int oncpu;
#endif
#endif

    /*
    8. 进程调度
        1) prio: 调度器考虑的优先级保存在prio，由于在某些情况下内核需要暂时提高进程的优先级，因此需要第三个成员来表示(除了static_prio、normal_prio之外)，由于这些改变不是持久的，因此静态(static_prio)和普通(normal_prio)优先级不受影响
        2) static_prio: 用于保存进程的"静态优先级"，静态优先级是进程"启动"时分配的优先级，它可以用nice、sched_setscheduler系统调用修改，否则在进程运行期间会一直保持恒定
        3) normal_prio: 表示基于进程的"静态优先级"和"调度策略"计算出的优先级，因此，即使普通进程和实时进程具有相同的静态优先级(static_prio)，其普通优先级(normal_prio)也是不同的。进程分支时(fork)，新创建的子进程会集成普通优先级   
    */
    int prio, static_prio, normal_prio;
    /*
        4) rt_priority: 表示实时进程的优先级，需要明白的是，"实时进程优先级"和"普通进程优先级"有两个独立的范畴，实时进程即使是最低优先级也高于普通进程，最低的实时优先级为0，最高的优先级为99，值越大，表明优先级越高
    */
    unsigned int rt_priority;
    /*
        5) sched_class: 该进程所属的调度类，目前内核中有实现以下四种： 
            5.1) static const struct sched_class fair_sched_class;
            5.2) static const struct sched_class rt_sched_class;
            5.3) static const struct sched_class idle_sched_class;
            5.4) static const struct sched_class stop_sched_class;        
    */
    const struct sched_class *sched_class;
    /*
        6) se: 用于普通进程的调用实体 
　　调度器不限于调度进程，还可以处理更大的实体，这可以实现"组调度"，可用的CPU时间可以首先在一般的进程组(例如所有进程可以按所有者分组)之间分配，接下来分配的时间在组内再次分配
　　这种一般性要求调度器不直接操作进程，而是处理"可调度实体"，一个实体有sched_entity的一个实例标识
　　在最简单的情况下，调度在各个进程上执行，由于调度器设计为处理可调度的实体，在调度器看来各个进程也必须也像这样的实体，因此se在task_struct中内嵌了一个sched_entity实例，调度器可据此操作各个task_struct
    */
    struct sched_entity se;
    /*
        7) rt: 用于实时进程的调用实体 
    */
    struct sched_rt_entity rt;

#ifdef CONFIG_PREEMPT_NOTIFIERS 
    /*
    9. preempt_notifier
    preempt_notifiers结构体链表 
    */
    struct hlist_head preempt_notifiers;
#endif
 
     /*
     10. fpu_counter
     FPU使用计数 
     */
    unsigned char fpu_counter;

#ifdef CONFIG_BLK_DEV_IO_TRACE
    /*
    11. btrace_seq
    blktrace是一个针对Linux内核中块设备I/O层的跟踪工具
    */
    unsigned int btrace_seq;
#endif

    /*
    12. policy
    policy表示进程的调度策略，目前主要有以下五种：
        1) #define SCHED_NORMAL        0: 用于普通进程，它们通过完全公平调度器来处理
        2) #define SCHED_FIFO        1: 先来先服务调度，由实时调度类处理
        3) #define SCHED_RR            2: 时间片轮转调度，由实时调度类处理
        4) #define SCHED_BATCH        3: 用于非交互、CPU使用密集的批处理进程，通过完全公平调度器来处理，调度决策对此类进程给与"冷处理"，它们绝不会抢占CFS调度器处理的另一个进程，因此不会干扰交互式进程，如果不打算用nice降低进程的静态优先级，同时又不希望该进程影响系统的交互性，最适合用该调度策略
        5) #define SCHED_IDLE        5: 可用于次要的进程，其相对权重总是最小的，也通过完全公平调度器来处理。要注意的是，SCHED_IDLE不负责调度空闲进程，空闲进程由内核提供单独的机制来处理
    只有root用户能通过sched_setscheduler()系统调用来改变调度策略 
    */
    unsigned int policy;

    /*
    13. cpus_allowed
    cpus_allowed是一个位域，在多处理器系统上使用，用于控制进程可以在哪里处理器上运行
    */
    cpumask_t cpus_allowed;

    /*
    14. RCU同步原语 
    */
#ifdef CONFIG_TREE_PREEMPT_RCU
    int rcu_read_lock_nesting;
    char rcu_read_unlock_special;
    struct rcu_node *rcu_blocked_node;
    struct list_head rcu_node_entry;
#endif /* #ifdef CONFIG_TREE_PREEMPT_RCU */

#if defined(CONFIG_SCHEDSTATS) || defined(CONFIG_TASK_DELAY_ACCT)
    /*
    15. sched_info
    用于调度器统计进程的运行信息
    */
    struct sched_info sched_info;
#endif

    /*
    16. tasks
    通过list_head将当前进程的task_struct串联进内核的进程列表中，构建；linux进程链表
    */
    struct list_head tasks;

    /*
    17. pushable_tasks
    limit pushing to one attempt 
    */
    struct plist_node pushable_tasks;

    /*
    18. 进程地址空间 
        1) mm: 指向进程所拥有的内存描述符 
        2) active_mm: active_mm指向进程运行时所使用的内存描述符
    对于普通进程而言，这两个指针变量的值相同。但是，内核线程不拥有任何内存描述符，所以它们的mm成员总是为NULL。当内核线程得以运行时，它的active_mm成员被初始化为前一个运行进程的active_mm值
    */
    struct mm_struct *mm, *active_mm;

    /*
    19. exit_state
    进程退出状态码
    */
    int exit_state;

    /*
    20. 判断标志
        1) exit_code
        exit_code用于设置进程的终止代号，这个值要么是_exit()或exit_group()系统调用参数(正常终止)，要么是由内核提供的一个错误代号(异常终止)
        2) exit_signal
        exit_signal被置为-1时表示是某个线程组中的一员。只有当线程组的最后一个成员终止时，才会产生一个信号，以通知线程组的领头进程的父进程
    */
    int exit_code, exit_signal; 
    /*
        3) pdeath_signal
        pdeath_signal用于判断父进程终止时发送信号
    */
    int pdeath_signal;   
    /*
        4)  personality用于处理不同的ABI，它的可能取值如下： 
            enum 
            {
                PER_LINUX =        0x0000,
                PER_LINUX_32BIT =    0x0000 | ADDR_LIMIT_32BIT,
                PER_LINUX_FDPIC =    0x0000 | FDPIC_FUNCPTRS,
                PER_SVR4 =        0x0001 | STICKY_TIMEOUTS | MMAP_PAGE_ZERO,
                PER_SVR3 =        0x0002 | STICKY_TIMEOUTS | SHORT_INODE,
                PER_SCOSVR3 =        0x0003 | STICKY_TIMEOUTS |
                                 WHOLE_SECONDS | SHORT_INODE,
                PER_OSR5 =        0x0003 | STICKY_TIMEOUTS | WHOLE_SECONDS,
                PER_WYSEV386 =        0x0004 | STICKY_TIMEOUTS | SHORT_INODE,
                PER_ISCR4 =        0x0005 | STICKY_TIMEOUTS,
                PER_BSD =        0x0006,
                PER_SUNOS =        0x0006 | STICKY_TIMEOUTS,
                PER_XENIX =        0x0007 | STICKY_TIMEOUTS | SHORT_INODE,
                PER_LINUX32 =        0x0008,
                PER_LINUX32_3GB =    0x0008 | ADDR_LIMIT_3GB,
                PER_IRIX32 =        0x0009 | STICKY_TIMEOUTS, 
                PER_IRIXN32 =        0x000a | STICKY_TIMEOUTS, 
                PER_IRIX64 =        0x000b | STICKY_TIMEOUTS, 
                PER_RISCOS =        0x000c,
                PER_SOLARIS =        0x000d | STICKY_TIMEOUTS,
                PER_UW7 =        0x000e | STICKY_TIMEOUTS | MMAP_PAGE_ZERO,
                PER_OSF4 =        0x000f,              
                PER_HPUX =        0x0010,
                PER_MASK =        0x00ff,
            };
    */
    unsigned int personality;
    /*
        5) did_exec
        did_exec用于记录进程代码是否被execve()函数所执行
    */
    unsigned did_exec:1;
    /*
        6) in_execve
        in_execve用于通知LSM是否被do_execve()函数所调用
    */
    unsigned in_execve:1;     
    /*
        7) in_iowait
        in_iowait用于判断是否进行iowait计数
    */
    unsigned in_iowait:1;

    /*
        8) sched_reset_on_fork
        sched_reset_on_fork用于判断是否恢复默认的优先级或调度策略
    */
    unsigned sched_reset_on_fork:1;

    /*
    21. 进程标识符(PID)
    在CONFIG_BASE_SMALL配置为0的情况下，PID的取值范围是0到32767，即系统中的进程数最大为32768个
    #define PID_MAX_DEFAULT (CONFIG_BASE_SMALL ? 0x1000 : 0x8000)  
    在Linux系统中，一个线程组中的所有线程使用和该线程组的领头线程(该组中的第一个轻量级进程)相同的PID，并被存放在tgid成员中。只有线程组的领头线程的pid成员才会被设置为与tgid相同的值。注意，getpid()系统调用
返回的是当前进程的tgid值而不是pid值。
    */
    pid_t pid;
    pid_t tgid;

#ifdef CONFIG_CC_STACKPROTECTOR 
    /*
    22. stack_canary
    防止内核堆栈溢出，在GCC编译内核时，需要加上-fstack-protector选项
    */
    unsigned long stack_canary;
#endif
 
     /*
     23. 表示进程亲属关系的成员 
         1) real_parent: 指向其父进程，如果创建它的父进程不再存在，则指向PID为1的init进程
         2) parent: 指向其父进程，当它终止时，必须向它的父进程发送信号。它的值通常与real_parent相同 
     */
    struct task_struct *real_parent;  
    struct task_struct *parent;   
    /*
        3) children: 表示链表的头部，链表中的所有元素都是它的子进程(子进程链表)
        4) sibling: 用于把当前进程插入到兄弟链表中(连接到父进程的子进程链表(兄弟链表))
        5) group_leader: 指向其所在进程组的领头进程
    */
    struct list_head children;     
    struct list_head sibling;     
    struct task_struct *group_leader;     
     
    struct list_head ptraced;
    struct list_head ptrace_entry; 
    struct bts_context *bts;

    /*
    24. pids
    PID散列表和链表  
    */
    struct pid_link pids[PIDTYPE_MAX];
    /*
    25. thread_group
    线程组中所有进程的链表
    */
    struct list_head thread_group;

    /*
    26. do_fork函数 
        1) vfork_done
        在执行do_fork()时，如果给定特别标志，则vfork_done会指向一个特殊地址
        2) set_child_tid、clear_child_tid
        如果copy_process函数的clone_flags参数的值被置为CLONE_CHILD_SETTID或CLONE_CHILD_CLEARTID，则会把child_tidptr参数的值分别复制到set_child_tid和clear_child_tid成员。这些标志说明必须改变子
进程用户态地址空间的child_tidptr所指向的变量的值。
    */
    struct completion *vfork_done;         
    int __user *set_child_tid;         
    int __user *clear_child_tid;         

    /*
    27. 记录进程的I/O计数(时间)
        1) utime
        用于记录进程在"用户态"下所经过的节拍数(定时器)
        2) stime
        用于记录进程在"内核态"下所经过的节拍数(定时器)
        3) utimescaled
        用于记录进程在"用户态"的运行时间，但它们以处理器的频率为刻度
        4) stimescaled
        用于记录进程在"内核态"的运行时间，但它们以处理器的频率为刻度
    */
    cputime_t utime, stime, utimescaled, stimescaled;
    /*
        5) gtime
        以节拍计数的虚拟机运行时间(guest time)
    */
    cputime_t gtime;
    /*
        6) prev_utime、prev_stime是先前的运行时间
    */
    cputime_t prev_utime, prev_stime; 
    /*
        7) nvcsw
        自愿(voluntary)上下文切换计数
        8) nivcsw
        非自愿(involuntary)上下文切换计数
    */
    unsigned long nvcsw, nivcsw; 
    /*
        9) start_time
        进程创建时间
        10) real_start_time
        进程睡眠时间，还包含了进程睡眠时间，常用于/proc/pid/stat，
    */
    struct timespec start_time;          
    struct timespec real_start_time;
    /*
        11) cputime_expires
        用来统计进程或进程组被跟踪的处理器时间，其中的三个成员对应着cpu_timers[3]的三个链表
    */
    struct task_cputime cputime_expires;
    struct list_head cpu_timers[3];
    #ifdef CONFIG_DETECT_HUNG_TASK 
    /*
        12) last_switch_count
        nvcsw和nivcsw的总和
    */
    unsigned long last_switch_count;
    #endif
    struct task_io_accounting ioac;
#if defined(CONFIG_TASK_XACCT)
    u64 acct_rss_mem1;     
    u64 acct_vm_mem1;     
    cputime_t acct_timexpd;     
#endif

    /*
    28. 缺页统计 
    */     
    unsigned long min_flt, maj_flt; 

    /*
    29. 进程权能 
    */
    const struct cred *real_cred;     
    const struct cred *cred;     
    struct mutex cred_guard_mutex;     
    struct cred *replacement_session_keyring;  

    /*
    30. comm[TASK_COMM_LEN]
    相应的程序名 
    */
    char comm[TASK_COMM_LEN]; 

    /*
    31. 文件 
        1) fs
        用来表示进程与文件系统的联系，包括当前目录和根目录
        2) files
        表示进程当前打开的文件
    */
    int link_count, total_link_count; 
    struct fs_struct *fs; 
    struct files_struct *files;

#ifdef CONFIG_SYSVIPC 
    /*
    32. sysvsem
    进程通信(SYSVIPC)
    */
    struct sysv_sem sysvsem;
#endif

    /*
    33. 处理器特有数据
    */
    struct thread_struct thread;  

    /*
    34. nsproxy
    命名空间 
    */
    struct nsproxy *nsproxy; 

    /*
    35. 信号处理 
        1) signal: 指向进程的信号描述符
        2) sighand: 指向进程的信号处理程序描述符
    */
    struct signal_struct *signal;
    struct sighand_struct *sighand;
    /*
        3) blocked: 表示被阻塞信号的掩码
        4) real_blocked: 表示临时掩码
    */
    sigset_t blocked, real_blocked;
    sigset_t saved_sigmask;     
    /*
        5) pending: 存放私有挂起信号的数据结构
    */
    struct sigpending pending;
    /*
        6) sas_ss_sp: 信号处理程序备用堆栈的地址
        7) sas_ss_size: 表示堆栈的大小
    */
    unsigned long sas_ss_sp;
    size_t sas_ss_size;
    /*
        8) notifier
        设备驱动程序常用notifier指向的函数来阻塞进程的某些信号
        9) otifier_data
        指的是notifier所指向的函数可能使用的数据。
        10) otifier_mask
        标识这些信号的位掩码
    */
    int (*notifier)(void *priv);
    void *notifier_data;
    sigset_t *notifier_mask;

    /*
    36. 进程审计 
    */
    struct audit_context *audit_context; 
#ifdef CONFIG_AUDITSYSCALL
    uid_t loginuid;
    unsigned int sessionid;
#endif

    /*
    37. secure computing 
    */
    seccomp_t seccomp;
     
     /*
     38. 用于copy_process函数使用CLONE_PARENT标记时 
     */
       u32 parent_exec_id;
       u32 self_exec_id;
 
     /*
     39. alloc_lock
     用于保护资源分配或释放的自旋锁 
     */
    spinlock_t alloc_lock;

    /*
    40. 中断 
    */
#ifdef CONFIG_GENERIC_HARDIRQS 
    struct irqaction *irqaction;
#endif
#ifdef CONFIG_TRACE_IRQFLAGS
    unsigned int irq_events;
    int hardirqs_enabled;
    unsigned long hardirq_enable_ip;
    unsigned int hardirq_enable_event;
    unsigned long hardirq_disable_ip;
    unsigned int hardirq_disable_event;
    int softirqs_enabled;
    unsigned long softirq_disable_ip;
    unsigned int softirq_disable_event;
    unsigned long softirq_enable_ip;
    unsigned int softirq_enable_event;
    int hardirq_context;
    int softirq_context;
#endif
     
     /*
     41. pi_lock
     task_rq_lock函数所使用的锁 
     */
    spinlock_t pi_lock;

#ifdef CONFIG_RT_MUTEXES 
    /*
    42. 基于PI协议的等待互斥锁，其中PI指的是priority inheritance/9优先级继承)
    */
    struct plist_head pi_waiters; 
    struct rt_mutex_waiter *pi_blocked_on;
#endif

#ifdef CONFIG_DEBUG_MUTEXES 
    /*
    43. blocked_on
    死锁检测
    */
    struct mutex_waiter *blocked_on;
#endif

/*
    44. lockdep，
*/
#ifdef CONFIG_LOCKDEP
# define MAX_LOCK_DEPTH 48UL
    u64 curr_chain_key;
    int lockdep_depth;
    unsigned int lockdep_recursion;
    struct held_lock held_locks[MAX_LOCK_DEPTH];
    gfp_t lockdep_reclaim_gfp;
#endif
 
     /*
     45. journal_info
     JFS文件系统
     */
    void *journal_info;
     
     /*
     46. 块设备链表
     */
    struct bio *bio_list, **bio_tail; 

    /*
    47. reclaim_state
    内存回收
    */
    struct reclaim_state *reclaim_state;

    /*
    48. backing_dev_info
    存放块设备I/O数据流量信息
    */
    struct backing_dev_info *backing_dev_info;

    /*
    49. io_context
    I/O调度器所使用的信息 
    */
    struct io_context *io_context;

    /*
    50. CPUSET功能 
    */
#ifdef CONFIG_CPUSETS
    nodemask_t mems_allowed;     
    int cpuset_mem_spread_rotor;
#endif

    /*
    51. Control Groups 
    */
#ifdef CONFIG_CGROUPS 
    struct css_set *cgroups; 
    struct list_head cg_list;
#endif

    /*
    52. robust_list
    futex同步机制 
    */
#ifdef CONFIG_FUTEX
    struct robust_list_head __user *robust_list;
#ifdef CONFIG_COMPAT
    struct compat_robust_list_head __user *compat_robust_list;
#endif
    struct list_head pi_state_list;
    struct futex_pi_state *pi_state_cache;
#endif 
#ifdef CONFIG_PERF_EVENTS
    struct perf_event_context *perf_event_ctxp;
    struct mutex perf_event_mutex;
    struct list_head perf_event_list;
#endif

    /*
    53. 非一致内存访问(NUMA  Non-Uniform Memory Access)
    */
#ifdef CONFIG_NUMA
    struct mempolicy *mempolicy;    /* Protected by alloc_lock */
    short il_next;
#endif

    /*
    54. fs_excl
    文件系统互斥资源
    */
    atomic_t fs_excl;

    /*
    55. rcu
    RCU链表 
    */     
    struct rcu_head rcu;

    /*
    56. splice_pipe
    管道
    */
    struct pipe_inode_info *splice_pipe;

    /*
    57. delays
    延迟计数
    */
#ifdef    CONFIG_TASK_DELAY_ACCT
    struct task_delay_info *delays;
#endif

    /*
    58. make_it_fail
    fault injection
    */
#ifdef CONFIG_FAULT_INJECTION
    int make_it_fail;
#endif

    /*
    59. dirties
    FLoating proportions 
    */
    struct prop_local_single dirties;

    /*
    60. Infrastructure for displayinglatency 
    */
#ifdef CONFIG_LATENCYTOP
    int latency_record_count;
    struct latency_record latency_record[LT_SAVECOUNT];
#endif
     
    /*
    61. time slack values，常用于poll和select函数 
    */
    unsigned long timer_slack_ns;
    unsigned long default_timer_slack_ns;

    /*
    62. scm_work_list
    socket控制消息(control message)
    */
    struct list_head    *scm_work_list;

    /*
    63. ftrace跟踪器
    */
#ifdef CONFIG_FUNCTION_GRAPH_TRACER 
    int curr_ret_stack; 
    struct ftrace_ret_stack    *ret_stack; 
    unsigned long long ftrace_timestamp;  
    atomic_t trace_overrun; 
    atomic_t tracing_graph_pause;
#endif
#ifdef CONFIG_TRACING 
    unsigned long trace; 
    unsigned long trace_recursion;
#endif  
};
```

<font style="color:rgb(85, 85, 85);">在 Linux 里面，无论是进程，还是线程，到了内核里面，我们统一都叫任务（Task），由一个统一的结构 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">task_struct</font>`<font style="color:rgb(85, 85, 85);"> 进行管理。这个结构非常复杂，将细细分析</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">task_struct</font>`<font style="color:rgb(85, 85, 85);">结构。主要分析顺序会按照该架构体中的成员变量和函数的作用进行分类，主要包括：</font>

+ <font style="color:rgb(85, 85, 85);">任务ID</font>
+ <font style="color:rgb(85, 85, 85);">亲缘关系</font>
+ <font style="color:rgb(85, 85, 85);">任务状态</font>
+ <font style="color:rgb(85, 85, 85);">任务权限</font>
+ <font style="color:rgb(85, 85, 85);">运行统计</font>
+ <font style="color:rgb(85, 85, 85);">进程调度</font>
+ <font style="color:rgb(85, 85, 85);">信号处理</font>
+ <font style="color:rgb(85, 85, 85);">内存管理</font>
+ <font style="color:rgb(85, 85, 85);">文件与文件系统</font>
+ <font style="color:rgb(85, 85, 85);">内核栈</font>

### <font style="color:rgb(85, 85, 85);">任务ID</font>
```c
pid_t pid;
pid_t tgid;
struct task_struct *group_leader
```

<font style="color:rgb(85, 85, 85);">之所以有</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">pid(process id)</font>`<font style="color:rgb(85, 85, 85);">，</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">tgid(thread group ID)</font>`<font style="color:rgb(85, 85, 85);">以及</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">group_leader</font>`<font style="color:rgb(85, 85, 85);">，是因为线程和进程在内核中是统一管理，视为相同的任务（task）。</font>

<font style="color:rgb(85, 85, 85);">任何一个进程，如果只有主线程，那 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">pid</font>`<font style="color:rgb(85, 85, 85);"> 和</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">tgid</font>`<font style="color:rgb(85, 85, 85);">相同，</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">group_leader</font>`<font style="color:rgb(85, 85, 85);"> 指向自己。但是，如果一个进程创建了其他线程，那就会有所变化了。线程有自己的</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">pid</font>`<font style="color:rgb(85, 85, 85);">，</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">tgid</font>`<font style="color:rgb(85, 85, 85);"> 就是进程的主线程的 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">pid</font>`<font style="color:rgb(85, 85, 85);">，</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">group_leader</font>`<font style="color:rgb(85, 85, 85);"> 指向的进程的主线程。因此根据</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">pid</font>`<font style="color:rgb(85, 85, 85);">和</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">tgid</font>`<font style="color:rgb(85, 85, 85);">是否相等我们可以判断该任务是进程还是线程。</font>

### <font style="color:rgb(85, 85, 85);">亲缘关系</font>
<font style="color:rgb(85, 85, 85);">除了0号进程以外，其他进程都是有父进程的。全部进程其实就是一颗进程树，相关成员变量如下所示</font>

```c
struct task_struct __rcu *real_parent; /* real parent process */
struct task_struct __rcu *parent; /* recipient of SIGCHLD, wait4() reports */
struct list_head children;      /* list of my children */
struct list_head sibling;       /* linkage in my parent's children list */
```

+ `<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">parent</font>`<font style="color:rgb(85, 85, 85);"> 指向其父进程。当它终止时，必须向它的父进程发送信号。</font>
+ `<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">children</font>`<font style="color:rgb(85, 85, 85);"> 指向子进程链表的头部。链表中的所有元素都是它的子进程。</font>
+ `<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">sibling</font>`<font style="color:rgb(85, 85, 85);"> 用于把当前进程插入到兄弟链表中。</font>

<font style="color:rgb(85, 85, 85);">通常情况下，real_parent 和 parent 是一样的，但是也会有另外的情况存在。例如，bash 创建一个进程，那进程的 parent 和 real_parent 就都是 bash。如果在 bash 上使用 GDB 来 debug 一个进程，这个时候 GDB 是 parent，bash 是这个进程的 real_parent。</font>

### <font style="color:rgb(85, 85, 85);">任务状态</font>
```c
volatile long state;    /* -1 unrunnable, 0 runnable, >0 stopped */
int exit_state;
unsigned int flags;
```

<font style="color:rgb(85, 85, 85);">其中状态</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">state</font>`<font style="color:rgb(85, 85, 85);">通过设置比特位的方式来赋值，具体值在</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">include/linux/sched.h</font>`<font style="color:rgb(85, 85, 85);">中定义</font>

```c
/* Used in tsk->state: */
#define TASK_RUNNING                    0
#define TASK_INTERRUPTIBLE              1
#define TASK_UNINTERRUPTIBLE            2
#define __TASK_STOPPED                  4
#define __TASK_TRACED                   8
/* Used in tsk->exit_state: */
#define EXIT_DEAD                       16
#define EXIT_ZOMBIE                     32
#define EXIT_TRACE                      (EXIT_ZOMBIE | EXIT_DEAD)
/* Used in tsk->state again: */
#define TASK_DEAD                       64
#define TASK_WAKEKILL                   128
#define TASK_WAKING                     256
#define TASK_PARKED                     512
#define TASK_NOLOAD                     1024
#define TASK_NEW                        2048
#define TASK_STATE_MAX                  4096

#define TASK_KILLABLE           (TASK_WAKEKILL | TASK_UNINTERRUPTIBLE)
```

`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_RUNNING</font>`<font style="color:rgb(85, 85, 85);">并不是说进程正在运行，而是表示</font>**<font style="color:rgb(85, 85, 85);">进程在时刻准备运行的状态</font>**<font style="color:rgb(85, 85, 85);">。当处于这个状态的进程获得时间片的时候，就是在运行中；如果没有获得时间片，就说明它被其他进程抢占了，在等待再次分配时间片。在运行中的进程，一旦要进行一些 I/O 操作，需要等待 I/O 完毕，这个时候会释放 CPU，进入睡眠状态。</font>

<font style="color:rgb(85, 85, 85);">在 Linux 中，有两种睡眠状态。</font>

+ <font style="color:rgb(85, 85, 85);">一种是</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_INTERRUPTIBLE</font>`<font style="color:rgb(85, 85, 85);">，可中断的睡眠状态。这是一种浅睡眠的状态，也就是说，虽然在睡眠，等待 I/O 完成，但是这个时候一个信号来的时候，进程还是要被唤醒。只不过唤醒后，不是继续刚才的操作，而是进行信号处理。当然程序员可以根据自己的意愿，来写信号处理函数，例如收到某些信号，就放弃等待这个 I/O 操作完成，直接退出；或者收到某些信息，继续等待。</font>
+ <font style="color:rgb(85, 85, 85);">另一种睡眠是</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_UNINTERRUPTIBLE</font>`<font style="color:rgb(85, 85, 85);">，不可中断的睡眠状态。这是一种深度睡眠状态，不可被信号唤醒，只能死等 I/O 操作完成。一旦 I/O 操作因为特殊原因不能完成，这个时候，谁也叫不醒这个进程了。你可能会说，我 kill 它呢？别忘了，kill 本身也是一个信号，既然这个状态不可被信号唤醒，kill 信号也被忽略了。除非重启电脑，没有其他办法。因此，这其实是一个比较危险的事情，除非程序员极其有把握，不然还是不要设置成</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_UNINTERRUPTIBLE</font>`<font style="color:rgb(85, 85, 85);">。</font>
+ <font style="color:rgb(85, 85, 85);">于是，我们就有了一种新的进程睡眠状态，</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_KILLABLE</font>`<font style="color:rgb(85, 85, 85);">，可以终止的新睡眠状态。进程处于这种状态中，它的运行原理类似</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_UNINTERRUPTIBLE</font>`<font style="color:rgb(85, 85, 85);">，只不过可以响应致命信号。由于</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_WAKEKILL</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">用于在接收到致命信号时唤醒进程，因此</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_KILLABLE</font>`<font style="color:rgb(85, 85, 85);">即在</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_UNINTERUPTIBLE</font>`<font style="color:rgb(85, 85, 85);">的基础上增加一个</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_WAKEKILL</font>`<font style="color:rgb(85, 85, 85);">标记位即可。</font>

`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_STOPPED</font>`<font style="color:rgb(85, 85, 85);">是在进程接收到</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">SIGSTOP</font>`<font style="color:rgb(85, 85, 85);">、</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">SIGTTIN</font>`<font style="color:rgb(85, 85, 85);">、</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">SIGTSTP</font>`<font style="color:rgb(85, 85, 85);">或者</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">SIGTTOU</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">信号之后进入该状态。</font>

`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">TASK_TRACED</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">表示进程被 debugger 等进程监视，进程执行被调试程序所停止。当一个进程被另外的进程所监视，每一个信号都会让进程进入该状态。</font>

<font style="color:rgb(85, 85, 85);">一旦一个进程要结束，先进入的是</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">EXIT_ZOMBIE</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">状态，但是这个时候它的父进程还没有使用</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">wait()</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">等系统调用来获知它的终止信息，此时进程就成了僵尸进程。</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">EXIT_DEAD</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">是进程的最终状态。</font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">EXIT_ZOMBIE</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">和</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">EXIT_DEAD</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">也可以用于</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">exit_state</font>`<font style="color:rgb(85, 85, 85);">。</font>

<font style="color:rgb(85, 85, 85);">上面的进程状态和进程的运行、调度有关系，还有其他的一些状态，我们称为标志。放在 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">flags</font>`<font style="color:rgb(85, 85, 85);">字段中，这些字段都被定义成为宏，以 PF 开头。</font>

```c
#define PF_EXITING    0x00000004
#define PF_VCPU      0x00000010
#define PF_FORKNOEXEC    0x00000040
```

`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">PF_EXITING</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">表示正在退出。当有这个 flag 的时候，在函数</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">find_alive_thread()</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">中，找活着的线程，遇到有这个 flag 的，就直接跳过。</font>

`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">PF_VCPU</font>`<font style="color:rgb(85, 85, 85);"> </font><font style="color:rgb(85, 85, 85);">表示进程运行在虚拟 CPU 上。在函数</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">account_system_time</font>`<font style="color:rgb(85, 85, 85);">中，统计进程的系统运行时间，如果有这个 flag，就调用</font><font style="color:rgb(85, 85, 85);"> </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">account_guest_time</font>`<font style="color:rgb(85, 85, 85);">，按照客户机的时间进行统计。</font>

`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">PF_FORKNOEXEC</font>`<font style="color:rgb(85, 85, 85);"> 表示 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">fork</font>`<font style="color:rgb(85, 85, 85);"> 完了，还没有 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">exec</font>`<font style="color:rgb(85, 85, 85);">。在 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">_do_fork ()</font>`<font style="color:rgb(85, 85, 85);">函数里面调用 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">copy_process()</font>`<font style="color:rgb(85, 85, 85);">，这个时候把 flag 设置为 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">PF_FORKNOEXEC()</font>`<font style="color:rgb(85, 85, 85);">。当 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">exec</font>`<font style="color:rgb(85, 85, 85);"> 中调用了 </font>`<font style="color:rgb(85, 85, 85);background-color:rgb(238, 238, 238);">load_elf_binary()</font>`<font style="color:rgb(85, 85, 85);"> 的时候，又把这个 flag 去掉。</font>

### 等等....参考链接[https://ty-chen.github.io/linux-kernel-task-struct/](https://ty-chen.github.io/linux-kernel-task-struct/)
## 参考链接
+ [https://ty-chen.github.io/linux-kernel-task-struct/](https://ty-chen.github.io/linux-kernel-task-struct/)
+ [https://www.51cto.com/article/720464.html](https://www.51cto.com/article/720464.html)
+ [https://huweicai.com/process-thread-goroutine/](https://huweicai.com/process-thread-goroutine/)
+ [https://www.cnblogs.com/Roboduster/p/16622413.html#311-int-0x80](https://www.cnblogs.com/Roboduster/p/16622413.html#311-int-0x80)
+ https://www.zhihu.com/question/35128513/answer/148038406
+ [https://arthurchiao.art/blog/linux-cfs-design-and-implementation-zh/](https://arthurchiao.art/blog/linux-cfs-design-and-implementation-zh/)

# 内核态&用户态&CPU调度
## 用户态与内核态
### CPU指令集与权限铺垫
在说用户态与内核态之前，有必要说一下 CPU指令集，指令集是 CPU 实现软件指挥硬件执行的媒介，具体来说每一条汇编语句都对应了一条CPU指令，而非常非常多的 CPU指令 在一起，可以组成一个、甚至多个集合，指令的集合叫CPU指令集。

同时 CPU指令集 有权限分级，大家试想，CPU指令集 可以直接操作硬件的，要是因为指令操作的不规范，造成的错误会影响整个计算机系统的。好比你写程序，因为对硬件操作不熟悉，导致操作系统内核、及其他所有正在运行的程序，都可能会因为操作失误而受到不可挽回的错误，最后只能重启计算机才行。

而对于硬件的操作是非常复杂的，参数众多，出问题的几率相当大，必须谨慎的进行操作，对开发人员来说是个艰巨的任务，还会增加负担，同时开发人员在这方面也不被信任，所以操作系统内核直接屏蔽开发人员对硬件操作的可能，都不让你碰到这些CPU指令集。

所以CPU指令可以分为如下两大类：

+ **特权指令**：只能由操作系统使用、用户程序不能使用的指令。 举例：启动I/O 内存清零 修改程序状态字 设置时钟 允许/禁止终端 停机
+ **非特权指令**：用户程序可以使用的指令。 举例：控制转移 算数运算 取数指令 访管指令（使用户程序从用户态陷入内核态）

硬件设备商直接提供硬件级别的支持，做法就是对 CPU 指令集设置了权限，不同级别权限能使用的 CPU 指令集 是有限的，以 Inter CPU 为例，Inter 把 CPU指令集 操作的权限由高到低划为4级：

+ ring 0、ring 1、ring 2、ring 3

其中 ring 0 权限最高，可以使用所有 CPU 指令集，相当于内核态；ring 3 权限最低，仅能使用常规 CPU指令集，不能使用操作硬件资源的 CPU 指令集，比如 IO 读写、网卡访问、申请内存都不行，R3相当于用户态；**Linux系统仅采用ring 0 和 ring 3 这2个权限。**

执行内核空间的代码，具有 ring 0 保护级别，有对硬件的所有操作权限，可以执行所有 CPU 指令集，访问任意地址的内存，在内核模式下的任何异常都是灾难性的，将会导致整台机器停机。

在用户模式下，具有ring 3保护级别，代码没有对硬件的直接控制权限，也不能直接访问地址的内存，程序是通过调用系统接口(System Call APIs)来达到访问硬件和内存，在这种保护模式下，即时程序发生崩溃也是可以恢复的，在电脑上大部分程序都是在，用户模式下运行的。

简单点说：

+ ring 0被叫做内核态，完全在操作系统内核中运行
+ ring 3被叫做用户态，在应用程序中运行

通关了 CPU 指令集权限，现在再说用户态与内核态就十分简单了，用户态与内核态的概念就是 CPU 指令集权限的区别，进程中要读写 IO，必然会用到 ring 0 级别的 CPU 指令集，而此时 CPU 的指令集操作权限只有 ring 3，为了可以操作ring 0 级别的 CPU 指令集， CPU 切换指令集操作权限级别为 ring 0，CPU再执行相应的ring 0 级别的 C P U 指令集（内核代码），执行的内核代码会使用当前进程的内核栈。

PS：每个进程都有两个栈，分别是用户栈与内核栈，对应用户态与内核态的使用

### 用户态与内核态空间
在内存资源上的使用，操作系统对用户态与内核态也做了限制，每个进程创建都会分配「虚拟空间地址」，以Linux32位操作系统为例，它的寻址空间范围是 4G（2的32次方），而操作系统会把虚拟控制地址划分为两部分，一部分为内核空间，另一部分为用户空间，高位的 1G（从虚拟地址 0xC0000000 到 0xFFFFFFFF）由内核使用，而低位的 3G（从虚拟地址 0x00000000 到 0xBFFFFFFF）由各个进程使用。

### 用户态与内核态切换
相信大家都听过这样的话「用户态和内核态切换的开销大」，但是它的开销大在那里呢？简单点来说有下面几点

1. 保留用户态现场（上下文、寄存器、用户栈等）
2. 复制用户态参数，用户栈切到内核栈，进入内核态
3. 额外的检查（因为内核代码对用户不信任）
4. 执行内核态代码
5. 复制内核态代码执行结果，回到用户态
6. 恢复用户态现场（上下文、寄存器、用户栈等）

实际上操作系统会比上述的更复杂，这里只是个大概，我们可以发现一次切换经历了「用户态 -> 内核态 -> 用户态」。

用户态要主动切换到内核态，那必须要有入口才行，实际上内核态是提供了统一的入口，下面是Linux整体架构图

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1722969167663-5b3f6eea-00fe-4968-9ffe-0fec90d98b30.png)

> <font style="color:rgb(77, 77, 77);">所谓系统调用，其实就是一些函数，操作系统直接提供了这些函数用于对文件和设备进行访问和控制。</font>
>
> <font style="color:rgb(77, 77, 77);">最常见的就是 read 和 write 这俩</font>
>
> <font style="color:rgb(77, 77, 77);">简单介绍下：</font>
>
> + <font style="color:rgba(0, 0, 0, 0.75);">read：从文件中读取内容</font>
> + <font style="color:rgba(0, 0, 0, 0.75);">write：往文件中写入内容</font>
>
> <font style="color:rgba(0, 0, 0, 0.75);">系统调用的汇编代码是 </font>**<font style="color:rgba(0, 0, 0, 0.75);">syscall</font>**
>

从上图我们可以看出来通过系统调用将Linux整个体系分为用户态和内核态，为了使应用程序访问到内核的资源，如CPU、内存、I/O，内核必须提供一组通用的访问接口，这些接口就叫系统调用。

库函数就是屏蔽这些复杂的底层实现细节，减轻程序员的负担，从而更加关注上层的逻辑实现，它对系统调用进行封装，提供简单的基本接口给程序员。

Shell顾名思义，就是外壳的意思，就好像把内核包裹起来的外壳，它是一种特殊的应用程序，俗称命令行。Shell也是可编程的，它有标准的Shell 语法，符合其语法的文本叫Shell脚本，很多人都会用Shell脚本实现一些常用的功能，可以提高工作效率。

最后来说说，什么情况会导致用户态到内核态切换

+ 系统调用：用户态进程主动切换到内核态的方式，用户态进程通过系统调用向操作系统申请资源完成工作，例如 fork（）就是一个创建新进程的系统调用，系统调用的机制核心使用了操作系统为用户特别开放的一个中断来实现，如Linux 的 int 80h 中断，也可以称为软中断
+ 异常：当 C P U 在执行用户态的进程时，发生了一些没有预知的异常，这时当前运行进程会切换到处理此异常的内核相关进程中，也就是切换到了内核态，如缺页异常
+ 中断：当 CPU 在执行用户态的进程时，外围设备完成用户请求的操作后，会向 CPU 发出相应的中断信号，这时 CPU 会暂停执行下一条即将要执行的指令，转到与中断信号对应的处理程序去执行，也就是切换到了内核态。如硬盘读写操作完成，系统会切换到硬盘读写的中断处理程序中执行后边的操作等。

### write 如何从用户态切换到内核态
有如下代码

```c
#include <unistd.h>
int main(){

    char msg[] = "hello world\n";

    write(STDOUT_FILENO, msg, sizeof(msg));

}
```

在 linux 中调试运行 gdb ./c_demo1，进入 gdb 中

```c
// 在write处打断点
(gdb) break write
Breakpoint 1 at 0x1060
    
// 调试运行
(gdb) run
Starting program: /root/c_demo/cmake-build-debug/c_demo1 
Breakpoint 1, __GI___libc_write (fd=1, buf=0x7fffffffe3bb, nbytes=13) at ../sysdeps/unix/sysv/linux/write.c:25
25	../sysdeps/unix/sysv/linux/write.c: No such file or directory.

// 反汇编
(gdb) disassemble $pc,+500
Dump of assembler code from 0x7ffff7edd280 to 0x7ffff7edd474:
=> 0x00007ffff7edd280 <__GI___libc_write+0>:	endbr64 
   0x00007ffff7edd284 <__GI___libc_write+4>:	mov    %fs:0x18,%eax
   0x00007ffff7edd28c <__GI___libc_write+12>:	test   %eax,%eax
   0x00007ffff7edd28e <__GI___libc_write+14>:	jne    0x7ffff7edd2a0 <__GI___libc_write+32>
   0x00007ffff7edd290 <__GI___libc_write+16>:	mov    $0x1,%eax
   0x00007ffff7edd295 <__GI___libc_write+21>:	syscall 
   0x00007ffff7edd297 <__GI___libc_write+23>:	cmp    $0xfffffffffffff000,%rax
   0x00007ffff7edd29d <__GI___libc_write+29>:	ja     0x7ffff7edd2f0 <__GI___libc_write+112>
   0x00007ffff7edd29f <__GI___libc_write+31>:	retq   
   0x00007ffff7edd2a0 <__GI___libc_write+32>:	sub    $0x28,%rsp
   0x00007ffff7edd2a4 <__GI___libc_write+36>:	mov    %rdx,0x18(%rsp)
   0x00007ffff7edd2a9 <__GI___libc_write+41>:	mov    %rsi,0x10(%rsp)
   0x00007ffff7edd2ae <__GI___libc_write+46>:	mov    %edi,0x8(%rsp)
   0x00007ffff7edd2b2 <__GI___libc_write+50>:	callq  0x7ffff7e635e0 <__libc_enable_asynccancel>
   0x00007ffff7edd2b7 <__GI___libc_write+55>:	mov    0x18(%rsp),%rdx
   0x00007ffff7edd2bc <__GI___libc_write+60>:	mov    0x10(%rsp),%rsi
   0x00007ffff7edd2c1 <__GI___libc_write+65>:	mov    %eax,%r8d
   0x00007ffff7edd2c4 <__GI___libc_write+68>:	mov    0x8(%rsp),%edi
   0x00007ffff7edd2c8 <__GI___libc_write+72>:	mov    $0x1,%eax
   0x00007ffff7edd2cd <__GI___libc_write+77>:	syscall 
   0x00007ffff7edd2cf <__GI___libc_write+79>:	cmp    $0xfffffffffffff000,%rax
   0x00007ffff7edd2d5 <__GI___libc_write+85>:	ja     0x7ffff7edd304 <__GI___libc_write+132>
   0x00007ffff7edd2d7 <__GI___libc_write+87>:	mov    %r8d,%edi
   0x00007ffff7edd2da <__GI___libc_write+90>:	mov    %rax,0x8(%rsp)

```

+ [https://www.bilibili.com/video/BV1VN4y1Q7Dk](https://www.bilibili.com/video/BV1VN4y1Q7Dk)

## 系统调用
Linux 查看系统调用的API`man syscall`

## CPU调度
<font style="color:rgb(34, 34, 34);">进程是由操作系统内核来管理和调度的，进程的切换只能发生在内核态。</font>

## 参考链接
+ [https://blog.csdn.net/m0_37199770/article/details/113482312](https://blog.csdn.net/m0_37199770/article/details/113482312)
+ [https://www.cnblogs.com/crazymakercircle/p/15546325.html#autoid-h2-15-0-1](https://www.cnblogs.com/crazymakercircle/p/15546325.html#autoid-h2-15-0-1)

# 缓冲区
缓冲区分为**用户缓冲区**和**内核缓冲区**

+ <font style="color:rgba(0, 0, 0, 0.75);">操作系统内核能够访问的内存区域呢，就称为</font>**<font style="color:rgba(0, 0, 0, 0.75);">内核空间</font>**<font style="color:rgba(0, 0, 0, 0.75);">，它独立于普通的应用程序，是受保护的内存空间。</font>
+ <font style="color:rgba(0, 0, 0, 0.75);">而普通应用程序可访问的内存区域呢，就是</font>**<font style="color:rgba(0, 0, 0, 0.75);">用户空间</font>**

**<font style="color:#DF2A3F;">内核缓冲区处理的是内核空间和磁盘之间的数据传递，目的是减少访问磁盘的次数；</font>**

**<font style="color:#DF2A3F;">用户缓冲区处理的是用户空间和内核空间的数据传递，目的是减少系统调用的次数。</font>**

## <font style="color:rgba(0, 0, 0, 0.75);">内核缓冲区</font>
<font style="color:rgb(77, 77, 77);">当我们说一个应用程序从磁盘上读取文件时，通常分两步走：</font>

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723019370774-16b78094-35e6-4b67-b86b-018e63f42b52.png)

<font style="color:rgb(77, 77, 77);">操作系统（内核）先从磁盘上读取数据存到内核空间，再把数据从内核空间拷贝到用户空间。此后，用户应用程序才可以操作此数据。</font>

所以，在这个过程中有两次数据读取操作：

1. 从磁盘上读取
2. 从内存中读取

众所周知，访问磁盘的速度要远远低于访问内存的速度，完全不是一个量级的，所以理论上 read 磁盘的速度要远远慢于 read 内存。那么整个文件读取过程的最大时间瓶颈就出现在了对磁盘的读取上。要解决这个问题，内核缓冲区（Kernel Buffer Cache）就应运而生了。

本质上其实就是内核空间的一块内存区域罢了

从 Buffer Cache（缓冲区缓存）这个名字上能看出来，内核缓冲区（准确的说，应该是内核缓冲区缓存），其实有两个作用，缓冲(Buffer) + 缓存(Cache)

+ **先来看看它是怎么充当 Cache 的：**

⭐ 数据预读

数据预读指的是，当程序发起 read() 系统调用时，内核会比请求更多地读取磁盘上的数据，保存在缓冲区，以备程序后续使用。这种数据的预取策略其实就是基于局部性原理。

因此当我们向内核请求读取数据时，内核会先到内核缓冲区中去寻找，如果命中数据，则不需要进行真正的磁盘 I/O，直接从缓冲区中返回数据就行了；如果缓存未命中，则内核会从磁盘中读取请求的 page，并同时读取紧随其后的几个 page（比如三个），如果文件是顺序访问的，那么下一个读取请求就会命中之前预读的缓存（当然了，预读算法非常复杂，这里只是一个简化的逻辑）。

+ **再来看看内核缓冲区是怎么充当 Buffer 的：**

⭐ 延时回写

回写指的是，当程序发起 write() 系统调用时，内核并不会直接把数据写入到磁盘文件中，而仅仅是写入到缓冲区中，几秒后（或者说等数据堆积了一些后）才会真正将数据刷新到磁盘中。对于系统调用来说，数据写入缓冲区后，就返回了。延迟往磁盘写入数据的最大一个好处就是，可以合并更多的数据一次性写入磁盘，把小块的 I/O 变成大块 I/O，减少磁盘处理命令次数，从而提高提盘性能。另一个好处是，当其它进程紧接着访问该文件时，内核可以从直接从缓冲区中提供更新的文件数据（这里又是充当 Cache 了）。

> 说起来一大堆，其实很简单，把握缓冲和缓存的定义就行了，如果你是读，我就会拿多一点放在内核缓冲区，这样你下次读的时候大概率就不需要访问磁盘了，直接从内核缓冲区拿就行；如果你是写，我就会等内核缓冲区中的数据堆积得多了再写磁盘，而不是来一点数据就写一次磁盘。
>
> 无论是读操作还是写操作，无论是充当缓存还是缓冲，究其根本，内核缓冲区的作用都是为了减少磁盘 IO 的次数
>

## 用户缓冲区
上面提到，read 磁盘的速度要远远慢于 read 内存，不过事实上，read 内存这个操作也挺费时的，因为应用程序想要访问系统资源的话，就要通过系统调用 or 中断（外中断、内中断）使得 CPU 从用户态转向内核态，这个状态的改变需要涉及堆栈的环境和数据变化，还是挺需要时间的。**<font style="color:#DF2A3F;">用户缓冲区是为了减少系统调用的发生（或者说，减少用户态和内核态的转换次数）</font>**，就设计了用户缓冲区。

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725178870461-e058b1d6-91f6-4dce-a25e-556465aebdca.png)

<font style="color:rgb(77, 77, 77);">另外，从上面的分析我们可以看出，read() 和 write() 都并非真正执行 I/O 操作（或者说，都并不直接和磁盘进行交互），它只代表数据在用户空间 / 内核空间传递的完成，read 是把数据从内核缓冲区复制到用户缓冲区，write 是把用户缓冲区复制到内核缓冲区。</font>

### <font style="color:rgb(77, 77, 77);">C 语言printf()缓冲策略</font>
当我们调用 printf() 函数时，数据写到我们语言层面的用户缓冲区，随后调用 系统调用 write 方法。数据从用户缓冲区写入到内核缓冲区中。

:::tips
**行缓冲（Line Buffering）：**在行缓冲模式下，缓冲区在遇到换行符 \n 时自动刷新。也就是说，当遇到换行符时，缓冲区中的数据会被立即写入文件

**全缓冲（Fully Buffered）：**在全缓冲模式下，缓冲区满时会触发刷新,此时缓冲区中的数据才会被写入文件

**手动刷新缓冲区**：使用 fflush() 函数手动刷新缓冲区。这对于确保数据在特定时刻被写入文件很有用

**关闭文件时刷新**：当文件关闭时，C 库会自动刷新缓冲区

:::

**<font style="color:rgb(28, 115, 49);">C语言的缓冲区在哪里?</font>**

```c
int main(){
    // C 语言提供的接口
    printf("this is c interface \n");

    const char* str = "this is os interface\n";
    
    // linux 系统提供的接口，数据直接从用户空间传递到内核空间缓冲区，没有语言层面的用户缓冲区。
    write(1, str, strlen(str));

    // 创建子进程
    fork();
}
```

当我们在 linux 中执行 ./c_demo 运行编译好的程序，得到结果

```c
./c_demo
this is c interface 
this is os interface
```

当我们将输出重定向到 hello.txt 文件中，得到结果

```c
./c_demo > /root/c_demo/hello.txt
cat /root/c_demo/hello.txt
this is os interface
this is c interface 
this is c interface 
```

我们发现 C 函数提供的方法将结果输出了两次，此时我们在创建子进程前强制刷新缓冲区

```c
int main(){
    printf("this is c interface \n");

    const char* str = "this is os interface\n";
    write(1, str, strlen(str));

    //强制刷新缓冲区
    fflush(stdout);

    fork();
}
```

运行结果

```c
./c_demo > /root/c_demo/hello.txt
cat /root/c_demo/hello.txt
this is os interface
this is c interface 
```

如此我们可以看出，**C 语言 printf() 自己维护了一个缓冲区(用户缓冲区)**

我们找到 **stdio.h **头文件，其中有一个 FILE 结构体，里面封装了 fd 和 该文件描述符所对应的缓冲区结构 _IO_FILE

```c
struct _IO_FILE {
  int _flags;                /* 文件状态标志 */
  char* _IO_read_ptr;        /* 当前读取指针 */
  char* _IO_read_end;        /* 读取区结束 */
  char* _IO_read_base;       /* 回退区的起始位置 */
  char* _IO_write_base;      /* 写入区起始位置 */
  char* _IO_write_ptr;       /* 当前写入指针 */
  char* _IO_write_end;       /* 写入区结束 */
  char* _IO_buf_base;        /* 缓存区起始位置 */
  char* _IO_buf_end;         /* 缓存区结束 */
  char *_IO_save_base;       /* 非当前读取区的起始指针 */
  char *_IO_backup_base;     /* 备份区第一个有效字符的指针 */
  char *_IO_save_end;        /* 非当前读取区结束指针 */

  int _fileno;               /* 文件描述符 */

};
```

回到我们的最初的代码

我们直接运行程序是向显示器中打印，采用的是行刷新策略，而我们重定向到文件中，向文件中打印，便成了全缓冲策略

1.如果是向显示器中打印，那么采用的是行刷新策略，那么最后执行fork的时候，所有的数据都已经刷新完成了，此时再执行fork就没有意义了

2.如果对程序进行了重定向，即此时要向文件中打印，此时刷新策略便隐式的变成了全缓冲，遇到\n换行符便不会再触发刷新

上述代码在我们 fork 的时候，函数已经执行完了，但是数据还没有刷新，这些数据在当前进程对应的C标准库中的缓冲区里，并且这些数据是父进程的.

> <font style="color:rgb(51, 51, 51);">在</font>`<font style="color:rgb(51, 51, 51);">fork()</font>`<font style="color:rgb(51, 51, 51);">的调用处，‌整个父进程的空间（‌包括指令、‌变量值、‌程序调用栈、‌环境变量、‌缓冲区、代开的文件描述符等）‌会被原模原样地复制到子进程中。‌</font>
>

当进程结束时，根据全缓冲策略，刷新缓冲区，所以 C 标准库函数打印了两次。

## 零拷贝
+ [https://www.bilibili.com/video/BV1gq4y1S7Xt](https://www.bilibili.com/video/BV1gq4y1S7Xt)
+ [https://www.zhihu.com/question/634419059/answer/3562575621](https://www.zhihu.com/question/634419059/answer/3562575621)

## 参考链接
+ [https://blog.csdn.net/m0_51429770/article/details/129346324](https://blog.csdn.net/m0_51429770/article/details/129346324)
+ [https://blog.csdn.net/m110572/article/details/125236240](https://blog.csdn.net/m110572/article/details/125236240)
+ [https://www.bilibili.com/video/BV1gq4y1S7Xt](https://www.bilibili.com/video/BV1gq4y1S7Xt)

# 文件描述符
## 文件描述符表
Linux的进程控制块PCB（process control block）本质是一个叫做 task_struct 的结构体，里边包括管理进程所需的各种信息，其中有一个结构体叫做

```c
struct task_struct{
    /* Open file information: */
    struct files_struct		*files;
}
```

其中 files_struct 定义在 `<font style="color:rgb(51, 51, 51);">include/linux/fdtable.h</font>`<font style="color:rgb(51, 51, 51);">中</font>

```c
struct files_struct {
  /*
   * read mostly part
   */
	atomic_t count;
	bool resize_in_progress;
	wait_queue_head_t resize_wait;

    /*
        RCU数据结构用于并发控制
         __rcu 是一个特殊的宏
         表示这个指针是一个 RCU（Read-Copy-Update）指针
    */
	struct fdtable __rcu *fdt;
	struct fdtable fdtab;
  /*
   * written part on a separate cache line in SMP
   */
	spinlock_t file_lock ____cacheline_aligned_in_smp;
	unsigned int next_fd;
	unsigned long close_on_exec_init[1];
	unsigned long open_fds_init[1];
	unsigned long full_fds_bits_init[1];
	struct file __rcu * fd_array[NR_OPEN_DEFAULT];
};
```

>  使用 RCU 机制的好处在于它允许多个读者并发访问数据，而无需加锁，这对于文件描述符表这种频繁访问的结构体特别重要。只有在需要修改结构体时，才会进行更新，从而提高了性能和并发性。  
>

其中 fdtable 也定义在 `include/linux/fdtable.h`中

```c
struct fdtable {
	unsigned int max_fds;
	struct file __rcu **fd;      /* current fd array */
	unsigned long *close_on_exec;
	unsigned long *open_fds;
	unsigned long *full_fds_bits;
	struct rcu_head rcu;
};
```

我们将它叫做文件描述符表，内核为每一个进程维护了一个文件描述符表，索引表中的值都是从 0 开始的。

其中 file 定义在 `include/linux/fs.h`中

```c
struct file {
	union {
		/* fput() uses task work when closing and freeing file (default). */
		struct callback_head 	f_task_work;
		/* fput() must use workqueue (most kernel threads). */
		struct llist_node	f_llist;
		unsigned int 		f_iocb_flags;
	};

	/*
	 * Protects f_ep, f_flags.
	 * Must not be taken from IRQ context.
	 */
	spinlock_t		f_lock;
	fmode_t			f_mode;
	atomic_long_t		f_count;
	struct mutex		f_pos_lock;
	loff_t			f_pos;
	unsigned int		f_flags;
	struct fown_struct	f_owner;
	const struct cred	*f_cred;
	struct file_ra_state	f_ra;
	struct path		f_path;
	struct inode		*f_inode;	/* cached value */
	const struct file_operations	*f_op;

	u64			f_version;
#ifdef CONFIG_SECURITY
	void			*f_security;
#endif
	/* needed for tty driver, and maybe others */
	void			*private_data;

#ifdef CONFIG_EPOLL
	/* Used by fs/eventpoll.c to link all the hooks to this file */
	struct hlist_head	*f_ep;
#endif /* #ifdef CONFIG_EPOLL */
	struct address_space	*f_mapping;
	errseq_t		f_wb_err;
	errseq_t		f_sb_err; /* for syncfs */
} __randomize_layout
  __attribute__((aligned(4)));	/* lest something weird decides that 2 is OK */
```

## 文件描述符
当进程打开一个文件时，操作系统会分配一个文件描述符，并创建一个对应的 `struct file`实例。文件描述符作为索引，指向内核中的`struct file`结构体，从而使得进程能够通过文件描述符与打开的文件进行交互。

例如：在内核中，当你用 `int fd = open("file.txt", O_RDONLY);` 打开一个文件时，`fd` 是文件描述符，而内核会在内部创建一个 `struct file` 实例来表示这个打开的文件，所有后续对该文件的操作（如读取、写入）都将通过这个 `struct file` 实例来进行。

<font style="color:rgb(64, 62, 62);">文件描述符就是内核为了高效管理这些已经被打开的文件所创建的索引，其是一个非负整数（通常是小整数），用于指代被打开的文件，所有执行I/O操作的系统调用都通过文件描述符来实现。</font>

每个进程在创建时会自动打开三个标准的文件描述符：

1. **标准输入（stdin）**<font style="color:rgb(64, 62, 62);">：文件描述符 0，用于从用户或其他进程读取输入。</font>
2. **标准输出（stdout）**<font style="color:rgb(64, 62, 62);">：文件描述符 1，用于向用户或其他进程输出数据。</font>
3. **标准错误（stderr）**<font style="color:rgb(64, 62, 62);">：文件描述符 2，用于输出错误消息。</font>

<font style="color:rgb(77, 77, 77);">文件描述符的分配规则：</font>**<font style="color:rgb(255, 153, 0);">在文件描述符数组当中，找到当前没有被使用的最小的一个下标，作为新的文件描述符。</font>**  
<font style="color:rgb(51, 51, 51);">文件描述符存储在文件描述符表（File descriptor table）</font>

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1722788677134-33615661-b093-493e-abff-91f58e80db41.png)![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1722790943408-a96c1bee-7f43-4ff2-acca-85a4810e5774.png)

**常见的文件描述符：**

+ `0`：标准输入（stdin）
+ `1`：标准输出（stdout）
+ `2`：标准错误（stderr）
+ `cwd`：当前工作目录（current work directory）
+ `txt`：程序的文本段（代码段）
+ `rtd`：根目录
+ `mem`：内存映射的文件
+ `mmap`：内存映射设备
+ **类型修饰符**：
    - `r`：表示文件以只读模式打开（read）
    - `w`：表示文件以只写模式打开（write）
    - `u`：表示文件以读写模式打开（update）
    - `a`：表示文件以追加模式打开（append）
+ **状态修饰符**：
    - `t`：表示文件被共享（文本段）
    - `x`：表示文件为程序文本段（代码段）
    - `m`：表示文件为内存映射文件
    - `L`：表示文件有专用锁
    - `N`：表示文件有非阻塞模式
    - `D`：表示文件为内存映射设备
    - `T`：表示文件的文件锁（File lock）

**详细展示：**

当我们编写如下 C 程序执行在 Linux 中

```c
#include <stdio.h>
#include <fcntl.h>

int main() {

    const int fd1 = open("/root/c_demo/hello.txt", O_RDONLY);
    const int fd2 = open("/root/c_demo/world.txt", O_RDONLY);

    printf(" %d\n %d\n", fd1, fd2);
    fflush(stdout); // 强制刷新缓冲区，输出文件描述符 fd 指针

    getchar(); // 让程序暂停
    return 0;
}
```

运行结果

```c
 3
 4
```

我们打开Linux 执行 ps -e，找到这段代码的进程是 57089，执行 lsof -p 57089

```c
COMMAND   PID USER   FD   TYPE DEVICE SIZE/OFF    NODE NAME
c_demo  57507 root  cwd    DIR  253,0     4096 1050203 /root/c_demo/cmake-build-debug
c_demo  57507 root  rtd    DIR  253,0     4096       2 /
c_demo  57507 root  txt    REG  253,0    19440 1050249 /root/c_demo/cmake-build-debug/c_demo
c_demo  57507 root  mem    REG  253,0  2029592  798748 /usr/lib/x86_64-linux-gnu/libc-2.31.so
c_demo  57507 root  mem    REG  253,0   191504  798744 /usr/lib/x86_64-linux-gnu/ld-2.31.so
c_demo  57507 root    0r  FIFO   0,13      0t0  306828 pipe
c_demo  57507 root    1w  FIFO   0,13      0t0  306829 pipe
c_demo  57507 root    2w  FIFO   0,13      0t0  306830 pipe
c_demo  57507 root    3r   REG  253,0       11 1050155 /root/c_demo/hello.txt
c_demo  57507 root    4r   REG  253,0       13 1050233 /root/c_demo/world.txt

// hello.txt fd == 3
// world.txt fd == 4
```

## 重定向
**常见的重定向**

:::tips
1.`>`：输出重定向

        示例：command > output.txt

        描述：将命令的标准输出重定向到文件 output.txt 中。如果文件不存在，则会创建文件；如果文件已存在，将会覆盖其中的内容。

2.`>>`：追加输出重定向

        示例：command >> output.txt

        描述：将命令的标准输出追加到文件 output.txt 中。如果文件不存在，则会创建文件；如果文件已存在，内容将会被追加到文件末尾。

3.`<`：输入重定向

        示例：command < input.txt

        描述：将文件 input.txt 中的内容作为命令的标准输入。命令将读取文件的内容而不是从键盘读取。

4.`|`：管道

        示例：command1 | command2

        描述：将 command1 的标准输出通过管道传递给 command2 的标准输入。这使得两个命令可以协作处理数据。

5.`2>`：错误输出重定向

        示例：command 2> error.txt

        描述：将命令的错误输出重定向到文件 error.txt 中。类似于 >，但是针对错误输出。

6.`&>` 或 `&>>`：合并输出和错误输出重定向

        示例：command &> output_and_error.txt 或 command &>> output_and_error.txt

        描述：将命令的标准输出和错误输出合并，并重定向到文件 output_and_error.txt 中。

:::

**重定向原理**

<font style="color:rgb(77, 77, 77);">重定向的本质是通过操作系统提供的文件描述符机制，动态地改变进程的输入和输出源</font>

我们知道 fd = 1，是标准输出流，但是当我们关闭掉标准输出流。再进行输出的时候，数据将会走向哪里呢？

```c
int main(){
    const int fd_stdout = 1;
    
    printf("brfore close ...\n");
    fflush(stdout);
    
    close(fd_stdout);
    
    printf("after close ...\n");
    fflush(stdout);
}
```

运行结果，我们发现当标准输出关闭掉之后，after close 无法输出

```c
brfore close ...
```

此时如果我们打开一个新的 file/socket/channel/stream，根据文件描述符数组下标的分配规则，**stdout 的标识符 2 将被新的 file/socket/channel/stream 替代，字符串将输出将输出到替代位置**

```c
int main(){
    const int fd_stdout = 1;

    printf("brfore close ...\n");
    fflush(stdout);
    close(fd_stdout);

    // 打开新的文件，替代标准输出位置
    const int new_fd = open("/root/c_demo/hello.txt", O_RDWR, 0644);

    printf("after close ...\n");
    printf("new_fd:%d", new_fd);
    fflush(stdout);
}
```

**控制台输出**

```c
brfore close
```



**hello.txt文件内容**

```c
after close ...
new_fd:1
```

**<font style="color:#DF2A3F;">这个就是输出的重定向原理，操作系统为我们提供一个函数专门用于重定向</font>**

```c
函数原型如下:
 
#include <unistd.h>
 
int dup2(int oldfd, int newfd);
```

**参数**

+ `**oldfd**`：要复制的文件描述符
+ `**newfd**`：要复制到的目标文件描述符

**返回值**

+ 成功时，返回新的文件描述符 `newfd`
+ 失败时，返回 -1，并设置 `errno` 来指示错误原因

**功能**

+ 如果 `newfd` 已经打开，`dup2` 会先关闭它，然后将 `oldfd` 复制到 `newfd`
+ 如果 `oldfd` 和 `newfd` 相同，则不执行任何操作

```c
int main(){
    printf("before duplicate ... \n");
    fflush(stdout);
    const int new_fd = open("/root/c_demo/hello.txt", O_RDWR);
    dup2(new_fd, 1);
    printf("after duplicate\n");
    fflush(stdout);
}
```

**控制台输出**

```c
before duplicate ... 
```

**hello.txt文件内容**

```c
after duplicate
```

> <font style="color:rgb(77, 77, 77);">printf 是 C 库当中的 IO 函数，一般往 stdout 中输出，但是 stdout 底层访问文件的时候，找的还是fd:1，但是 fd:1 下标所表示内容，变成了 file 的地址，不再是显示器文件的地址，所以输出的任何消息都会往文件中写入，进而完成输出重定向。</font>
>



## 参考链接
+ [https://blog.csdn.net/yushuaigee/article/details/107883964](https://blog.csdn.net/yushuaigee/article/details/107883964)
+ [https://blog.csdn.net/qq_64446981/article/details/135191090](https://blog.csdn.net/qq_64446981/article/details/135191090)
+ [https://blog.csdn.net/sdhajkdghjkawhd/article/details/134675215](https://blog.csdn.net/sdhajkdghjkawhd/article/details/134675215)
+ [https://subingwen.cn/linux/file-descriptor/#2-1-%E6%96%87%E4%BB%B6%E6%8F%8F%E8%BF%B0%E7%AC%A6](https://subingwen.cn/linux/file-descriptor/#2-1-%E6%96%87%E4%BB%B6%E6%8F%8F%E8%BF%B0%E7%AC%A6)
+ [https://blog.csdn.net/metersun/article/details/80513702](https://blog.csdn.net/metersun/article/details/80513702)

# <font style="color:rgb(0, 0, 0);">unistd.h#read</font>
**<font style="color:rgb(51, 51, 51);">unistd.h</font>**<font style="color:rgb(51, 51, 51);"> 是 C 和 C++ 程序设计语言中提供对 </font>[<font style="color:rgb(51, 51, 51);">POSIX</font>](https://baike.baidu.com/item/POSIX/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 操作系统 </font>[<font style="color:rgb(51, 51, 51);">API</font>](https://baike.baidu.com/item/API/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 的访问功能的</font>[<font style="color:rgb(51, 51, 51);">头文件</font>](https://baike.baidu.com/item/%E5%A4%B4%E6%96%87%E4%BB%B6/10978258?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);">的名称。该头文件由 POSIX.1 标准（可移植</font>[<font style="color:rgb(51, 51, 51);">系统接口</font>](https://baike.baidu.com/item/%E7%B3%BB%E7%BB%9F%E6%8E%A5%E5%8F%A3/56363892?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);">）提出，故所有遵循该标准的操作系统和</font>[<font style="color:rgb(51, 51, 51);">编译器</font>](https://baike.baidu.com/item/%E7%BC%96%E8%AF%91%E5%99%A8/8853067?fromModule=lemma_inlink)[<font style="color:rgb(51, 51, 51);">均应</font>](https://baike.baidu.com/item/%E5%9D%87%E5%BA%94/23111305?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);">提供该头文件（如 Unix 的所有官方版本，包括 </font>[<font style="color:rgb(51, 51, 51);">Mac OS X</font>](https://baike.baidu.com/item/Mac%20OS%20X/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);">、</font>[<font style="color:rgb(51, 51, 51);">Linux</font>](https://baike.baidu.com/item/Linux/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 等）。</font>

<font style="color:rgb(51, 51, 51);">对于类 Unix 系统，unistd.h 中所定义的接口通常都是大量针对</font>[<font style="color:rgb(51, 51, 51);">系统调用</font>](https://baike.baidu.com/item/%E7%B3%BB%E7%BB%9F%E8%B0%83%E7%94%A8/861110?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);">的封装（英语：wrapper functions），如 </font>[<font style="color:rgb(51, 51, 51);">fork</font>](https://baike.baidu.com/item/fork/7143171?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);">、pipe 以及各种 </font>[<font style="color:rgb(51, 51, 51);">I/O</font>](https://baike.baidu.com/item/I%2FO/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 原语（read、write、</font>[<font style="color:rgb(51, 51, 51);">close</font>](https://baike.baidu.com/item/close/3948219?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 等等）。</font>

<font style="color:rgb(51, 51, 51);">类似于 </font>[<font style="color:rgb(51, 51, 51);">Cygwin</font>](https://baike.baidu.com/item/Cygwin/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 和 </font>[<font style="color:rgb(51, 51, 51);">MinGW</font>](https://baike.baidu.com/item/MinGW/0?fromModule=lemma_inlink)<font style="color:rgb(51, 51, 51);"> 的 Unix 兼容层也提供相应版本的 unistd.h。</font>

# 网络IO
## socket
位于`#include <sys/socket.h>`中，以下函数 Linux 系统调用函数（除了send，recv）

```plain
socket()                    : 创建套接字
bind()                      : 绑定套接字到本地地址
listen()                    : 监听网络连接
accept()                    : 接受网络连接
connect()                   : 连接到远程主机

send(), recv()              : 发送和接收数据（面向连接的套接字）
sendto(), recvfrom()        : 发送和接收数据（无连接的套接字）

// 用read ，write方法来读和写也行，适用范围更广

close() ,shutdown()         : 关闭套接字
getsockopt(), setsockopt()  : 获取和设置套接字选项
```

```c
recv() 是一个用户空间的函数，recvfrom() 是一个系统调用函数

ssize_t recv(int sockfd, void *buf, size_t len, int flags) {
    return recvfrom(sockfd, buf, len, flags, NULL, NULL);
}
ssize_t recvfrom(int sockfd, void *buf, size_t len, int flags, struct sockaddr *src_addr, socklen_t *addrlen);
```

### 基本使用
创建一个服务端

```c
int main(){
    // AF_INET：ipv4 地址；SOCK_STREAM：面向 TCP 的 socket stream,protocol；认协议，根据前面的选择而来 TCP
    int socket_fd = socket(AF_INET, SOCK_STREAM, 0);

    struct sockaddr_in server_addr = {0};
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(55660);
    server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0

    if(bind(socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("绑定失败\n");
    }

    if (listen(socket_fd, 255) == -1) {
        perror("监听失败\n");
    }

    int client_fd = accept(socket_fd, NULL, NULL);
    if (client_fd == -1) {
        perror("接收失败\n");
    }

    printf("等待客户端输入....\n");
    fflush(stdout);
    char buf[50];
    /*
        read方法，recv方法都能够从socket中读取数据，但是recv方法是专门用来读取socket中数据的函数
        相比read方法有更多的参数，read方法是一个通用的系统调用
    */
    read(client_fd, buf, sizeof(buf));

    printf("%s", buf);
    fflush(stdout);
    return 0;
}
```

Java 客户端

```c
public static void main(String[] args) throws IOException {
    Socket socket = new Socket("192.168.75.128", 55660);

    OutputStream outputStream = socket.getOutputStream();
    PrintWriter writer = new PrintWriter(outputStream);
    
    writer.println("this is java client");
    writer.flush();
}
```

### 简答原理图
![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723045733932-45dd0851-1c33-4545-98b8-4617a94a5222.webp)

以 read 为例

**<font style="color:rgb(51, 51, 51);">第一步：等待网络数据的到来</font>**

<font style="color:rgb(51, 51, 51);">当网络数据到达时，网络接口卡（NIC）首先通过直接内存访问（DMA）将数据传输到内核空间分配的 socket 接收缓冲区中，无需 CPU 参与。</font>

**<font style="color:rgb(51, 51, 51);">第二步：CPU 复制数据至用户空间</font>**

<font style="color:rgb(51, 51, 51);">一旦数据通过 DMA 传输到内核的 socket 接收缓冲区，用户进程的 read 系统调用会被唤醒（如果它在等待数据的话）。接下来，CPU 会介入，将数据从内核缓冲区复制到用户空间提供的缓冲区中。</font>

<font style="color:rgb(51, 51, 51);">也就是说，在 I/O 操作的过程中，</font>**<font style="color:rgb(51, 51, 51);">存在两个潜在的等待时间点 ：一个是等待网络数据到达 socket 接收缓冲区，另一个是等待 CPU 复制数据至用户空间。</font>**

![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723047445483-d7900fe2-b5c1-4917-a349-0ff4416e1ee1.jpeg)

### listen()方法
`listen` 方法的作用是将一个已创建的套接字（socket）设置为监听状态，以便接受传入的连接请求。具体来说，它有以下几个关键作用：

1. **设置监听状态**：在调用 `listen` 之前，套接字处于未连接状态。调用 `listen` 后，该套接字可以接受客户端的连接请求。
2. **定义连接队列长度**：`listen` 的第二个参数指定了未决连接的最大数量，也就是允许排队的连接请求的数量。当客户端尝试连接时，如果在此队列中的连接数已满，新的连接请求可能会被拒绝。
3. **为后续的 **`**accept**`** 准备**：`listen` 方法的主要目的是准备好接收客户端的连接。之后，程序通常会调用 `accept` 函数来实际接受连接。

#### 原理
```c
#include <sys/socket.h>
// 成功返回0,错误返回-1,同时错误码设置在errno
int listen(int sockfd, int backlog);
```

<font style="color:rgb(36, 41, 46);">注意，这边的 listen 调用是被 glibc 的 INLINE_SYSCALL 装过一层，其将返回值修正为只有0和-1这两个选择，同时将错误码的绝对值设置在errno内。 这里面的 backlog 是个非常重要的参数，如果设置不好，是个很隐蔽的坑。</font>

> <font style="color:rgb(36, 41, 46);">对于java开发者而言，基本用的现成的框架，而java本身默认的 backlog 设置大小只有50。这就会引起一些微妙的现象，</font><font style="color:#0033b3;background-color:#ffffff;">public </font><font style="color:#00627a;background-color:#ffffff;">ServerSocket</font><font style="color:#080808;background-color:#ffffff;">(</font><font style="color:#0033b3;background-color:#ffffff;">int </font><font style="color:#080808;background-color:#ffffff;">port) </font><font style="color:#0033b3;background-color:#ffffff;">throws </font><font style="color:#000000;background-color:#ffffff;">IOException </font><font style="color:#080808;background-color:#ffffff;">{</font><font style="color:#0033b3;background-color:#ffffff;">this</font><font style="color:#080808;background-color:#ffffff;">(port, </font><font style="color:#1750eb;background-color:#ffffff;">50</font><font style="color:#080808;background-color:#ffffff;">, </font><font style="color:#0033b3;background-color:#ffffff;">null</font><font style="color:#080808;background-color:#ffffff;">);}</font>
>

<font style="color:#080808;background-color:#ffffff;">在 </font>`<font style="color:#080808;background-color:#ffffff;"> sysdeps/unix/sysv/linux/listen.c</font>`<font style="color:#080808;background-color:#ffffff;">（GNU源代码中）</font>

```c
int
listen (int fd, int backlog)
{
#ifdef __ASSUME_LISTEN_SYSCALL
  return INLINE_SYSCALL (listen, 2, fd, backlog);
#else
  return SOCKETCALL (listen, fd, backlog);
#endif
}
weak_alias (listen, __listen);
```

+ **系统调用号**：`INLINE_SYSCALL` 会生成一个系统调用号，并将其与参数一起传递给内核
+ **内核入口**：内核接收到系统调用请求后，检查系统调用号，找到对应的处理函数。对于 `listen`，这将是 `SYSCALL_DEFINE2(listen, int, fd, int, backlog)` 的实现

在 `net/socket.c`中

```c
SYSCALL_DEFINE2(listen, int, fd, int, backlog)
{
	return __sys_listen(fd, backlog);
}
```

在 `net/socket.c`中

```c
int __sys_listen(int fd, int backlog)
{
	struct socket *sock;
	int err, fput_needed;

	sock = sockfd_lookup_light(fd, &err, &fput_needed);
	if (sock) {
		err = __sys_listen_socket(sock, backlog);
		fput_light(sock->file, fput_needed);
	}
	return err;
}
```

在 `net/socket.c`中

```c
int __sys_listen_socket(struct socket *sock, int backlog)
{
	int somaxconn, err;

	somaxconn = READ_ONCE(sock_net(sock->sk)->core.sysctl_somaxconn);
	if ((unsigned int)backlog > somaxconn)
		backlog = somaxconn;

	err = security_socket_listen(sock, backlog);
	if (!err)
		err = READ_ONCE(sock->ops)->listen(sock, backlog); //sock->ops->listen对应的方法为inet_listen
	return err;
}
```

以上调用关系如下

```c
listen
	|->INLINE_SYSCALL(listen......)
		|->SYSCALL_DEFINE2(listen, int, fd, int, backlog)
			/* 检测对应的描述符fd是否存在，不存在，返回-BADF
			|->sockfd_lookup_light
			/* 限定传过来的backlog最大值不超出 /proc/sys/net/core/somaxconn
			|->if ((unsigned int)backlog > somaxconn) backlog = somaxconn
			|->sock->ops->listen(sock, backlog) <=> inet_listen
            
            
```

<font style="color:rgb(36, 41, 46);">值得注意的是，Kernel对于我们传进来的backlog值做了一次调整，让其无法>内核参数设置中的somaxconn</font>

inet_listen

核心调动程序 **<font style="color:rgb(36, 41, 46);">inet_listen </font>**<font style="color:rgb(36, 41, 46);">在 </font>`<font style="color:rgb(36, 41, 46);">net/ipv4/af_inet.c</font>`

```c
int inet_listen(struct socket *sock, int backlog)
{
	struct sock *sk = sock->sk;
	int err = -EINVAL;

	lock_sock(sk);

	if (sock->state != SS_UNCONNECTED || sock->type != SOCK_STREAM)
		goto out;

	err = __inet_listen_sk(sk, backlog);

out:
	release_sock(sk);
	return err;
}
```

这里github上给的源码与文章中的[https://heapdump.cn/article/2300883](https://heapdump.cn/article/2300883)不一样。。。。。，看不懂了，等日后分析

#### 半连接&全连接队列
+ **<font style="color:rgb(79, 79, 79);">全连接队列（Completed Connection Queue）</font>**

<font style="color:rgb(77, 77, 77);">全连接队列也被称为已完成连接队列，用于存储已经建立好三次握手的连接。当服务器通过</font>`<font style="color:rgb(199, 37, 78);background-color:rgb(249, 242, 244);">accept()</font>`<font style="color:rgb(77, 77, 77);">函数接受了客户端的连接请求后，该连接会在全连接队列中等待被服务器进程处理。</font>

+ **<font style="color:rgb(79, 79, 79);">半连接队列（Half-Open Connection Queue）</font>**

<font style="color:rgb(77, 77, 77);">半连接队列也称为未完成连接队列，用于存储那些已经收到客户端连接请求并发送了 SYN+ACK 响应，但服务器还没有执行完全的三次握手建立连接的请求。这些连接处于半开放状态，等待服务器进程继续完成连接建立。</font>

<font style="color:rgb(77, 77, 77);">半连接队列为了防止 syn + ack 攻击？</font>

+ `**<font style="color:rgb(199, 37, 78);background-color:rgb(249, 242, 244);">listen()</font>**`**<font style="color:rgb(79, 79, 79);">函数中的</font>**`**<font style="color:rgb(199, 37, 78);background-color:rgb(249, 242, 244);">backlog</font>**`**<font style="color:rgb(79, 79, 79);">参数</font>**

`<font style="color:rgb(199, 37, 78);background-color:rgb(249, 242, 244);">listen()</font>`<font style="color:rgb(77, 77, 77);">函数中的</font>`<font style="color:rgb(199, 37, 78);background-color:rgb(249, 242, 244);">backlog</font>`<font style="color:rgb(77, 77, 77);">参数指定了服务器正在处理的连接队列的最大长度，即全连接队列的长度。这个参数影响着服务器能够同时处理的等待连接的数量。</font>

> <font style="color:rgb(77, 77, 77);">backlog 的值含义从来就没有被严格定义过。原先 Linux 实现中，backlog 参数定义了该套接字对应的未完成连接队列的最大长度 （pending connections)。如果一个连接到达时，该队列已满，客户端将会接收一个 ECONNREFUSED 的错误信息，如果支持重传，该请求可能会被忽略，之后会进行一次重传。</font>
>
> <font style="color:rgb(77, 77, 77);">从 Linux 2.2 开始，backlog 的参数内核有了新的语义，它现在定义的是已完成连接队列的最大长度，表示的是已建立的连接（established connection），正在等待被接收（accept 调用返回），而不是原先的未完成队列的最大长度。现在，未完成队列的最大长度值可以通过 /proc/sys/net/ipv4/tcp_max_syn_backlog 完成修改，默认值为 128。至于已完成连接队列，如果声明的 backlog 参数比 /proc/sys/net/core/somaxconn 的参数要大，那么就会使用我们声明的那个值。实际上，这个默认的值为 128。注意在 Linux 2.4.25 之前，这个值是不可以修改的一个固定值，大小也是 128。</font>
>
> <font style="color:rgb(77, 77, 77);">设计良好的程序，在 128 固定值的情况下也是可以支持成千上万的并发连接的，这取决于 I/O 分发的效率，以及多线程程序的设计。在后面的性能篇里，我们的目标就是设计这样的程序。</font>
>

#### 总结图
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723051413776-e2b710c5-007f-4133-ab72-318334702b9e.png)



### sokcet 缓冲区
socket 缓冲区用于临时存储从网络接收或即将发送的数据。具体来说，socket 缓冲区分为接收缓冲区和发送缓冲区：

+ **接收缓冲区**：用于存储从网络接收到的数据，直到应用程序读取它们。数据到达网络接口时，首先会被存放在接收缓冲区中。应用程序调用 recv 或类似的读取函数时，会从这个缓冲区中提取数据。
+ **发送缓冲区**：用于存储应用程序要发送到网络的数据。应用程序调用 send 或类似的写入函数时，数据会被复制到发送缓冲区，然后操作系统负责将这些数据发送到网络。

这两个缓冲区的大小是可配置的，默认值取决于操作系统的设置。可以使用 `getsockopt` 和 `setsockopt` 函数来获取和设置缓冲区大小。

```c
int getsockopt(int socket, int level, int optname, void *optval, socklen_t *optlen);
```

**参数说明**

1. `**socket**`：
    - socket 的文件描述符，表示你想要获取选项的 socket。
2. `**level**`：
    - 指定要获取的选项的级别。通常，对于 socket 选项，这个参数使用 `SOL_SOCKET`，表示获取 socket 本身的选项。
3. `**optname**`：
    - 具体的选项名称，指定你要获取的选项。例如：
        * `SO_RCVBUF`：接收缓冲区的大小。
        * `SO_SNDBUF`：发送缓冲区的大小。
        * 其他选项如 `SO_REUSEADDR`、`SO_KEEPALIVE` 等。
4. `**optval**`：
    - 一个指向存储选项值的缓冲区的指针。在调用 `getsockopt` 后，这个缓冲区将会被填充为当前选项的值。
5. `**optlen**`：
    - `optlen` 是一个指向 `socklen_t` 类型的变量的指针，用于指定和接收 socket 选项值的缓冲区大小。
    - **调用 **`**setsockopt**`：在调用 `setsockopt` 时，你需要提供 `optlen`，表示你为 `optval` 分配的空间大小。这个函数会将你提供的值应用到指定的 socket 选项中。
    - **调用 **`**getsockopt**`：在调用 `getsockopt` 时，你也需要提供 `optlen`，在这种情况下，它表示你希望接收的选项值的缓冲区大小。调用后，它会更新为实际填充到 `optval` 中的选项值的大小。
6. <font style="color:rgb(51, 51, 51);">函数成功时返回0，‌失败时返回-1并设置相应的错误码。‌</font>

<font style="color:rgb(51, 51, 51);">下面采用代码，说明 socket 缓冲区</font>

```c
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd < 0) {
    perror("创建socket失败");
}
int optval;
socklen_t optlen = sizeof(optval); // 用于指定和返回选项值的长度
if (getsockopt(socketfd, SOL_SOCKET, SO_RCVBUF, &optval, &optlen) == -1) {
    perror("获取失败");
}
printf("%d\n", optval);
fflush(stdout);
return 0;
```

运行结果

```c
131072
```

### 设置缓冲区大小
1. optval       > sysctl_rmem_max，则设置为最大值的2倍：2 * sysctl_wmem_max；
2. optval * 2 < SOCK_MIN_SNDBUF，则设置成最小值：SOCK_MIN_SNDBUF
3. optval       < sysctl_rmem_max，且 val*2 > SOCK_MIN_SNDBUF，则设置成 2 * val。

```c
存放接收缓冲区最大值的位置：cat /proc/sys/net/core/rmem_max
    212992
存放发送缓冲区最大值的位置：cat /proc/sys/net/core/wmem_max
    212992
```

```c
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd < 0) {
    perror("创建socket失败");
}

int optval = 3000; // 3000 字节
socklen_t optlen = sizeof(optval);
if (setsockopt(socketfd, SOL_SOCKET, SO_RCVBUF, &optval, optlen) == -1) {
    perror("设置失败");
}
if (getsockopt(socketfd, SOL_SOCKET, SO_RCVBUF, &optval, &optlen) == -1) {
    perror("获取失败");
}
printf("%d\n", optval);
fflush(stdout);
return 0;
```

运行结果

```c
6000
```

### accept()方法
`<font style="color:rgb(44, 44, 54);">accept()</font>`<font style="color:rgb(44, 44, 54);"> 的主要作用是从监听队列中取出一个连接请求并创建一个新的 socket 来处理这个连接。</font>

<font style="color:rgb(44, 44, 54);">监听队列（也称为连接队列）是操作系统内核维护的一个数据结构，它用于存储等待被接受的新连接请求。当一个 socket 被设置为监听模式（通过调用 </font>`<font style="color:rgb(44, 44, 54);">listen()</font>`<font style="color:rgb(44, 44, 54);"> 函数）后，任何客户端尝试与该 socket 建立连接时，它们的连接请求都会被放置到这个监听队列中，直到服务器端通过 </font>`<font style="color:rgb(44, 44, 54);">accept()</font>`<font style="color:rgb(44, 44, 54);"> 函数处理这些请求。</font>

**<font style="color:rgb(44, 44, 54);">监听队列的工作原理：</font>**

1. **<font style="color:rgb(44, 44, 54);">监听模式</font>**<font style="color:rgb(44, 44, 54);">：首先，服务器程序需要创建一个套接字，并将其设置为监听模式，通常是通过调用 </font>`<font style="color:rgb(44, 44, 54);">listen()</font>`<font style="color:rgb(44, 44, 54);"> 函数来完成。这使得套接字准备好接收连接请求。</font>
2. **<font style="color:rgb(44, 44, 54);">客户端发起连接</font>**<font style="color:rgb(44, 44, 54);">：客户端尝试与服务器通信时，它会通过 </font>`<font style="color:rgb(44, 44, 54);">connect()</font>`<font style="color:rgb(44, 44, 54);"> 函数尝试建立连接。此时，如果服务器端的套接字处于监听模式，连接请求会被捕获，并加入到监听队列中。</font>
3. **<font style="color:rgb(44, 44, 54);">接受连接</font>**<font style="color:rgb(44, 44, 54);">：服务器端的应用程序会调用 </font>`<font style="color:rgb(44, 44, 54);">accept()</font>`<font style="color:rgb(44, 44, 54);"> 函数来处理监听队列中的连接请求。每当 </font>`<font style="color:rgb(44, 44, 54);">accept()</font>`<font style="color:rgb(44, 44, 54);"> 被调用时，如果监听队列中有可用的连接请求，那么这个请求会被移除，并为这个连接创建一个新的套接字描述符。这个新套接字将用于后续的数据传输。</font>
4. **<font style="color:rgb(44, 44, 54);">数据传输</font>**<font style="color:rgb(44, 44, 54);">：一旦连接被接受，服务器和客户端就可以通过新创建的套接字进行数据交换。</font>

**<font style="color:rgb(44, 44, 54);">谁来监听：</font>**

+ **<font style="color:rgb(44, 44, 54);">操作系统内核</font>**<font style="color:rgb(44, 44, 54);">：监听队列实际上是操作系统内核的一部分，内核负责维护这个队列，并确保连接请求被正确地处理。</font>
+ **<font style="color:rgb(44, 44, 54);">服务器应用程序</font>**<font style="color:rgb(44, 44, 54);">：服务器应用程序通过调用 </font>`<font style="color:rgb(44, 44, 54);">listen()</font>`<font style="color:rgb(44, 44, 54);"> 和 </font>`<font style="color:rgb(44, 44, 54);">accept()</font>`<font style="color:rgb(44, 44, 54);"> 来配置和监听连接请求。服务器程序通常在一个无限循环中调用 </font>`<font style="color:rgb(44, 44, 54);">accept()</font>`<font style="color:rgb(44, 44, 54);">，以便能够持续处理新的连接请求。</font>

### <font style="color:rgb(44, 44, 54);">read()方法</font>
```c
ssize_t read(int fd, void *buf, size_t count);
```

**参数说明：**

+ `**int fd**`: 文件描述符，表示要读取的文件或设备的标识符。通常通过调用 `open()` 函数获取。
+ `**void *buf**`: 指向一个内存缓冲区的指针，读取的数据将存储在这个缓冲区中。该缓冲区必须足够大，以容纳要读取的数据。
+ `**size_t count**`: 要读取的字节数，即希望从文件中读取多少字节的数据。通常，这个值应该小于或等于缓冲区的大小。

**返回值：**

+ 成功时，`read()` 返回实际读取的字节数（类型为 `ssize_t`），可能少于请求的字节数。如果到达文件末尾，返回值可能为 0。
+ 失败时，返回 -1，并且 `errno` 会被设置为指示错误类型。

而是用于读取从 `client_fd`（一个已经建立的客户端连接的 socket ）中接收到的数据。

**原理说明：**

1. **读取数据**：
    - `read` 函数会尝试从 `client_fd` 的读取缓冲区中获取数据，并将数据存储在 `buf` 中。
    - 如果缓冲区中有数据可供读取，`read` 将返回读取的字节数。如果没有数据可读，它会阻塞（等待数据到达）直到有数据可用，除非 `client_fd` 连接关闭。
2. **阻塞行为**：
    - 在没有数据的情况下，`read` 将会阻塞，直到有数据可供读取或发生错误。因此，可以说 `read` 是在“等待”或“阻塞”直到数据可用，但这与监听（如 `listen` 或 `select`）的概念不同。
    - 如果你想要在不阻塞的情况下检查 `client_fd` 是否有数据可以读取，你可以使用 `select`、`poll` 或 `epoll` 等机制。
3. **读取后的数据处理**：
    - 一旦 `read` 成功返回，数据就被复制到了 `buf` 中，可以对其进行处理。

### write()方法
`write` 方法用于向套接字发送数据。在 C 语言的 socket 编程中，它用于将数据写入一个连接的套接字，通常是客户端或服务器端的套接字。`write` 函数的基本用法和语法如下：

```c
ssize_t write(int fd, const void *buf, size_t count);
```

**参数解释**

+ `**fd**`：要写入的文件描述符（套接字文件描述符），通常是通过 `socket` 和 `accept` 创建的套接字。
+ `**buf**`：指向要发送数据的缓冲区的指针。
+ `**count**`：要写入的字节数。

**返回值**

+ 如果成功，`write` 返回实际写入的字节数。
+ 如果发生错误，返回 -1，并设置 `errno` 以指示错误类型。

### 参考链接
+ [https://www.cnblogs.com/xiaokang-coding/p/18024684](https://www.cnblogs.com/xiaokang-coding/p/18024684)
+ [https://heapdump.cn/article/2300883](https://heapdump.cn/article/2300883)
+ [https://blog.csdn.net/weixin_50448879/article/details/135049118](https://blog.csdn.net/weixin_50448879/article/details/135049118)
+ [https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect](https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect)

## 同步阻塞网络
首先我们来看下面这段代码，创建了一个 socket 客户端

```c
int main()
{
    int sk = socket(AF_INET, SOCK_STREAM, 0);
    connect(sk, ...);
    recv(sk, ...);
}
```

下面是这段代码的大概原理图

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723283021952-c9bfb852-9830-4086-a29d-f550a7b4ee29.webp)

### 创建socket时内核的工作
在我们创建 socket 时，内核已经在内部创建了一系列的 socket 相关内核对象，下图是简化版

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723283103172-7f2a5cd4-00d3-4653-82d9-339a66969462.webp)

在 `net/socket.c`中

```c
SYSCALL_DEFINE3(socket, int, family, int, type, int, protocol)
{
	return __sys_socket(family, type, protocol);
}
```

```c
int __sys_socket(int family, int type, int protocol)
{
	struct socket *sock;
	int flags;

	sock = __sys_socket_create(family, type,
				   update_socket_protocol(family, type, protocol));
	if (IS_ERR(sock))
		return PTR_ERR(sock);

	flags = type & ~SOCK_TYPE_MASK;
	if (SOCK_NONBLOCK != O_NONBLOCK && (flags & SOCK_NONBLOCK))
		flags = (flags & ~SOCK_NONBLOCK) | O_NONBLOCK;

	return sock_map_fd(sock, flags & (O_CLOEXEC | O_NONBLOCK));
}
```

```c
static struct socket *__sys_socket_create(int family, int type, int protocol)
{
	struct socket *sock;
	int retval;

	/* Check the SOCK_* constants for consistency.  */
	BUILD_BUG_ON(SOCK_CLOEXEC != O_CLOEXEC);
	BUILD_BUG_ON((SOCK_MAX | SOCK_TYPE_MASK) != SOCK_TYPE_MASK);
	BUILD_BUG_ON(SOCK_CLOEXEC & SOCK_TYPE_MASK);
	BUILD_BUG_ON(SOCK_NONBLOCK & SOCK_TYPE_MASK);

	if ((type & ~SOCK_TYPE_MASK) & ~(SOCK_CLOEXEC | SOCK_NONBLOCK))
		return ERR_PTR(-EINVAL);
	type &= SOCK_TYPE_MASK;

	retval = sock_create(family, type, protocol, &sock);
	if (retval < 0)
		return ERR_PTR(retval);

	return sock;
}

```

```c
int sock_create(int family, int type, int protocol, struct socket **res)
{
	return __sock_create(current->nsproxy->net_ns, family, type, protocol, res, 0);
}
EXPORT_SYMBOL(sock_create);
```

```c
int __sock_create(struct net *net, int family, int type, int protocol,
			 struct socket **res, int kern)
{
	int err;
	struct socket *sock;
	const struct net_proto_family *pf;

    // ....

    // 分配sock对象
	sock = sock_alloc();    

    // 获得每个协议族的操作表
	pf = rcu_dereference(net_families[family]);

    //调用每个协议族创建函数，对于AF_NETT对应的是 inet_create 方法
     err = pf->create(net, sock, protocol, kern);

    
}
```

在`net/ipv4/af_inet.c`中

```c
static int inet_create(struct net *net, struct socket *sock, int protocol,
		       int kern)
{
	struct sock *sk;

	if (protocol < 0 || protocol >= IPPROTO_MAX)
		return -EINVAL;

	sock->state = SS_UNCONNECTED;

	/* 查找对应的协议，对于TCP SOCK_STREAM 就是获取到了 */
lookup_protocol:
    // ....
	list_for_each_entry_rcu(answer, &inetsw[sock->type], list)
    // ....

    // 将 inet_stream_ops 赋值给 socket -> ops 
	sock->ops = answer->ops;
    // 获得 tcp_prot
	answer_prot = answer->prot;

    // ....
    // 分配 sock 对象，并把 tcp_prot 赋值给 sock -> sk_prot上
	sk = sk_alloc(net, PF_INET, GFP_KERNEL, answer_prot, kern);
    // ....
}
```

<font style="color:rgb(66, 75, 93);">在 inet_create 中，根据类型 SOCK_STREAM 查找到对于 tcp 定义的操作方法实现集合 inet_stream_ops 和 tcp_prot。并把它们分别设置到 socket->ops 和 sock->sk_prot 上。</font>  
 ![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723285504519-e35617bf-5aa3-4f46-9fa8-315cd9bb991f.webp)

我们再往下看到了 sock_init_data。在这个方法中将 sock 中的 sk_data_ready 函数指针进行了初始化，设置为默认 sock_def_readable()。

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723285565088-1a8f6833-71c0-4bdd-b8fd-963ca625570b.webp)

在 `net/core/sock.c`中

```c
// 没找到，应该是更新了
void sock_init_data(struct socket *sock, struct sock *sk) 
{
    sk->sk_data_ready   =   sock_def_readable;
    sk->sk_write_space  =   sock_def_write_space;
    sk->sk_error_report =   sock_def_error_report;
}
```

当软中断上收到数据包时会通过调用 sk_data_ready 函数指针（实际被设置成了 sock_def_readable()） 来唤醒在 sock 上等待的进程。这个咱们后面介绍软中断的时候再说，这里记住这个就行了。

至此，一个 tcp对象，确切地说是 AF_INET 协议族下 SOCK_STREAM对象就算是创建完成了。这里花费了一次 socket 系统调用的开销

### 继续整理......
[https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect](https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect)

[https://cloud.tencent.com/developer/article/2188691](https://cloud.tencent.com/developer/article/2188691)![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723287053331-426c0bf4-8c1c-4c49-b9b4-1aca18a7a836.jpeg)

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723285953234-12faeee0-46bf-48ed-9196-096cd543ee48.webp)

## IO 多路复用/转接
IO 多路复用就是使用 IO 多路复用转接函数委托内核检测服务端文件描述符

### select()
#### 简单使用
```c
#include<sys/select.h>
struct timeval {
    time_t 		tv_sec;
    suseconds_t tv_usec;
}
int select (int __nfds, 
            fd_set *__restrict __readfds,
    		fd_set *__restrict __writefds,
    		fd_set *__restrict __exceptfds,
    		struct timeval *__restrict __timeout);
```

+ `nfds`: 要监视的文件描述符集中的最大文件描述符加1
    - 内核需要线性遍历这些集合中的文件描述符，这个值是循环结束的条件
    - 在 windows 中这个参数是无效的，指定为 -1 即可
+ `readfds`: 用于检查可读性的文件描述符集
+ `writefds`: 用于检查可写性的文件描述符集。
+ `exceptfds`: 用于检查异常条件的文件描述符集。
+ `timeout`: 等待的超时时间。

```c
int main(){
    int socketfd = socket(AF_INET, SOCK_STREAM, 0);
    if (socketfd == -1) {
        perror("创建socket失败");
        exit(1);
    }

    struct sockaddr_in server_addr = {0};
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(8080);
    server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0
    if (bind(socketfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
        perror("绑定端口失败");
        exit(1);
    }

    if (listen(socketfd, 255) == -1) {
        perror("监听失败");
        exit(1);
    }

    fd_set fds;

    // 将fds数组中所有位置清0
    FD_ZERO(&fds);

    // 将要添加监听的文件描述符添加到集合中
    FD_SET(socketfd, &fds);


    int maxfd = socketfd;

    while (1) {
        fd_set tmp = fds;

        // 调用 select 监听这些文件描述符
        // 传入一个副本，防止要监听的fds集合被修改
        int ret = select(maxfd + 1, &tmp, NULL, NULL, NULL);

        // 遍历文件描述符集合
        for (int i = 0; i <= maxfd; ++i) {
            // FD_ISSET 检查指定的文件描述符是否处于已设置状态（准备好IO操作）
            if (FD_ISSET(i, &tmp)) {
                if (socketfd == i) {
                    // socketfd准备好 iO
                    int cfd = accept(socketfd, NULL, NULL);
                    FD_SET(cfd, &fds);
                    maxfd = cfd > maxfd ? cfd : maxfd;
                    printf("v_1.0有客户端建立连接,fd=%d\n", cfd);
                } else {
                    // 客户端fd 准备好IO
                    char buf[1024];
                    int len = recv(i, buf, sizeof(buf) - 1, 0); // 减去1是为了留出一个位置给\0
                    if (len == -1) {
                        perror("接受客户端消息出错\n");
                    } else if (len == 0) {
                        printf("fd=%d，客户端已经断开连接\n", i);
                        FD_CLR(i, &fds);
                        close(i);
                        break;
                    }
                    buf[len] = '\0'; // 将 \0 之前的字符全部打印
                    printf("fd:%d，客户端消息:%s", i, buf);
                    char msg[] = "接收到数据\n";
                    if (send(i, msg, strlen(msg) + 1, 0) == -1) {
                        perror("发送数据失败\n");
                    }
                }
            }
        }
    }
}
```

#### `select` 的底层实现原理
![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723373293621-2b63bf3f-e92a-4f8b-a392-1d468ccb7f3c.jpeg)

1. **遍历文件描述符集**： 当 `select` 被调用时，内核会遍历文件描述符集合中的文件描述符，并将它们添加到等待队列中。内核为每个文件描述符设置一个等待事件，比如等待文件变得可读、可写或出现异常。
2. **进入休眠**： 如果没有文件描述符立即满足条件，`select` 使调用进程进入休眠状态，直到文件描述符集中的某一个文件描述符的状态发生变化，或者超时时间到达。
3. **唤醒与返回**： 当一个或多个文件描述符的状态发生变化时，内核唤醒休眠中的进程，并更新相应的文件描述符集合，以表明哪些文件描述符已经准备好执行 I/O 操作。`select` 返回准备好的文件描述符数量。



**源码讲解：**[https://blog.csdn.net/qq_37058442/article/details/78004507](https://blog.csdn.net/qq_37058442/article/details/78004507)



#### fd_set 结构体
```c
typedef long int __fd_mask;
#define __FD_SETSIZE		1024
#define __NFDBITS	(8 * (int) sizeof (__fd_mask))

typedef struct
  {
    __fd_mask __fds_bits[__FD_SETSIZE / __NFDBITS];
  } fd_set;

// 结合上面的定义我们可以看出，在ubuntu中sizeof(long int) = 8
// __NFDBITS == (8 * (int) sizeof (__fd_mask)) == (8 * (int) sizeof (long int)) == 64
// [__FD_SETSIZE / __NFDBITS] = 1024 / 64 = 16
{
    long int _fds_bits[16]
}
```

下图 fd_set 中存储了要委托内核检测的文件描述符集合

+ 如果集合标志位为0，代表不检测这个文件描述符
+ 如果集合标志位为1，代表检测这个文件描述符状态

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723291438871-05263edc-2ca9-4c3b-84fd-e615d13001e8.png)

内核在遍历这个读集合的过程中，如果被检测的文件描述符对应的读缓冲区中沿有数据，内核将修改这个文件描述符在读集合fd_set中对应的标志位，改为0，如果有数据那么这个标志位的值不变还是1。

#### FD_SET宏定义
```c
#define	FD_SET(fd, fdsetp)	__FD_SET (fd, fdsetp)

#define __FD_SET(d, set) \
  ((void) (__FDS_BITS (set)[__FD_ELT (d)] |= __FD_MASK (d)))

# define __FDS_BITS(set) ((set)->__fds_bits)

// __FDS_BITS (set) 其实就是 fd_set 结构体中的 fds_bits 数组
// __FD_ELT 用来计算索引的值
// __FD_MASK用来计算索引对应位置的值
```

**__FD_ELT**

```c
__FD_ELT 
    ==> (d) / ( 8 * (int) sizeof (long int)) //(8 * (int) sizeof (long int)) 在 ubuntu 中为 64
    ==> (d) / 64
```

**__FD_MASK**

```c
__FD_MASK 
    ==> ((long int) (1UL << (d) % ( 8 * (int) sizeof (long int)) ) )
    ==> ((long int) (1UL << (d) % 64 ) )
```

我们可以看出FD_SET宏的具体实现原理

```c
// d 代表文件描述符
fds_bits [d / 64] |= ((long int) (1UL << (d) % 64 ) )
```

```c
FD_SET(0, &fds);  // fds_bits[0] = 1    对应二进制 1
FD_SET(1, &fds);  // fds_bits[0] = 3    对应二进制 11
FD_SET(2, &fds);  // fds_bits[0] = 7    对应二进制 111
FD_SET(4, &fds);  // fds_bits[0] = 23   对应二进制 10111
...
//每监听一个文件描述符就将对应的 bit_position 设置为1
```

#### FD_ISSET宏定义
```c
#define	FD_ISSET(fd, fdsetp)	__FD_ISSET (fd, fdsetp)

#define __FD_ISSET(d, set) \
  ((__FDS_BITS (set)[__FD_ELT (d)] & __FD_MASK (d)) != 0)

//由前面可知
// __FDS_BITS (set) ==> fds_bits
// __FD_ELT ==> d / 64
// __FD_MASK ==> ((long int) (1UL << (d) % 64 ) )

fds_bits[d / 64]  & ((long int) (1UL << (d) % 64 ) ) != 0
```

### poll()
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723401623630-fc052e04-1aea-4f1a-ab55-e149ce32d9c7.png)![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723401640250-89785d70-fd04-44c1-aa06-41bc0f5f0ed3.png)

```c
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd == -1) {
    perror("创建socket失败");
    exit(1);
}

struct sockaddr_in server_addr = {0};
server_addr.sin_family = AF_INET;
server_addr.sin_port = htons(8080);
server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0
if (bind(socketfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
    perror("绑定端口失败");
    exit(1);
}

if (listen(socketfd, 255) == -1) {
    perror("监听失败");
    exit(1);
}

struct pollfd fds[1024];

for (int i = 0; i < 1024; ++i) {
    fds[i].fd = -1; // 将 fds 中 fd 的初始值设置为 -1
    fds[i].events = POLLIN; // 监听这些文件描述符的POLLIN事件
}
fds[0].fd = socketfd; // 监听 socketfd

int maxfd = 0;

while (1) {
    // 内核监听fds数组中的文件描述符
    int ret = poll(fds, maxfd + 1, -1);

    // 检查是不是 socketfd 的事件
    if (fds[0].revents & POLLIN) {
        int clientfd = accept(socketfd, NULL, NULL);
        printf("v_1.1有客户端建立连接,fd=%d\n", clientfd);
        // 将 clientfd 添加到 fds 中
        for (int i = 0; i < 1024; ++i) {
            if (fds[i].fd == -1) {
                fds[i].fd = clientfd;
                fds[i].events = POLLIN; // 监听客户端的读事件
                break;
            }
        }
        maxfd = maxfd > clientfd ? maxfd : clientfd;
    }

    // 检查是不是 clientfd 的事件
    for (int i = 1; i <= maxfd; ++i) {
        if (fds[i].revents & POLLIN) {
            // 客户端fd，发现读事件
            char buf[1024];
            int len = recv(fds[i].fd, buf, sizeof(buf) - 1, 0); // 减去1是为了留出一个位置给\0
            if (len == -1) {
                perror("接受客户端消息出错\n");
            } else if (len == 0) {
                printf("fd=%d，客户端已经断开连接\n", i);
                fds[i].fd = -1;
                close(fds[i].fd);
                break;
            } else {
                buf[len] = '\0'; // 将 \0 之前的字符全部打印
                printf("fd:%d，客户端消息:%s", fds[i].fd, buf);
                char msg[] = "接收到数据\n";
                if (send(fds[i].fd, msg, strlen(msg) + 1, 0) == -1) {
                    perror("发送数据失败\n");
                }
            }
        }
    }
}
```

### epoll()
#### 简单使用
epoll 全称 event poll

```c
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd == -1) {
    perror("创建socket失败");
    exit(1);
}
struct sockaddr_in server_addr = {0};
server_addr.sin_family = AF_INET;
server_addr.sin_port = htons(8080);
server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0
if (bind(socketfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
    perror("绑定端口失败");
    exit(1);
}
if (listen(socketfd, 255) == -1) {
    perror("监听失败");
    exit(1);
}

// 创建一个 epoll 实例，返回对应的文件描述符
int epfd = epoll_create(1);
if (epfd == -1) {
    perror("创建红黑树失败");
    exit(1);
}

struct epoll_event ev;
ev.events = EPOLLIN; //监听读事件
ev.data.fd = socketfd;
epoll_ctl(epfd, EPOLL_CTL_ADD, socketfd, &ev);

struct epoll_event evs[1024];
int size = sizeof(evs) / sizeof(evs[0]);

while (1) {
    int ret = epoll_wait(epfd, evs, size, -1);
    for (int i = 0; i < ret; ++i) {
        int fd = evs[i].data.fd;
        if (fd == socketfd) {
            int cfd = accept(socketfd, NULL, NULL);
            ev.events = EPOLLIN; //监听读事件
            ev.data.fd = cfd;
            // 红黑树添加节点
            epoll_ctl(epfd, EPOLL_CTL_ADD, cfd, &ev);
            printf("v_1.epoll有客户端建立连接,fd=%d\n", cfd);
        } else {
            char buf[1024];
            int len = recv(fd, buf, sizeof(buf) - 1, 0);
            if (len == -1) {
                perror("接受客户端消息出错\n");
            } else if (len == 0) {
                printf("fd=%d，客户端已经断开连接\n", fd);
                // 删除红黑树上的节点
                epoll_ctl(epfd, EPOLL_CTL_DEL, fd, NULL);
                close(fd);
                break;
            }
            buf[len] = '\0'; // 将 \0 之前的字符全部打印
            printf("fd:%d，客户端消息:%s", fd, buf);
            char msg[] = "接收到数据\n";
            if (send(fd, msg, strlen(msg) + 1, 0) == -1) {
                perror("发送数据失败\n");
            }
        }
    }
}
```

#### epoll_create() 
epoll_create 创建一个 eventpoll 内核对象

`fs/eventpoll.c`

```c
struct eventpoll {

    // ....

	/* Wait queue used by sys_epoll_wait() */
    // 等待队列链表，存放阻塞的进程
	wait_queue_head_t wq;

    
	/* List of ready file descriptors */
    // 存放数据就绪的文件描述符
	struct list_head rdllist;


	/* RB tree root used to store monitored fd structs */
    // 红黑树，存放进程下所有添加进来的文件描述符以及相关的事件
	struct rb_root_cached rbr;

    // ....
};


```

![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723540144178-7649b534-ac1d-47b5-bfb5-4309dacc69a8.jpeg)

#### epoll_ctl() & epoll_event
**epoll_event：**是一个用于封装文件描述符和监听事件的结构体

```c
struct epoll_event
{
  uint32_t events;	/* Epoll events */
  epoll_data_t data;	/* User data variable */
} __EPOLL_PACKED;

typedef union epoll_data
{
  void *ptr;
  int fd;
  uint32_t u32;
  uint64_t u64;
} epoll_data_t;
```

**epoll_ctl：**可以向内核中的红黑树，添加、删除epoll_event

#### event_wait
1. [https://www.cnblogs.com/88223100/p/Deeply-learn-the-implementation-principle-of-IO-multiplexing-select_poll_epoll.html](https://www.cnblogs.com/88223100/p/Deeply-learn-the-implementation-principle-of-IO-multiplexing-select_poll_epoll.html)
2. **边缘触发与水平触发**:
    - `epoll_wait` 支持边缘触发（Edge Triggered, ET）和水平触发（Level Triggered, LT）两种模式：
        * **水平触发**：当某个文件描述符上有事件发生时，`epoll_wait` 会不断返回这个事件，直到事件被处理（**比如缓冲区的数据被处理干净了**）
        * **边缘触发**：当某个文件描述符上有事件发生时，`epoll_wait` 只会通知一次，除非有新的事件发生。

### 三者对比
| | select | poll | epoll |
| --- | --- | --- | --- |
| 数量限制 | FD_SETSIZE 通常为1024 | 无 | 无 |
| 监视方式 | 采用固定大小的位图来表示要监视的文件描述符，在处理大量稀疏的文件描述符低效 | 使用一个结构体来监听存储文件描述符和事件，处理大量稀疏的文件描述符高效 | |
| 文件描述符集合 | 会被修改 | 不会被修改 | |
| 内核检测文件描述符方式 | 线性扫描 | 线性扫描 | |
| 内核和用户空间交互 | 频繁 | 频繁 | 采用共享内存，减少内存拷贝 |
|  | 需要对文件描述符逐一判断 | 需要对文件描述符逐一判断 | 返回具体的文件描述符 |


![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723456058131-da1803b8-5791-4d57-98f8-e08d89e2dd77.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723456040923-b905efe2-e4d8-4cba-b711-8b9408687e57.png)



### 参考链接
+ [https://www.bilibili.com/video/BV15X4y1Y7T9](https://www.bilibili.com/video/BV15X4y1Y7T9)
+ [https://www.bilibili.com/video/BV1fg411376j](https://www.bilibili.com/video/BV1fg411376j)



# 工具
## lsof
`lsof`（List Open Files）是一个用于列出当前系统上所有打开文件的强大工具。在类 Unix 操作系统（如 Linux 和 macOS）中，文件不仅包括常规文件，还包括目录、网络socket、设备文件、管道等。因此，`lsof` 可以用于显示与文件相关的几乎所有活动。

`lsof` 的用途

+ 查看某个进程打开的文件。
+ 确定哪些文件正在被某个进程使用。
+ 查找打开某个文件的所有进程。
+ 监控网络连接。
+ 调试文件描述符泄漏等问题。

### `lsof` 的输出解释 
`lsof` 的输出包含多个字段，每个字段提供关于打开文件的信息。常见字段包括：

+ **COMMAND**：进程的命令名。
+ **PID**：进程 ID。
+ **USER**：进程所有者的用户名。
+ **FD**：文件描述符。
    - `cwd`：当前工作目录。
    - `txt`：程序的文本段（代码段）。
    - `mem`：内存映射的文件。
    - `0`、`1`、`2`：标准输入、输出和错误。
+ **TYPE**：文件类型（如 `DIR` 目录、`REG` 常规文件、`CHR` 字符设备等）。
+ **DEVICE**：设备号。
+ **SIZE/OFF**：文件大小或文件偏移量。
+ **NODE**：文件的索引节点号（inode number）。
+ **NAME**：文件名。

#### FD 字段
+ **FD**：文件描述符，表示进程打开文件的编号。
+ **类型修饰符**：
    - `r`：表示文件以只读模式打开（read）。
    - `w`：表示文件以只写模式打开（write）。
    - `u`：表示文件以读写模式打开（update）。
    - `a`：表示文件以追加模式打开（append）。
+ **状态修饰符**：
    - `t`：表示文件被共享（文本段）。
    - `x`：表示文件为程序文本段（代码段）。
    - `m`：表示文件为内存映射文件。
    - `L`：表示文件有专用锁。
    - `N`：表示文件有非阻塞模式。
    - `D`：表示文件为内存映射设备。
    - `T`：表示文件的文件锁（File lock）。

### 查看某个进程打开的文件
```c
lsof -p <PID>
```

### 查看某个文件被哪些进程打开
```c
lsof <filename>
```



## netstat命令
[https://blog.csdn.net/wkd_007/article/details/135929411](https://blog.csdn.net/wkd_007/article/details/135929411)

+ **显示所有网络连接**

<font style="color:rgb(77, 77, 77);">使用netstat -a命令可以显示所有网络连接，包括TCP和UDP连接。</font>

<font style="color:rgb(77, 77, 77);">这条命令将列出本地IP地址和端口、远程IP地址和端口，以及连接状态（如ESTABLISHED、LISTENING等）。通过这个命令，我们可以了解当前系统上所有的网络连接情况。</font>

+ **显示特定协议网络连接**

<font style="color:rgb(77, 77, 77);">有时候我们只关注特定协议的连接，如TCP或UDP。netstat指令提供了相应的参数来过滤特定协议的连接信息</font>

<font style="color:rgb(77, 77, 77);">查看TCP连接：netstat -t</font>

<font style="color:rgb(77, 77, 77);">查看UDP连接：netstat -u</font>

<font style="color:rgb(77, 77, 77);"></font>

<font style="color:rgb(77, 77, 77);">-r, --route	显示路由表</font>

<font style="color:rgb(77, 77, 77);">-i, --interfaces	显示网络接口表</font>

<font style="color:rgb(77, 77, 77);">-g, --groups	显示多播组成员身份</font>

<font style="color:rgb(77, 77, 77);">-s, --statistics	显示网络统计信息（如SNMP）</font>

<font style="color:rgb(77, 77, 77);">-M, --masquerade	显示伪装的连接，Linux不支持</font>

<font style="color:rgb(77, 77, 77);">-v, --verbose	详细地告诉用户发生了什么。特别是打印一些有关未配置地址族的有用信息</font>

<font style="color:rgb(77, 77, 77);">-W, --wide	不要根据需要使用输出来截断IP地址。这是可选的，目前不破坏现有脚本。</font>

<font style="color:rgb(77, 77, 77);">-n, --numeric	显示数字地址，而不是解析为名称</font>

<font style="color:rgb(77, 77, 77);">–numeric-hosts	显示数字主机地址，不解析主机名。</font>

<font style="color:rgb(77, 77, 77);">–numeric-ports	不解析端口名</font>

<font style="color:rgb(77, 77, 77);">–numeric-users	不解析用户名</font>

<font style="color:rgb(77, 77, 77);">-N, --symbolic	解析硬件名称</font>

<font style="color:rgb(77, 77, 77);">-e, --extend	显示其他/更多信息</font>

<font style="color:rgb(77, 77, 77);">-p, --programs	显示套接字的PID/程序名称</font>

<font style="color:rgb(77, 77, 77);">-o, --timers	显示计时器</font>

<font style="color:rgb(77, 77, 77);">-c, --continuous	这将导致netstat连续每秒打印一次所选信息。</font>

<font style="color:rgb(77, 77, 77);">-l, --listening	仅显示处于监听状态的套接字。（默认情况下会省略这些。）</font>

<font style="color:rgb(77, 77, 77);">-a, --all	显示所有套接字（默认只显示已连接的）</font>

<font style="color:rgb(77, 77, 77);">-F, --fib	显示转发信息库（默认）</font>

<font style="color:rgb(77, 77, 77);">-C, --cache	显示路由缓存而不是FIB</font>

<font style="color:rgb(77, 77, 77);">-Z, --context	显示套接字的SELinux安全上下文</font>

<font style="color:rgb(77, 77, 77);">-t, --tcp	仅显示TCP相关</font>

<font style="color:rgb(77, 77, 77);">-u, --ud	仅显示UDP相关</font>

<font style="color:rgb(77, 77, 77);">-w, --raw	仅显示RAW套接字相关</font>

<font style="color:rgb(77, 77, 77);">-x, --unix	仅显示Unix域套接字相关</font>

<font style="color:rgb(77, 77, 77);">-4	仅显示IPv4相关</font>

<font style="color:rgb(77, 77, 77);">-6	仅显示IPv6相关</font>



