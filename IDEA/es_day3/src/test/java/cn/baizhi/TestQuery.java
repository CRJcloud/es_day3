package cn.baizhi;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = EsDay3Application.class)
public class TestQuery {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //查所有
    @Test
    void testQueryAll() throws IOException {
        //创建查询对象
        SearchRequest searchRequest = new SearchRequest();
        //查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //指定查询条件 查所有
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.indices("ems")//索引
        .types("emp")//查询类型
                .source(searchSourceBuilder);//查询条件
        //使用客户端对象 完成查询操作 需要一个查询对象
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getMaxScore());//分数
        System.out.println(hits.getTotalHits());//条数

        SearchHit[] hits1 = hits.getHits();
        for (SearchHit document : hits1) {
            System.out.println("当前文档得分:   "+document.getScore());
            System.out.println("当前文档id:  "+document.getId());
            System.out.println(document.getSourceAsString());
            }

        System.out.println("------------------------");

        for (SearchHit document : hits1) {
            Map<String, Object> sourceAsMap = document.getSourceAsMap();
            Set<String> keys = sourceAsMap.keySet();
            for (String key : keys) {
                Object value = sourceAsMap.get(key);
                System.out.println(key+"   "+value);
            }
        }

    }

    //各种查询
    @Test
    void testQuery2() throws IOException {
        //查询对象
        SearchRequest searchRequest = new SearchRequest();
        //查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义查询条件
       // searchSourceBuilder.query(QueryBuilders.termQuery("content","开发"));
//                                                          哪个字段的范围
       // searchSourceBuilder.query(QueryBuilders.rangeQuery("age").gte(8).lte(38));
        //fuzzy  根据分词长度
      //  searchSourceBuilder.query(QueryBuilders.fuzzyQuery("content","spring"));
        //通配符查
       // searchSourceBuilder.query(QueryBuilders.wildcardQuery("content","开?"));
        //ids query
       // searchSourceBuilder.query(QueryBuilders.idsQuery().addIds("0fQIKXsBqCRMkTn21-OM").addIds("0PQIKXsBqCRMkTn20uPG"));
        //boolquery  组合查  must  should  mustnot     （must+mustnot） （should+mustnot）
        // bool查条件对象
       /* BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //       must  必须满足
        boolQuery.must(QueryBuilders.termQuery("name","小"));
        //      mustNot  必须不满足
        boolQuery.mustNot(QueryBuilders.termQuery("name","黑"));
        //          指定条件
        searchSourceBuilder.query(boolQuery);*/
        //queryString 多分词查询
        searchSourceBuilder.query(QueryBuilders.queryStringQuery("开发团队").field("name").field("content"));

        //通过查询对象  查     哪个索引      哪个类型      什么查询条件
        searchRequest.indices("ems").types("emp").source(searchSourceBuilder);

        //查询的响应对象
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit document : hits1) {
            System.out.println("当前文档id:  "+document.getId());
            System.out.println("当前文档得分:  "+document.getScore());
            System.out.println("当前文档内容  "+document.getSourceAsString());//json

        }
    }

    //各种查
    @Test
    public void testQuery() throws IOException {
        //查询对象
        SearchRequest searchRequest = new SearchRequest();
        //查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //定义查询条件
        searchSourceBuilder.query(QueryBuilders.termQuery("content", "开发"));
        //通过查询对象    查     哪个索引   哪个类型         什么查询条件
        searchRequest.indices("ems").types("emp").source(searchSourceBuilder);

        //查询的响应对象
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit document : hits1) {
            System.out.println("当前文档的id ： "+document.getId());
            System.out.println("当前文档得分： "+document.getScore());
            System.out.println("当前文档的内容: "+document.getSourceAsString());//json
        }
    }

    //高亮查询
    @Test
    public void testSearchHig() throws IOException {
        //查询对象
        SearchRequest searchRequest = new SearchRequest();
        //定义查询条件对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //创建一个高亮对象
        HighlightBuilder highlightBuilder =  new HighlightBuilder();
        highlightBuilder.field("content").requireFieldMatch(false).preTags("<span style='color:red;'>").postTags("</span>");
        sourceBuilder.from(0).size(2).sort("age", SortOrder.DESC).highlighter(highlightBuilder).query(QueryBuilders.termQuery("content","框架"));
        searchRequest.indices("ems").types("emp").source(sourceBuilder);
        //查询响应对象
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //拿到内层hits
        SearchHit[] hits = search.getHits().getHits();
        //document  拿到文档相关内容
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            //拿到 元素 高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            highlightFields.forEach((k,v)-> System.out.println("key: "+k + " value: "+v.fragments()[0]));
        }
    }

    //分页查询
    @Test
    void testSearchpage() throws IOException {
        //2.查询对象
        SearchRequest searchRequest = new SearchRequest();
        //4.定义查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //5.条件是 查所有  分页查询  from 从哪开始查  size  查到几条
        sourceBuilder.from(0).size(2).sort("age",
                SortOrder.DESC).query(QueryBuilders.matchAllQuery());
        //3.通过查询对象 查询那个索引  哪个类型  查询条件
        searchRequest.indices("ems").types("emp").source(sourceBuilder);
        //1.        通过客户端对象 进行查询操作  需要参数:查询对象
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //6. 得到查询结果
        SearchHits hits = search.getHits();
        //7.得到查询结果所有文档
        SearchHit[] hits1 = hits.getHits();
        //8.遍历 得到每一个文档
        for (SearchHit document : hits1) {
            //9.  得到当前文档的 分数  id号  文档的json 字符串内容
            System.out.println(document.getScore());
            System.out.println(document.getId());
            System.out.println(document.getSourceAsString());// json字符串
        }
    }
}



















