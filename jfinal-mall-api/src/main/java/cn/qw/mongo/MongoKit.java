package cn.qw.mongo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Log;
import com.mongodb.Block;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;


public enum MongoKit {

    /**
     * 枚举实现单例模式
     */
    INSTANCE;
    private static MongoClient client;
    private static MongoDatabase defaultDb;
    private Log log = Log.getLog(getClass());

    public void init(MongoClient client, String database) {
        MongoKit.client = client;
        MongoKit.defaultDb = client.getDatabase(database);
    }

    /**
     * 获取客户端连接
     *
     * @return
     */
    public MongoClient getClient() {
        return client;
    }

    /**
     * 根据集合名称获取集合
     *
     * @param collectionName
     * @return
     */
    public MongoCollection<Document> getCollection(String collectionName) {
        return defaultDb.getCollection(collectionName);
    }

    /**
     * 批量插入
     *
     * @param collectionName
     * @param docs
     * @param ops
     */
    public void insert(String collectionName, List<Document> docs, InsertManyOptions ops) {
        getCollection(collectionName).insertMany(uniding(docs), ops);
    }

    /**
     * 插入单条数据
     *
     * @param collectionName
     * @param doc
     */
    public void insert(String collectionName, Document doc) {
        getCollection(collectionName).insertOne(uniding(doc));
    }

    /**
     * 聚合操作
     *
     * @param collectionName
     * @param query
     * @param allowDiskUse
     * @return
     */
    public List<JSONObject> aggregate(String collectionName, List<Bson> query, boolean allowDiskUse) {
        final List<JSONObject> list = new ArrayList<>();
        Block<Document> block = document -> {
            document = iding(document);
            list.add(parseObject(document.toJson()));
        };
        getCollection(collectionName).aggregate(query).allowDiskUse(allowDiskUse).forEach(block);
        return list;
    }

    /**
     * 聚合操作
     *
     * @param collectionName
     * @param query
     * @param allowDiskUse
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> aggregate(String collectionName, List<Bson> query, boolean allowDiskUse, final Class<T> clazz) {
        final List list = new ArrayList();
        Block<Document> block = document -> {
            document = iding(document);
            list.add(parseObject(document, clazz));
        };
        getCollection(collectionName).aggregate(query).allowDiskUse(allowDiskUse).forEach(block);
        return list;
    }

    public List<JSONObject> find(String collectionName, Bson projection) {
        return find(collectionName, new BsonDocument(), new BsonDocument(), projection, 0, 0, "");
    }

    public List<JSONObject> find(String collectionName, int limit, Bson sort, Bson projection) {
        return find(collectionName, new BsonDocument(), sort, projection, limit, 0, "");
    }

    public List<JSONObject> find(String collectionName, int limit, int skip, Bson sort, Bson projection, String join) {
        return find(collectionName, new BsonDocument(), sort, projection, limit, skip, join);
    }

    public <T> List<T> find(String collectionName, int limit, Bson sort, Bson projection, Class<T> clazz) {
        return find(collectionName, new BsonDocument(), sort, projection, limit, 0, "", clazz);
    }

    public <T> List<T> find(String collectionName, int limit, int skip, Bson sort, Bson projection, String join, Class<T> clazz) {
        return find(collectionName, new BsonDocument(), sort, projection, limit, skip, join, clazz);
    }

    public List<JSONObject> find(String collectionName, Bson query, Bson projection) {
        return find(collectionName, query, new BsonDocument(), projection, 0, 0, "");
    }

    public long count(String collectionName, Bson query) {
        return getCollection(collectionName).count(query);
    }

    public long count(String collectionName) {
        return getCollection(collectionName).count();
    }

    public JSONObject findOne(String collectionName, Bson query, Bson sort, String join) {
        return toJSON(
                iding(jointing(getCollection(collectionName).find(query).sort(sort).first(), join))
        );
    }

    public <T> T findOne(String collectionName, Bson query, Bson sort, String join, Class<T> clazz) {
        return parseObject(
                iding(jointing(getCollection(collectionName).find(query).sort(sort).first(), join))
                , clazz);
    }

    public List<JSONObject> find(String collectionName, Bson query, Bson sort, Bson projection, int limit,
                                 int skip, final String join) {
        final List<JSONObject> list = new ArrayList<>();
        Block<Document> block = document -> {
            document = iding(document);
            document = jointing(document, join);
            list.add(toJSON(document));
        };
        getCollection(collectionName).find(query).projection(projection).sort(sort).limit(limit).skip(skip).forEach(block);
        return list;
    }

    public <T> List<T> find(String collectionName, Bson query, Bson sort, Bson projection, int limit, int skip,
                            final String join, final Class<T> clazz) {
        final List list = new ArrayList();
        Block<Document> block = document -> {
            document = iding(document);
            document = jointing(document, join);
            list.add(parseObject(document, clazz));
        };
        getCollection(collectionName).find(query).projection(projection).sort(sort).limit(limit).skip(skip).forEach(block);
        return list;
    }


    public long update(String collectionName, Bson queue, Bson data) {
        UpdateResult updateResult = getCollection(collectionName).updateMany(queue, data);
        return updateResult.getModifiedCount();
    }

    public long updateOne(String collectionName, Bson queue, Bson data) {
        UpdateResult updateResult = getCollection(collectionName).updateOne(queue, data);
        return updateResult.getModifiedCount();
    }

    public long replaceOne(String collectionName, Bson queue, Document document) {
        UpdateResult updateResult = getCollection(collectionName).replaceOne(queue, document);
        return updateResult.getModifiedCount();
    }

    public long delete(String collectionName, Bson queue) {
        DeleteResult deleteResult = getCollection(collectionName).deleteMany(queue);
        return deleteResult.getDeletedCount();
    }

    public long deleteOne(String collectionName, Bson queue) {
        DeleteResult deleteResult = getCollection(collectionName).deleteOne(queue);
        return deleteResult.getDeletedCount();
    }

    public String validation(Object obj) {
        //用于存储验证后的错误信息
        StringBuffer buffer = new StringBuffer(64);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        //验证某个对象,其实也可以只验证其中的某一个属性的
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
        Iterator iter = constraintViolations.iterator();
        while (iter.hasNext()) {
            ConstraintViolation c = (ConstraintViolation) iter.next();
            buffer.append(c.getMessage());
        }
        return buffer.toString();
    }

    /**
     * 校验单个属性
     */
    public String validation(Object obj, String[] keys) {
        //用于存储验证后的错误信息
        StringBuffer buffer = new StringBuffer(64);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> constraintViolations = new HashSet<ConstraintViolation<Object>>();
        for (String key : keys) {
            Iterator<ConstraintViolation<Object>> it = validator.validateProperty(obj, key).iterator();
            if (it.hasNext()) {
                constraintViolations.add(it.next());
            }
        }

        Iterator iter = constraintViolations.iterator();
        while (iter.hasNext()) {
            ConstraintViolation c = (ConstraintViolation) iter.next();
            buffer.append(c.getMessage());
        }

        return buffer.toString();
    }

