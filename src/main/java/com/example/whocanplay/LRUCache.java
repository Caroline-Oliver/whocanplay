package com.example.whocanplay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString()

@Service
public class LRUCache {

    private Integer size;
    private Map<String,Map<String,String>> cacheValues;
    private List<String> orderedListValues;

    public LRUCache(){
        this.setSize(10);
        this.setCacheValues(new HashMap<>(10));
        this.setOrderedListValues(new LinkedList<>());
    }

    //Implement rest of cache methods
    public void lruPut(String key,Map<String,String> value){
       this.cacheValues.put(key,value);
    }

    public Map<String,String> lruGet(String key){
        return cacheValues.getOrDefault(key,null);
    }

}
