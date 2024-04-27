package com.java.node.simple.sync;

/**
 * @Description 线程同步测试
 */
public class SynchronizeTest {
    public static void main(String[] args) {
        final SynchronizeTest single = new SynchronizeTest();
        //两个非静态方法，单例模式，两个的对象锁一致，被阻塞无法同时进行
        new Thread(() -> single.fun1()).start();
        new Thread(() -> single.fun2()).start();
        //两个非静态方法，多例模式，两个的对象锁不一致，可以同时进行
        new Thread(() -> new SynchronizeTest().fun1()).start();
        new Thread(() -> new SynchronizeTest().fun2()).start();
        //两个静态方法，都是类锁，阻塞无法同时进行
        new Thread(() -> fun1s()).start();
        new Thread(() -> fun2s()).start();
        //一个静态方法、一个非静态方法，一个对象锁一个类锁，不互斥，可以同时进行
        new Thread(() -> single.fun1()).start();
        new Thread(() -> fun1s()).start();
    }

    private synchronized static void fun1s() {
        System.out.println("fun1s----->start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("fun1s----->end");
    }

    private synchronized static void fun2s() {
        System.out.println("fun2s----->start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("fun2s----->end");
    }

    private synchronized void fun1() {
        System.out.println("fun1----->start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("fun1----->end");
    }

    private synchronized void fun2() {
        System.out.println("fun2----->start");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("fun2----->end");
    }
}
