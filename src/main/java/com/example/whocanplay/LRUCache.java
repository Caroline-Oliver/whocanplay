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


    private Integer size;
    private Map<String,List<Map<String,Object>>> cache;
    private Map<String,List<String>> filters;
    private Map<String,String> playabilityMap;
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
        this.playabilityMap = Map.of(
                "Playability: Highest","ORDER BY percentage_playable(GameInfo.gpu_id,GameInfo.vram) DESC",
                "Playability: Lowest","ORDER BY percentage_playable(GameInfo.gpu_id,GameInfo.vram) ASC",
                "Alphabetically: A-Z","ORDER BY GameInfo.game_name ASC",
                "Alphabetically: Z-A","ORDER BY GameInfo.game_name DESC"
        );
    }

    //The cache can keep track of the list of values by making an initial call to the database to check a set of all possible filters (AZ,ZA,processors,graphics)

    public void lruPut(String key,List<Map<String,Object>> value){
       this.cache.put(key,value);
    }

    public List<Map<String,Object>> lruGet(String key){
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
                "Playability", List.of("Playability: Highest","Playability: Lowest","Alphabetically: A-Z","Alphabetically: Z-A"),
                "Processor",processorList,
                "Gpu",graphicsList
        );

        //For query parameters
        List<List<String>> queryFilterParameters = List.of(
                List.of("processor_name","Processor"),
                List.of("gpu_name","Gpu")
        );


        String query = "SELECT %s,COUNT(GameInfo.%s_id) AS occ FROM %s,GameInfo WHERE %s.%s_id = GameInfo.%s_id GROUP BY %s.%s_id ORDER BY occ desc";

        Statement stmt = null;
        ResultSet rs = null;


        try{
            connection = DriverManager.getConnection(WhocanplayApplication.URL,WhocanplayApplication.SQLUSERNAME,WhocanplayApplication.SQLPASSWORD);
            stmt = connection.createStatement();

            for (List<String> params: queryFilterParameters) {
                String formattedQuery = String.format(query,params.get(0),params.get(1),params.get(1),params.get(1),params.get(1),params.get(1),params.get(1),params.get(1));
                System.out.println(formattedQuery);

                rs = stmt.executeQuery(formattedQuery);
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


