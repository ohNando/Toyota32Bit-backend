package com.example.kafka.opensearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import com.example.kafka.entity.Rate;

@Service
public class OpensearchService {
    private final RestHighLevelClient client;

    public OpensearchService(RestHighLevelClient _client){
        this.client = _client;
    }

    public void updateRate(Rate rate) throws IOException {
        if(rate == null || rate.getRateName() == null){
            System.err.println("Rate list is empty. No action taken.");
            return;
        }
        System.out.println("Rates are being updated in Opensearch");

        try{
            this.indexRate(rate);
        }catch(Exception e){
            System.err.println("Error while indexing rate "+rate.getRateName()+" to OpenSearch: " + e.getMessage());
            throw e;
        }
    }

    private void indexRate(Rate rate) throws IOException{
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("rateName", rate.getRateName());
        jsonMap.put("bid",rate.getBid());
        jsonMap.put("ask",rate.getAsk());
        jsonMap.put("rateUpdateTime",rate.getRateUpdateTime());
        jsonMap.put("@Timestamp",rate.getRateUpdateTime());

        IndexRequest request = new IndexRequest("rates");
        request.id(rate.getRateName());
        request.source(jsonMap,XContentType.JSON);

        System.out.println("Indexing rate: " + rate.getRateName());
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println("Rate" + rate.getRateName() +
         "updated. Result: "+ response.getResult() + ", ID: " + response.getId());
    }
}
