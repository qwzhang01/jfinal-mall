package cn.qw.mongo;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

public class MongoPlugin implements IPlugin {

    private Log log = Log.getLog(getClass());
    private MongoClient client;


    private MongoClientOptions.Builder options = MongoClientOptions.builder();

    private List<ServerAddress> hostList = new ArrayList<>();

    private List<MongoCredential> authList = new ArrayList<>();

    /*数据库名*/
    private String database;

    public MongoPlugin add(String host) {
        this.hostList.add(new ServerAddress(host, 27017));
        return this;
    }

    public MongoPlugin add(String host, int port) {
        this.hostList.add(new ServerAddress(host, port));
        return this;
    }

    public String getDatabase() {
        return this.database;
    }

    public MongoPlugin setDatabase(String database) {
        this.database = database;
        return this;
    }

    /*数据库授权*/
    public MongoPlugin auth(MongoCredential mongoCredential) {
        this.authList.add(mongoCredential);
        return this;
    }

    /*数据库授权*/
    public MongoPlugin auth(String username, String password) {
        this.authList.add(MongoCredential.createScramSha1Credential(username, this.database, password.toCharArray()));
        return this;
    }

    public MongoPlugin authCR(String username, String password) {
        this.authList.add(MongoCredential.createMongoCRCredential(username, this.database, password.toCharArray()));
        return this;
    }

    public MongoPlugin authX509(String x509) {
        this.authList.add(MongoCredential.createMongoX509Credential(x509));
        return this;
    }

    /*SSL*/
    public MongoPlugin ssl() {
        this.options.sslEnabled(true);
        return this;
    }

    public MongoPlugin connectTimeout(int connectTimeout) {
        this.options.connectTimeout(connectTimeout);
        return this;
    }

    public MongoPlugin opition(MongoClientOptions.Builder opitions) {
        this.options = opitions;
        return this;
    }


    /*自定义读写分离
     * primary
     * 主节点,默认模式,读操作只在主节点,如果主节点不可用,报错或者抛出异常。
     * primaryPreferred
     * 首选主节点,大多情况下读操作在主节点,如果主节点不可用,如故障转移,读操作在从节点。
     * secondary
     * 从节点,读操作只在从节点, 如果从节点不可用,报错或者抛出异常。
     * secondaryPreferred
     * 首选从节点,大多情况下读操作在从节点,特殊情况（如单主节点架构）读操作在主节点。
     * nearest
     * 最邻近节点,读操作在最邻近的成员,可能是主节点或者从节点,根据ping时间最短
     * */
    public MongoPlugin readPreference(ReadPreference readPreference) {
        this.options.readPreference(readPreference);
        return this;
    }

    public MongoPlugin readPreference() {
        this.options.readPreference(ReadPreference.secondaryPreferred());
        return this;
    }


    public MongoPlugin writeConcern(WriteConcern writeConcern) {
        this.options.writeConcern(writeConcern);
        return this;
    }

    /*前几次修改操作仍然被记录在journal中,可以被还原也可以被撤销,避免数据不一致或弄脏的情况,写成功后再执行到真的数据集中*/
    public MongoPlugin writeConcern() {
        this.options.writeConcern(WriteConcern.JOURNALED);
        return this;
    }

    /*在分布式下写到多个节点后才进行返回*/
    public MongoPlugin writeSafe() {
        this.options.writeConcern(WriteConcern.MAJORITY);
        return this;
    }

    /*如果不是debug模式可以关闭日志*/
    public MongoPlugin setDebug(boolean debug) {
        // MongoKit.INSTANCE.setDebug(debug);
        return this;
    }

    public MongoClient getMongoClient() {

        try {
            return new MongoClient(hostList, authList, options.build());
        } catch (Exception e) {
            throw new RuntimeException("无法连接mongodb,请检查配置!Can't connect mongodb!");
        }

    }

    @Override
    public boolean start() {
        client = getMongoClient();
        MongoKit.INSTANCE.init(client, getDatabase());
        return true;
    }

    @Override
    public boolean stop() {
        if (client != null) {
            client.close();
        }
        return true;
    }


}
