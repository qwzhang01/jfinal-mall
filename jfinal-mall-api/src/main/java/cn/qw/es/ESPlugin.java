package cn.qw.es;

import cn.qw.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class ESPlugin implements IPlugin {
    private Log log = Log.getLog(getClass());
    private String ip;
    private int port;
    private String clusterName;

    public ESPlugin(String ip, int port, String clusterName) {
        this.ip = ip;
        this.port = port;
        this.clusterName = clusterName;
    }

    @Override
    public boolean start() {
        if(StrKit.isBlank(this.ip) || this.port == 0 || StrKit.isBlank(this.clusterName)) {
            return true;
        }

        Settings settings = Settings.builder()
                //设置ES实例的名称
                .put("cluster.name", this.clusterName)
                //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
                .put("client.transport.sniff", true)
                .build();

        /**
         * 这里的连接方式指的是没有安装x-pack插件,如果安装了x-pack则参考{@link ElasticsearchXPackClient}
         * 1. java客户端的方式是以tcp协议在9300端口上进行通信
         * 2. http客户端的方式是以http协议在9200端口上进行通信
         */
        try {
            TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(this.ip), this.port));
            List<DiscoveryNode> nodes = client.connectedNodes();
            if (nodes.isEmpty()) {
                log.info("No NODES Connected");
            } else {
                for (DiscoveryNode node : nodes) {
                    log.info("节点信息：" + node.getHostName() + node.getName() + node.getHostAddress());
                }
            }
            log.info("ElasticsearchClient 连接成功,节点包括：" + JsonKit.toJson(client.listedNodes()));
            ES.init(client);
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean stop() {
        return ES.close();
    }
}