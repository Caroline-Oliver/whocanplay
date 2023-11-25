package com.example.whocanplay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Getter
@Setter
@ToString()

@Service
public class LRUCache {
    public static final String SQLUSERNAME = "springuser";
    public static final String SQLPASSWORD = "#Kwanz9Laur3nCarolinEW3reHere";

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


    //Now we need to grab our filters
    private Map<String,List<String>> requestFilters(){

        Connection connection = null;


        //Set up arraylists for the values
        List<String> processorList = new ArrayList<>();
        List<String>  graphicsList = new ArrayList<>();

        //Map that we want to return
        Map<String,List<String>> allFilters = Map.of(
                "Alphabetically", List.of("AZ","ZA"),
                "Processor",processorList,
                "Gpu",graphicsList
        );

        //For query parameters
        List<List<String>> queryFilterParameters = List.of(
                List.of("processor_name","Processor"),
                List.of("gpu_name","Gpu")
        );


        String query = "SELECT %s from %s";

        Statement stmt = null;
        ResultSet rs = null;



        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/WHOCANPLAY",SQLUSERNAME,SQLPASSWORD);
            stmt = connection.createStatement();

            for (List<String> params: queryFilterParameters) {
                rs = stmt.executeQuery(String.format(query,params.get(0), params.get(1)));
                while (rs.next()) {
                    allFilters.get(params.get(1)).add(rs.getString(1));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        finally {
            freeConnections(connection, stmt, rs);
        }

        System.out.println("All filters will be: " + allFilters);
        return allFilters;
    }

    static void freeConnections(Connection connection, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) { } // ignore
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) { } // ignore
            stmt = null;
        }
        if (connection !=null){
            try{
                connection.close();
            }catch(SQLException ignored) {}
            connection = null;
        }
    }

}


