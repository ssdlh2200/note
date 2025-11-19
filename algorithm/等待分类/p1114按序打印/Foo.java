package algorithm.p1114;

import java.util.concurrent.Semaphore;


public class Foo {
    public static void main(String[] args) {
        Foo foo = new Foo();
        Runnable task1 = () -> System.out.print("first");
        Runnable task2 = () -> System.out.print("second");
        Runnable task3 = () -> System.out.print("third");

        Thread t1 = new Thread(() -> {
            try {
                foo.first(task1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                foo.second(task2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t3 = new Thread(() -> {
            try {
                foo.third(task3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t3.start();
        t2.start();
        t1.start();
    }

    public Foo() {

    }

    Semaphore semaphoreA = new Semaphore(1);
    Semaphore semaphoreB = new Semaphore(0);
    Semaphore semaphoreC = new Semaphore(0);

    public void first(Runnable printFirst) throws InterruptedException {
        semaphoreA.acquire(1);
        printFirst.run();
        semaphoreB.release(1);
    }

    public void second(Runnable printSecond) throws InterruptedException {
        semaphoreB.acquire();
        printSecond.run();
        semaphoreC.release(1);
    }

    public void third(Runnable printThird) throws InterruptedException {
        semaphoreC.acquire(1);
        printThird.run();
        semaphoreA.release(1);
    }
}


