package org.hackstyle.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.hackstyle.dto.Index;
import org.json.JSONArray;
import org.json.JSONObject;


public class IndexRequest {

    public JSONArray getAllIndexes() throws IOException {
        
     RestClientBuilder builder = RestClient.builder(new HttpHost("dejt.ua01.trtsp.jus.br", 9200, "http"));
        Header[] defaultHeaders = new Header[]{new BasicHeader("Content-Type", "application/json")};
        builder.setDefaultHeaders(defaultHeaders);

        RestClient client = builder.build();
        
        Request request = new Request("GET", "/_cat/indices?format=json&pretty");
        Response response = client.performRequest(request);

        RequestLine requestLine = response.getRequestLine();
        HttpHost host = response.getHost();
        int statusCode = response.getStatusLine().getStatusCode();
        Header[] headers = response.getHeaders();
        String responseBody = EntityUtils.toString(response.getEntity());
        
        JSONArray jsonArray = new JSONArray(responseBody);

        List<Index> list = new ArrayList<>();
        
        for (int i=0; i<jsonArray.length(); i++) {
            
            JSONObject json = jsonArray.getJSONObject(i);
            
            Index index = new Index();
            
            index.setHealth(json.getString("health"));
            index.setStatus(json.getString("status"));
            index.setIndex(json.getString("index"));
            index.setUuid(json.getString("uuid"));
            index.setPrimary(json.getString("pri"));
            index.setReplica(json.getString("rep"));
            index.setTotalDocs(json.getString("docs.count"));
            index.setDeletedDocs(json.getString("docs.deleted"));
            index.setStoreSize(json.getString("store.size"));
            index.setPriStoreSize(json.getString("pri.store.size"));
            
            response = client.performRequest(new Request("GET", "/" + index.getIndex() + "?format=json&pretty"));
            
            responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonIndex = new JSONObject(responseBody);
            
            JSONObject jsonX = jsonIndex.getJSONObject(index.getIndex());
            String mapping = jsonX.getJSONObject("mappings").toString();
            String settings = jsonX.getJSONObject("settings").toString();
            
            index.setMapping(mapping);
            index.setSettings(settings);
            
            list.add(index);
        }
        
        client.close();
        
        JSONArray jsonA = new JSONArray(list);
        System.out.println(jsonA);
        
        return jsonA;
    }
}
