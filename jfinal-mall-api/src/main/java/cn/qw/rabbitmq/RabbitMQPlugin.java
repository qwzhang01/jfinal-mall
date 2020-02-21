package cn.qw.rabbitmq;

import com.jfinal.plugin.IPlugin;
import com.rabbitmq.client.*;

import java.io.IOException;

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
        // 初始化
        RabbitMQKit.init(10, 1, host, port, username, password, virtual);
        // 初始化 exchange queue，并绑定
        initQueue();
        initConsume();
        return true;
    }

    /**
     * 初始化消费逻辑-demo
     */
    private void initConsume() {
        Channel channel = null;
        try {
            channel = RabbitMQKit.getChannel();
            channel.basicQos(64);

            Channel finalChannel = channel;
            channel.basicConsume("testquene", false, "testtag", new DefaultConsumer(finalChannel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println(new String(body));
                    long deliveryTag = envelope.getDeliveryTag();
                    finalChannel.basicAck(deliveryTag, true);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(channel != null) {
                RabbitMQKit.free(channel);
                channel = null;
            }
        }
    }

    /**
     * 初始化队列及绑定-demo
     */
    private void initQueue() {
        Channel channel = null;
        try {
            channel = RabbitMQKit.getChannel();
            channel.exchangeDeclare("test-qw", "direct", true);
            String queueName = channel.queueDeclare("testquene", true, true,true, null).getQueue();
            channel.queueBind(queueName, "test-qw", "test");
            // 开启发送方确认模式
            channel.confirmSelect();

            //异步监听确认和未确认的消息
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("未确认消息，标识：" + deliveryTag);
                }
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(channel != null) {
                RabbitMQKit.free(channel);
                channel = null;
            }
        }
    }

    @Override
    public boolean stop() {
        RabbitMQKit.close();
        return true;
    }
}
