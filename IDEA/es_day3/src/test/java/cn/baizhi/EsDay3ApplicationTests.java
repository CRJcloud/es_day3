package cn.baizhi;

import com.alibaba.fastjson.JSONObject;
import cn.baizhi.entity.Emp;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = EsDay3Application.class)

class EsDay3ApplicationTests {
    @Autowired
    private  RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {
        System.out.println(restHighLevelClient);
    }


    //创建索引
    @Test
    void testIndex() throws IOException {
        Emp emp = new Emp("1",20,"小程",new Date(),"这是个好学生");
        //                                           创建索引      指定索引名 类型名  文档id
        IndexRequest indexRequest = new IndexRequest("ems","emp",emp.getId());
        //                          添加一个文档 （需要String类型的） 如果是一个java对象需要转json
        indexRequest.source(JSONObject.toJSONString(emp), XContentType.JSON);
        //                      通过客户端 连接es创建索引
        IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(index.status());
    }
    //查一个
    @Test
    void testIndexGet() throws IOException {
        //这是索引信息:                            索引名     类型名    文档id
        GetRequest getRequest = new GetRequest("ems","emp","1");
        //   通过客户端对象  获取 一个索引信息
        GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        //表示文档 以json形式返回
        System.out.println(response.getSourceAsString());

        Map<String, Object> map = response.getSourceAsMap();
        /*      key    value
                age     18
                des     这是一个好学生，但是贪玩
                ...
         */
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Object value = map.get(key);
            System.out.println(key+" "+value);
        }
    }

    //修改文档
    @Test
    void testUpdate() throws IOException {
        //这是索引信息:                            索引名     类型名    文档id
        Emp emp = new Emp(null, 23, "小程max", new Date(), "这是一个超级好学生");

        UpdateRequest updateRequest = new UpdateRequest("ems", "emp", "1");
        updateRequest.doc(JSONObject.toJSONString(emp),XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());

    }

    //删除
    @Test
    void testDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("ems", "emp", "1");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());

    }

}


















