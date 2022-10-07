package com.test.dynamicServerNode;

import org.apache.zookeeper.*;

import java.io.IOException;

//动态上下线功能指，让客户端实时洞察到服务器上下线的变化
public class DistributeServer {
    private static String connectString="127.0.0.1:2181";
    private static int sessionTimeout=2000;
    private ZooKeeper zkClient=null;
    private String parentNode="/servers";

    public void getConnect() throws IOException {
        zkClient=new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
    }
    public void registServer(String hostname) throws KeeperException, InterruptedException {
        String create= zkClient.create(parentNode+"/"+hostname,hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname+" is online " + create);
    }

    public void business(String hostname) throws InterruptedException {
        System.out.println(hostname+ "is working ... ");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeServer server=new DistributeServer();
        server.getConnect();
        server.registServer("server33");
        server.business("server33");
    }
}
