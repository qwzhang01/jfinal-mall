package cn.qw.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ConnPool {

    private List<Connection> CONN_POOL = null;
    private List<Channel> CHANNEL_POOL = null;
    private int channelPoolSize = 10;
    private int connPoolSize = 1;

    void init(int channelPoolSize, int connPoolSize, String host, int port, String username, String password, String virtualHost) {
        this.channelPoolSize = channelPoolSize;
        this.connPoolSize = connPoolSize;

        CONN_POOL = Collections.synchronizedList(new LinkedList<>());
        for(int i = 0; i < connPoolSize; i++){
            CONN_POOL.add(creatConn(host, port, username, password, virtualHost));
        }

        CHANNEL_POOL = Collections.synchronizedList(new LinkedList<>());
        for(int i = 0; i < channelPoolSize; i++){
            Connection conn = null;
            try {
                 conn = getConn();
                CHANNEL_POOL.add(conn.createChannel());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                freeConn(conn);
            }
        }
    }

    Connection getConn(){
        Connection connection = CONN_POOL.get(0);
        CONN_POOL.remove(0);
        return connection;
    }

    Channel getChannel(){
        Channel connection = CHANNEL_POOL.get(0);
        CHANNEL_POOL.remove(0);
        return connection;
    }

    void freeChannel(Channel channel){
        if(CHANNEL_POOL.size() <= channelPoolSize) {
            CHANNEL_POOL.add(channel);
        }else{
            closeChannel(channel);
        }
    }

    void freeConn(Connection connection){
        if(connection == null) {
            return;
        }
        if(CONN_POOL.size() <= connPoolSize) {
            CONN_POOL.add(connection);
        }else{
            closeConn(connection);
        }
    }

    Channel creatChannel(Connection connection) {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Connection creatConn(String host, int port, String username, String password, String virtualHost) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        try {
            // 创建与RabbitMQ服务器的TCP连接
            return factory.newConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    void closeChannel(Channel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    void closeConn(Connection connection) {
        try {
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void close() {

        CONN_POOL.stream().forEach(c->{
            closeConn(c);
        });
        CHANNEL_POOL.stream().forEach(c->{
            closeChannel(c);
        });
    }
}