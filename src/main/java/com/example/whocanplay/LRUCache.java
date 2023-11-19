package com.example.whocanplay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.*;

@Getter
@Setter
@ToString()

@Service
public class LRUCache {

    private Integer size;
    private Map<String,List<Map<String,String>>> cache;

    public LRUCache(){
        this.setSize(10);
        //The true here is toggled on because if it was false, it would go off of insertion order vs accessOrder which is what we want
        this.setCache(new LinkedHashMap<>(size,0.75f,true){
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > size;
                }
            }
        );
    }


    public void lruPut(String key,List<Map<String,String>> value){
       this.cache.put(key,value);
    }

    public List<Map<String,String>> lruGet(String key){
        return cache.getOrDefault(key,null);
    }
    public boolean lruContainsKey(String key){
        return (cache.containsKey(key));
    }

}
