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

/**
 * Service class responsible for indexing and updating rate data in OpenSearch.
 * <p>
 * This service uses the OpenSearch high-level REST client to index {@link Rate} entities
 * into the "rates" index. Each rate is stored with its name as the document ID.
 */
@Service
public class OpensearchService {
    private final RestHighLevelClient client;

    /**
     * Constructs an {@code OpensearchService} with the provided OpenSearch client.
     *
     * @param _client the OpenSearch {@link RestHighLevelClient} to be used
     */
    public OpensearchService(RestHighLevelClient _client){
        this.client = _client;
    }

    /**
     * Updates the given {@link Rate} object in the OpenSearch "rates" index.
     * <p>
     * If the rate is {@code null} or its name is missing, the operation is skipped.
     *
     * @param rate the {@link Rate} to be indexed
     * @throws IOException if there is an issue during communication with OpenSearch
     */
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

    /**
     * Indexes a {@link Rate} object in the "rates" index in OpenSearch.
     * <p>
     * The document ID is set to the rate name, and relevant fields are serialized as JSON.
     *
     * @param rate the {@link Rate} entity to index
     * @throws IOException if an error occurs during the indexing process
     */
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
