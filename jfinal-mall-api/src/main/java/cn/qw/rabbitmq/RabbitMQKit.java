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

    public static Channel getChannel(Channel channel){
        return pool.getChannel();
    }

    public static void free(Channel channel){
        pool.freeChannel(channel);
    }
}