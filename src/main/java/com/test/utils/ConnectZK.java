package com.test.utils;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ConnectZK {
    private static String connectString="127.0.0.1:2181";
    private static int sessionTimeout=2000;
    private ZooKeeper zkClient=null;

    @Before
    public void init() throws IOException {
        zkClient=new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getType()+" -- "+watchedEvent.getPath());
                try{
                    List<String> children=zkClient.getChildren("/",true);
                    for(String child:children)
                    {
                        System.out.println(child);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }
            }
        });

    }

//    创建子节点
    @Test
    public void create() throws Exception{
        String nodeCreated=zkClient.create("/servers/server","wt".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
    }
//  获取子节点并监听节点变化
    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        List<String> children=zkClient.getChildren("/",true);
        for(String child:children)
        {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);
    }
    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat stat=zkClient.exists("/wttest",false);
        System.out.println(stat==null? "not exist" : "exist");
    }
}
