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
    //
    @GetMapping("/search")
    public List<Map<String,String>> search(
            @RequestParam(name = "filterArgs", required = false) String filterArgs,
            @RequestParam(name = "gameName", required = false) String gameArg
    ) throws JsonProcessingException {

        //Checks if any args have been passed and if so, they are empty

        System.out.println((!filterArgs.isEmpty()) ? "Args for filter:" + filterArgs : "Empty args");

        //FUNC: Creates our object parser
        ObjectMapper gameArgumentParser = new ObjectMapper();
        //Decodes the URL arguments passed
        String decodedFilterArgs = URLDecoder.decode(filterArgs, StandardCharsets.UTF_8);

        System.out.println(decodedFilterArgs);

        //FUNC: Parses out the serialized json string into our game filters. These arguments will then be able to be sent into the sql request
        TypeReference<Map<String,Set<String>>> filterTypeRef = new TypeReference<>(){};
        Map<String,Set<String>> gameFilters = (null == filterArgs || filterArgs.isEmpty()) ? null : gameArgumentParser.readValue(decodedFilterArgs, filterTypeRef);



        //FUNC: All this does is check if an empty string was passed in or something null
        gameArg = (null == gameArg || gameArg.isEmpty()) ? null:gameArg;

        System.out.println("All argumnets passed in:" + Objects.requireNonNullElse(gameArg, "NO GAME NAME PROVIDED!" + Objects.requireNonNullElse(gameFilters,"NO FILTERS PROVIDED")));

        //FUNC: This puts all of our map values into a string with spaces
        StringBuilder query = new StringBuilder();
        //Checks if a game name exists and trims the game name to remove all leading and trailing spaces
        if (null != gameArg) query.append(gameArg.trim());


        //In order to make this work, we first have to be able to get the order of how everything will parse and we can work from there
        lruCache.getFilters().forEach((k,v)->{
            //This will then see if a key is contained and if it is, we will throw in all it's values
            assert gameFilters != null;
            if (gameFilters.containsKey(k)){
                Set<String> gameFilter = gameFilters.get(k);
                for (String filter : lruCache.getFilters().get(k)){
                    if (gameFilter.contains(filter)){
                        query.append(" ").append(filter);
                    }
                }
            }
        });

        String queryValue = query.toString();

        //FUNC: Check and see if requested query is already in our cache
        List<Map<String,String>> queryResult = lruCache.lruGet(queryValue);

        //If our query result was non-empty,return
        if (null != queryResult){
            System.out.println("Already calculated!");
            return queryResult;
        }


        System.out.println("Query is: " + queryValue);
        //FUNC: If not, make request to SQL database
        //TODO: Fix the types that we pass in, but for now we will just return the basic type
//        List<Map<String,String>> queryResults = this.makeSearchRequest(gameFilters);
        List<Map<String,String>> queryResults = makeSearchRequest(null);
        lruCache.lruPut(queryValue,queryResults);

        //NOTE: Placeholder while we unfuck
        return makeSearchRequest(null);


    }

    @GetMapping("/explore")
    public List<Map<String,String>> explore(){
        //NOTE: Playability is calculated by throwing in the gpu_id and it's vram. We will calculate this and add it to our map for each game that we will send
        //FIXME: Once we end up getting the request and everything working, we can then go back and do error checking such as nulls and stuff
        if (lruCache.lruContainsKey("")) return lruCache.lruGet("");

        List<Map<String,String>> queryResults =  makeSearchRequest(null);

        lruCache.lruPut("",queryResults);
        return queryResults;
    }


    //TODO: We want to query by throwing in any sort of search parameters and then making the query and ordering it by plability
    public List<Map<String,String>> makeSearchRequest(Map<String,String> args){
        //NOTE: Basically all of this logic will eventually be replaced by the SQL

        List<Map<String,String>> games = new ArrayList<>();
        for(int i = 0; i < 4;i++){
            games.addAll(List.of(
                    new Game("Fortnite","GeForce GTX 6GB",
                            "Version 12","4 hardware CPU Threads Intel Core i5 750 or higher", """
                            Explore large, destructible environments where no two games are ever the same. Team up with friends by sprinting, climbing and smashing your way to earn your Victory Royale, whether you choose to build up in Fortnite Battle Royale or go no-builds in Fortnite Zero Build.
                            Discover even more ways to play across thousands of creator-made game genres: adventure, roleplay, survival and more. Or, band together with up to three friends to fend off hordes of monsters in Save the World.
                            ""","80", FORTNITEURL).getGameData(),

                    new Game("Minecraft","Geforce infinity",
                            "version 12","Intel core processor 502940","Minecraft is a game made up of blocks, creatures, and community. Blocks can be used to reshape the world or build fantastical creations. Creatures can be battled or befriended, depending on your playstyle. Experience epic adventures solo or with friends, there’s no wrong way to play.\n" +
                            "Unless you’re digging straight down.","70", MINECRAFTURL).getGameData(),

                    new Game("Fall Guys","Xbox 360 or sum shit","Version 12",
                            "Intel Core process 45", """
                            You’re invited to dive and dodge your way to victory in the pantheon of clumsy. Rookie or pro? Solo or partied up? Fall Guys delivers ever-evolving, high-concentrated hilarity and fun. Prefer to be the maverick behind the mayhem? Build your very own obstacle course to share with friends or the wider community.
                            Create your own Course: Fall Guys Creative is a level editor that allows you to create fiendish custom Rounds and share them with the wider community.
                            Competitive & Cooperative: Tumble between competitive free-for-alls and cooperative challenges—or take on the Blunderdome with up to 3 friends!
                            Play with Friends: Fall Guys supports cross-play, cross-platform parties and cross-progression via your Epic Games Account.
                            Ever-Evolving Content: Play stays fresh with new collabs and game updates which bring new Costumes, Obstacles, and ways to play.""","80", FALLGUYSURL).getGameData(),

                    new Game("Five Night's at Freddy's: Sister Location","GeForce GTX 6GB","Version 12",
                            "AMD Athlon","Welcome to Circus Baby's Pizza World, where family fun " +
                            "and interactivity go beyond anything you've seen at those *other* pizza places! Now hiring: " +
                            "Late night technician. Must enjoy cramped spaces and be comfortable around active machinery. " +
                            "Not responsible for death or dismemberment.","100", SISTERLOCATIONURL).getGameData()
            ));
        }
        Game roblox = new Game("Roblox","GeForce GTX 6GB","Version 12",
                "AMD Athlon","Roblox is an online game platform and game creation system developed " +
                "by Roblox Corporation that allows users to program games and play games created by other users.","20",
                "https://images.rbxcdn.com/c83761712c58384892d63501cad3a1ee");
        games.add(roblox.getGameData());

        //PLAN: Add to check if we actually have arguments. If not, or they are blank, we return everything

        return (null == args) ? games : games.stream()
                .filter(obj-> obj.get("name").equalsIgnoreCase(args.get("name")))
                .collect(Collectors.toList());
    }

    //FUNC: All this does is return the filters to the frontend
    @GetMapping("/filters")
    public Map<String,List<String>> Filters(){
       return lruCache.getFilters();
    }

}
