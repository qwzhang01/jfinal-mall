package com.qw.service.frontend.good;

import cn.qw.base.BaseService;
import cn.qw.es.ES;
import cn.qw.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;

public class GoodSearchService extends BaseService {

    private static GoodSearchService service;

    private GoodSearchService() {
    }

    public static synchronized GoodSearchService me() {
        if (service == null) {
            service = new GoodSearchService();
        }
        return service;
    }


    /**
     * 搜索
     */
    public List<Record> search(String keyword, String index, String type) {

        QueryBuilder queryBuilders = null;
        if (StrKit.notBlank(keyword)) {
            queryBuilders = QueryBuilders.queryStringQuery(keyword);
        } else {
            queryBuilders = QueryBuilders.matchAllQuery();
        }

        SearchResponse response = ES.search().prepareSearch(index)
                .setTypes(type)
                .setQuery(queryBuilders)
                .setExplain(true)
                .execute().actionGet();

        SearchHits hits = response.getHits();
        List<Record> articles = new ArrayList<>();

        for (SearchHit searchHit : hits) {
            String sourceAsString = searchHit.getSourceAsString();
            Record article = JsonKit.toRecord(sourceAsString);
            articles.add(article);
        }
        return articles;
    }
}