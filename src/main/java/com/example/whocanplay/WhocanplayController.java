package com.example.whocanplay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter


@CrossOrigin
@RestController
public class WhocanplayController {

    //Autowired variables
    private LRUCache lruCache;


    private final String FORTNITEURL = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTegOws2NDAvoep6r9uCpF8ttSHdoZ8io4ZBNc1mOKxeqGklWG7";
    private final String MINECRAFTURL = "https://www.minecraft.net/content/dam/games/minecraft/key-art/Games_Subnav_Minecraft-300x465.jpg";
    private final String SISTERLOCATIONURL = "https://cdn.cloudflare.steamstatic.com/steam/apps/506610/header.jpg?t=1579635985";
    private final String FALLGUYSURL = "https://upload.wikimedia.org/wikipedia/en/d/d4/Fall_Guys_Post_F2P_keyart.png";


    //Upon assignment, we need to make a request and get all of our possible parameters. This way we can just reference them



    @Autowired
    public WhocanplayController(LRUCache lruCache){
       this.setLruCache(lruCache);
   }

    @GetMapping("/")
    public String index () {
        return "Greetings from WhoCanPlay!";
    }

    //For this one, we want to add some parameters that will also have defaults associated with them
    //NOTE: Could create defaults for filterArgs and gameName just to be on the safe side to not worry about nulls
    @GetMapping("/search")
    public @ResponseBody List<Map<String,Object>> search(
            @RequestParam(name = "filterArgs", required = false) String filterArgs,
            @RequestParam(name = "gameName", required = false) String gameArg,
            @RequestParam(name="orderBy", defaultValue = "Playability: Highest") String orderBy
    ) throws JsonProcessingException {

        //Checks if any args have been passed and if so, they are empty

        //FUNC: Creates our object parser
        ObjectMapper gameArgumentParser = new ObjectMapper();
        //Decodes the URL arguments passed or returns null if not passed
        String decodedFilterArgs = (null == filterArgs || filterArgs.isEmpty())  ? null : URLDecoder.decode(filterArgs, StandardCharsets.UTF_8);

        //FUNC: Parses out the serialized json string into our game filters. These arguments will then be able to be sent into the sql request
        TypeReference<Map<String,Set<String>>> filterTypeRef = new TypeReference<>(){};
        Map<String,Set<String>> gameFilters = (null == filterArgs || filterArgs.isEmpty()) ? null : gameArgumentParser.readValue(decodedFilterArgs, filterTypeRef);


        System.out.println("All argumnets passed in:" +gameArg + orderBy +gameFilters);


        String query = buildQuery(gameArg,gameFilters,orderBy);

        //FUNC: Check and see if requested query is already in our cache
        //FUNC If our query result was non-empty,return
        List<Map<String,Object>> queryResult = lruCache.lruGet(query);
        if (null != queryResult){
            System.out.println("Already calculated!");
            return queryResult;
        }

        System.out.println("Query is: " + query);

        List<Map<String,Object>> queryResults = makeSearchRequest(query);
        lruCache.lruPut(query,queryResults);

        return queryResults;

    }

    @GetMapping("/explore")
    public @ResponseBody List<Map<String,Object>> explore(){
        //FIXME: Once we end up getting the request and everything working, we can then go back and do error checking such as nulls and stuff

        //FUNC This query will get all the games. From there, we will have the user filter out what they want to see
        String query = "SELECT GameInfo.game_name AS gameName,Processor.processor_name AS processor, GPU.gpu_name AS gpu, GameInfo.game_description AS description, GameInfo.game_url as url, percentage_playable(GameInfo.gpu_id,GameInfo.vram) AS Percent FROM GameInfo INNER JOIN Gpu ON GameInfo.gpu_id = Gpu.gpu_id INNER JOIN Processor ON Processor.processor_id = GameInfo.processor_id ORDER BY percentage_playable(GameInfo.gpu_id,GameInfo.vram) DESC";

        if (lruCache.lruContainsKey(query)) return lruCache.lruGet(query);

        List<Map<String,Object>> queryResults =  makeSearchRequest(query);

        lruCache.lruPut(query,queryResults);
        return queryResults;
    }


