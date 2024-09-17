package com.example.demo.Repository;

import com.example.demo.Beans.PhieuGiamGiaBean;
import com.example.demo.Utils.PhieuGiamGiaMap;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class PhieuGiamGiaDAO  {
    RestTemplate rest = new RestTemplate();
    String url = "https://datn-9eb87-default-rtdb.firebaseio.com/phieugiamgia.json";

    private String getUrl(String key){
        return url.replace(".json","/"+key+".json");
    }

    public PhieuGiamGiaMap findAll(){
        return rest.getForObject(url, PhieuGiamGiaMap.class);
    }

    public PhieuGiamGiaBean findByKey(String key){
        return rest.getForObject(getUrl(key),PhieuGiamGiaBean.class);
    }

    public  String create(PhieuGiamGiaBean data){
        HttpEntity<PhieuGiamGiaBean> entity = new HttpEntity<>(data);
        JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
        return resp.get("name").asText();
    }

    public  PhieuGiamGiaBean update(String key,PhieuGiamGiaBean data){
        HttpEntity<PhieuGiamGiaBean> entity = new HttpEntity<>(data);
        rest.put(getUrl(key), entity);
        return data;
    }

    public void delete(String key){
        rest.delete(getUrl(key));
    }

}
