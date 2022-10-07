package com.test.distributeLock;

import com.test.dynamicServerNode.DistributeClient;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributeLockTest {
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributeLock lock1=new DistributeLock();
        final DistributeLock lock2=new DistributeLock();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    lock1.zkLock();
                    System.out.println("线程1获取锁");
                    Thread.sleep(5*1000);

                    lock1.zkUnlock();
                    System.out.println("线程1释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.zkLock();
                    System.out.println("线程2获取锁");
                    Thread.sleep(5*1000);

                    lock2.zkUnlock();
                    System.out.println("线程2释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
