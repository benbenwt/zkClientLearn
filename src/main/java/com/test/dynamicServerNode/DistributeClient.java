package com.test.dynamicServerNode;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DistributeClient {
    private static String connectString="127.0.0.1:2181";
    private static int sessionTimeout=2000;
    private ZooKeeper zkClient=null;
    private String parentNode="/servers";

    public  void getConnect() throws IOException {
        zkClient=new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            public void process(WatchedEvent event) {
                try {
                    getServerList();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getServerList() throws KeeperException, InterruptedException {
        List<String> children= zkClient.getChildren(parentNode,true);
        ArrayList<String> servers=new ArrayList<String>();
        for(String child:children){
            byte[] data= zkClient.getData(parentNode+"/"+child,false,null);
            servers.add(new String(data));
        }
        System.out.println(servers);
    }

    public void business() throws InterruptedException {
        System.out.println("client is working ...");
        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeClient client=new DistributeClient();
        client.getConnect();
        client.getServerList();
        client.business();
    }
}
