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
    private Map<String,List<String>> filters;

    public LRUCache(){
        this.setSize(10);
        //The true here is toggled on because if it was false, it would go off of insertion order vs accessOrder which is what we want
        this.setCache(new LinkedHashMap<>(size,0.75f,true){
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > size;
                }
            }
        );
        this.filters = requestFilters();
    }

    //The cache can keep track of the list of values by making an initial call to the database to check a set of all possible filters (AZ,ZA,processors,graphics)

    public void lruPut(String key,List<Map<String,String>> value){
       this.cache.put(key,value);
    }

    public List<Map<String,String>> lruGet(String key){
        return cache.getOrDefault(key,null);
    }
    public boolean lruContainsKey(String key){
        return (cache.containsKey(key));
    }

    private Map<String,List<String>> requestFilters(){
        //TODO: We need an ordered list so that we can properly parse this stuff so that we can guarantee that a

        //NOTE: These will of course be later replaced by a SQL query
        List<String> graphicsList = List.of("GeForce GTX 6GB","Geforce infinity","Xbox 360 or sum shit");
        List<String> processorList = List.of("Intel Core i5","Intel core processor 502940","Intel Core process 45","AMD Athlon");

        return Map.of(
                "Alphabetically", List.of("AZ","ZA"),
                "processors",processorList,
                "graphics",graphicsList
        );

    }

}