    public String setIndex(String collectionName, Bson bson) {
        return getCollection(collectionName).createIndex(bson);
    }

    public List<String> setIndex(String collectionName, List<IndexModel> list) {
        return getCollection(collectionName).createIndexes(list);
    }

    public List<JSONObject> getIndex(String collectionName) {
        final List list = new ArrayList();
        Block<Document> block = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                list.add(parseObject(document.toJson()));
            }
        };
        getCollection(collectionName).listIndexes().forEach(block);
        return list;
    }

    public void deleteIndex(String collectionName, Bson bson) {
        getCollection(collectionName).dropIndex(bson);
    }

    public void deleteIndex(String collectionName) {
        getCollection(collectionName).dropIndexes();
    }

    private Document iding(Document document) {
        try {
            if (document == null || document.get("_id") == null) {
                return document;
            } else {
                document.put("_id", document.get("_id").toString());
            }
        } catch (ClassCastException e) {
            /*如果转换出错直接返回原本的值,不做任何处理*/
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return document;
    }

    private List<Document> uniding(List<Document> list) {
        List<Document> newList = new ArrayList<Document>();
        for (Document doc : list) {
            newList.add(uniding(doc));
        }
        return newList;
    }

    private Document uniding(Document document) {
        try {
            if (document == null || document.get("_id") == null) {
                return document;
            } else {
                document.remove("_id");
            }
        } catch (ClassCastException e) {
            /*如果转换出错直接返回原本的值,不做任何处理*/
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return document;
    }

    private Document jointing(Document document, String join) {
        if (join != null && !join.isEmpty()) {
            try {
                DBRef dbRef = document.get(join, DBRef.class);
                Document joinDoc = getCollection(dbRef.getCollectionName())
                        .find(new Document("_id", dbRef.getId())).first();
                joinDoc = iding(joinDoc);
                joinDoc.put("id", joinDoc.getString("_id"));
                joinDoc.remove("_id");
                document.put(join, joinDoc);
            } catch (ClassCastException e) {
                /*用于避免如果key对应的值并不是DBRef,如果转换出错直接返回原本的值,不做任何处理*/
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return document;

    }

    /**
     * 由于fastjson转换空对象时就会直接抛出异常,而在实际查询中查不到东西是很正常的,所以为了避免会有空异常,特别做了异常处理
     */
    private JSONObject parseObject(String json) {
        try {
            if (json != null && !json.isEmpty()) {
                return JSON.parseObject(json);
            }
            return new JSONObject();
        } catch (NullPointerException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return new JSONObject();
        }
    }

    private <T> T parseObject(Document doc, Class<T> clazz) {
        try {
            if (doc == null) {
                return JSON.parseObject(new JSONObject().toJSONString(), clazz);
            }
            return JSON.parseObject(JSON.toJSONString(doc), clazz);
        } catch (NullPointerException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return JSON.parseObject(new JSONObject().toJSONString(), clazz);
        }
    }

    private JSONObject toJSON(Object obj) {
        try {
            return (JSONObject) JSON.toJSON(obj);
        } catch (NullPointerException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return new JSONObject();
        }
    }

    public Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                /*过滤class属性  */
                if (!key.equals("class")) {
                    //*得到property对应的getter方法*/
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return map;
    }


    public <T> Document toDocument(T t) {
        String json = JSON.toJSONString(t);
        Document document = Document.parse(json);
        return document;
    }
}



