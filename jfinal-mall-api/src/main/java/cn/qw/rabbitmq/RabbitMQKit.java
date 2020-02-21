package cn.qw.rabbitmq;

import com.rabbitmq.client.Channel;

public class RabbitMQKit {
    private static ConnPool pool = null;

    static void init(int channelPoolSize, int connPoolSize, String host, int port, String username, String password, String virtualHost){
        ConnPool p = new ConnPool();
        p.init(channelPoolSize, connPoolSize, host, port, username, password, virtualHost);
        RabbitMQKit.pool = p;
    }

    static void close(){
        ConnPool p = new ConnPool();
        p.close();
        p = null;
    }

    /**
     * 多线程间channel实例是不安全的，也就是多线程不可以共享一个channel  TODO 以前不清楚这个，刚刚看文章有提到，所以要看一下这里原来设计的对不对
     * @return
     */
    public static Channel getChannel(){
        return pool.getChannel();
    }

    public static void free(Channel channel){
        pool.freeChannel(channel);
    }
}