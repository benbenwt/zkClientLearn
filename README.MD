>Learn zookeeper client.
>
>统一配置：将配置信息存储到Znode中，需要该配置的监视事件。
>
>动态上下线：服务启动时到zookeeper注册临时节点，下线时断开链接，临时节点就会消失。通过监视zookeeper的临时node，确认有哪些服务节点在线。
>
>分布式锁：1，lock时尝试创建znode，然后检查是否最小编号，最小则获得锁。否则，注册监听比它小一个序号的znode，并await当前线程。
>
>
>          2，unlock时直接删除申请的znode
>
>          3，收到 比当前线程申请的znode小一个序号的znode被删除的事件信息时，说明当前线程可以唤醒了，使用countDown唤醒。