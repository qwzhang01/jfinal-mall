package cn.qw.rabbitmq;

import com.jfinal.plugin.IPlugin;

public class RabbitMQPlugin implements IPlugin {

    private String host;
    private int port;
    private String username;
    private String password;
    private String virtual;

    public RabbitMQPlugin(String host, int port, String username, String password, String virtualHost){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.virtual = virtualHost;
    }

    @Override
    public boolean start() {
        RabbitMQKit.init(10, 1, host, port, username, password, virtual);
        return true;
    }
    @Override
    public boolean stop() {
        RabbitMQKit.close();
        return true;
    }
}
