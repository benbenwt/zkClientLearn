package com.test.distributeLock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributeLock {
    private static String connectString="127.0.0.1:2181";
    private static int sessionTimeout=2000;
    private ZooKeeper zkClient=null;
    private String rootNode="locks";
    private String subNode="seq-";
    private String waitPath;

    private CountDownLatch connectDownLatch=new CountDownLatch(1);

    private CountDownLatch waitLatch=new CountDownLatch(1);

    private String currentNode;

    public DistributeLock() throws IOException, InterruptedException, KeeperException {
        zkClient=new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent event) {
//需要锁时，抢占创建node。判断最小的获得锁。
//发生节点删除时，再次检查自己是否是最小的node，最小的获得锁。
                if(event.getState()== Event.KeeperState.SyncConnected)
                {
                    connectDownLatch.countDown();
                }
                if(event.getType()==Event.EventType.NodeDeleted && event.getPath().equals(waitPath))
                {
                    waitLatch.countDown();
                }
            }
        });

        connectDownLatch.await();

        Stat stat= zkClient.exists("/"+rootNode,false);

        if(stat==null)
        {
            System.out.println("根节点不存在");
            zkClient.create("/"+rootNode,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
    }

    public void zkLock() throws KeeperException, InterruptedException {
        try{
            currentNode= zkClient.create("/"+rootNode+"/"+subNode,null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            Thread.sleep(10);

            List<String> childrenNodes=zkClient.getChildren("/"+rootNode,false);
            if(childrenNodes.size() ==1 )
            {
                return;
            }else{
                Collections.sort(childrenNodes);

                String thisNode=currentNode.substring(("/"+rootNode+"/").length());
                int index=childrenNodes.indexOf(thisNode);

                if(index==-1)
                {
                    System.out.println("数据异常");
                }else if(index == 0)
                {
                    return;
                }else{
                    this.waitPath="/"+rootNode+"/"+childrenNodes.get(index-1);
                    zkClient.getData(waitPath,true,new Stat());
                    waitLatch.await();
                    return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    public void zkUnlock(){
        try{
            zkClient.delete(this.currentNode,-1);
        }catch (InterruptedException | KeeperException e)
        {
            e.printStackTrace();
        }
    }
}
