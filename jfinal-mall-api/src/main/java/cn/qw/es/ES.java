package cn.qw.es;

import cn.qw.kit.BeanKit;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.util.Map;

public class ES {
    private static TransportClient client = null;

    public static void init(TransportClient client) {
        if (client == null) {
            throw new RuntimeException("ES初始化失败");
        }
        ES.client = client;
    }

    public static boolean close() {
        if (ES.client != null) {
            ES.client.close();
        }
        return true;
    }

    public static boolean addIndex(String index) {
        return isIndexExist(index)
                ? false
                : client.admin().indices().prepareCreate(index).execute().actionGet().isAcknowledged();
    }

    public static boolean isIndexExist(String index) {
        return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
    }

    public static boolean delIndex(String index) {
        return isIndexExist(index)
                ? client.admin().indices().prepareDelete(index).execute().actionGet().isAcknowledged()
                : false;
    }

    public static boolean isTypeExist(String index, String type) {
        return isIndexExist(index)
                ? client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists()
                : false;
    }

    public static boolean addType(String index, String type, Object bean) {
        if(!isIndexExist(index)){
            addIndex(index);
        }
        if(isTypeExist(index, type)){
            return true;
        }
        // 创建索引映射,相当于创建数据库中的表操作
        CreateIndexRequestBuilder cib = client.admin().indices().prepareCreate(index);

        // 设置自定义字段
        XContentBuilder mapping = null;
        try {
            XContentBuilder properties = XContentFactory.jsonBuilder().startObject().startObject("properties");
            Map<String, String> beanToDoc = BeanKit.fieldType(bean);
            for(String name: beanToDoc.keySet()) {
                String typeName = beanToDoc.get(name);
                if("date".equals(typeName)) {
                    properties.startObject(name).field("type", typeName).field("format", "yyyy-MM-dd HH:mm:ss").endObject();
                }else{
                    properties.startObject(name).field("type", typeName).endObject();
                }
            }

            mapping = properties.endObject().endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cib.addMapping(type, mapping);
        return cib.execute().actionGet().isAcknowledged();
    }

    public static long addDoc(String index, String type, Object object, String id)  {
        if(!isTypeExist(index, type)) {
            addType(index, type, object);
        }

        Map<String, Object> map = BeanKit.toMap(object);
        XContentBuilder xContentBuilder = null;
        try {
            xContentBuilder = XContentFactory.jsonBuilder().startObject();
            for(String k: map.keySet()) {
                xContentBuilder.field(k, map.get(k));
            }
            xContentBuilder = xContentBuilder.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IndexResponse response = client.prepareIndex(index, type, id).setSource(xContentBuilder).get();
        return response.getVersion();
    }

    public static String delDoc(String index, String type, Object id) {
        return client.prepareDelete(index, type, String.valueOf(id)).get().getId();
    }


    /************************************查询*****************************************/
    public static String findById(String index, String type, String id){
        GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
        return response.getSourceAsString();
    }

    public static SearchResponse matchAllQuery(String index) {
        QueryBuilder query = QueryBuilders.matchAllQuery();
        SearchResponse response = client.prepareSearch(index).setQuery(query).execute().actionGet();
        return response;
    }

    public static SearchResponse matchAllQueryInType(String index, String type) {
        SearchResponse response = client.prepareSearch(index).setTypes(type).execute().actionGet();
        return response;
    }

    public static TransportClient search(){
        return client;
    }
}