    //TODO: We want to query by throwing in any sort of search parameters and then making the query and ordering it by plability
    public List<Map<String,Object>> makeSearchRequest(String query){

        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;

        List<Map<String,Object>> queryResults = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(WhocanplayApplication.URL,WhocanplayApplication.SQLUSERNAME,WhocanplayApplication.SQLPASSWORD);
            statement = connection.createStatement();

            resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                queryResults.add(
                        Map.of(
                                "gameName",resultSet.getString("gameName"),
                                "processor",resultSet.getString("processor"),
                                "gpu",resultSet.getString("gpu"),
                                "description",resultSet.getString("description"),
                                "url",resultSet.getString("url"),
                                "percent",resultSet.getDouble("percent")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        finally {
            LRUCache.freeConnections(connection,statement,resultSet);
        }
        return queryResults;
    }

    //FUNC: All this does is return the filters to the frontend
    @GetMapping("/filters")
    public Map<String,List<String>> Filters(){
       return lruCache.getFilters();
    }

    public String buildQuery(String gameArg,Map<String,Set<String>> gameFilters,String orderBy){

        //FUNC: Initialize query with the basic set up that each one will end up running
        StringBuilder query = new StringBuilder("SELECT GameInfo.game_name AS gameName,Processor.processor_name AS processor, GPU.gpu_name AS gpu, GameInfo.game_description AS description, GameInfo.game_url as url, percentage_playable(GameInfo.gpu_id,GameInfo.vram) AS Percent FROM GameInfo INNER JOIN Processor ON GameInfo.processor_id = Processor.processor_id INNER JOIN Gpu ON GameInfo.gpu_id = Gpu.gpu_id");

        //FUNC This where clauseAdded is needed in order to know if we need to append ANDS or not
        boolean whereClauseAdded = false;

        //FUNC Checks if a game name exists and trims the game name to remove all leading and trailing spaces
        gameArg = (null == gameArg || gameArg.isEmpty()) ? null:gameArg;
        //FUNC: Pre-process this for any sort of spaces and replaces any non-character with a dot for regex purposes
        if (null != gameArg){
            String gameArgProcessed = gameArg.trim().chars()
                    .mapToObj(ch-> Character.isLetter(ch) ? (char)ch : (char)'.')
                    .collect(StringBuilder::new,StringBuilder::append,StringBuilder::append)
                    .toString();
            query.append(" WHERE game_name REGEXP '").append(gameArgProcessed).append("'");
            whereClauseAdded = true;
            System.out.println("Processed gameArg: " + gameArgProcessed);
        }

        // FUNC This gets all of the key names and their respective columns as we have no other way of knowing these
        Map<String,String> filterColumns = Map.of(
                "Processor","Processor.processor_name",
                "Gpu","GPU.gpu_name"
        );

        //This will loop through all of our possible filters in a specific order for caching purposes
        //NOTE: Test to see if it is even worth it to cache all of this in terms of speed
        if (gameFilters != null){
            for(Map.Entry<String,List<String>> entry: lruCache.getFilters().entrySet()){
                String k = entry.getKey();
                List<String> v = entry.getValue();
                if (gameFilters.containsKey(k)){
                    if (Objects.equals(k, "Playability")){
                        System.out.println("Acknowledged, but skipping");
                        continue;
                    }
                    if (whereClauseAdded){
                        query.append(" AND");
                    }
                    else{
                        query.append(" WHERE");
                        whereClauseAdded = true;
                    }
                    Set<String> gameFilter = gameFilters.get(k);
                    boolean addOrClause = false;

                    for (String filter : lruCache.getFilters().get(k)){
                        if (gameFilter.contains(filter)){
                            if (addOrClause){
                                query.append(" OR ")
                                        .append(filterColumns.get(k))
                                        .append("='")
                                        .append(filter)
                                        .append("'");
                            }
                            else{
                                query.append(" ")
                                        .append(filterColumns.get(k))
                                        .append("='")
                                        .append(filter)
                                        .append("'");
                                addOrClause = true;
                            }
                        }
                    }
                }
            }
        }

        //FUNC We will finally append the order by clause by using our key to get our string for the order by clause which is already created in our lrucahce

        query.append(" ");
        query.append(lruCache.getPlayabilityMap().getOrDefault(orderBy,"Playability: Highest"));


        return query.toString();
    }
}

