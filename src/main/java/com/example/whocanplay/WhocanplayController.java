package com.example.whocanplay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/*

    Plan for now
    - In order to search, we need to recieve our arguments and also be able to clean our query before we send it off
        to our query
    - We also want to be able to cache our queries in order to not have to go to the database as much as possible
        therefore, we want to keep an LRUCache that will keep the top 10 or 12 queries that we use based off of the
            frequency in which they are called. The LRUCache is bascially a special hashmap that also keep order in which things are ordered
 */
@Getter
@Setter


@CrossOrigin
@RestController
public class WhocanplayController {
    //Autowire our class of our cache here
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
            @RequestParam(name = "searchArgs", required = false) String searchArgs
    ) throws JsonProcessingException {

        //TODO: We need a data cleaning function to help with pre processing

        //Checks if any args have been passed and if so, they are empty
        if ( null == searchArgs || searchArgs.isEmpty()){
            //TODO: Change this so that it makes a query to get everything
            return null;
        }

        //FUNC: Creates our object parser
        ObjectMapper gameArgumentParser = new ObjectMapper();
        //FUNC: Parses out the serialized json string into our game arguments. These arguments will then be able to be sent into the sql request
        Map<String,String> gameArguments = gameArgumentParser.readValue(searchArgs, new TypeReference<HashMap<String,String>>() {});


        //FUNC: This puts all of our map values into a string with spaces
        String query = String.join(" ",gameArguments.values());

        //FUNC: Check and see if requested query is already in our cache
        if (lruCache.lruContainsKey(query)){
            return lruCache.lruGet(query);
        }
        System.out.println("Query is:" + query);
        //FUNC: If not, make request to SQL database
        List<Map<String,String>> queryResults = this.makeSearchRequest(gameArguments);
        lruCache.lruPut(query,queryResults);

        return queryResults;

    }

    @GetMapping("/explore")
    public List<Map<String,String>> explore(){

        //FIXME: Once we end up getting the request and everything working, we can then go back and do error checking such as nulls and stuff

        return makeSearchRequest(null);
    }


    //TODO: This will make the request to the SQL Server
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

    //FUNC: All that this route does is make a request to the SQL database to get a collection of all of the possible processors and graphics cards
    @GetMapping("/filters")
    public Map<String,List<String>> Filters(){

        //TODO: We need an ordered list so that we can properly parse this stuff so that we can guarantee that a

        //NOTE: These will of course be later replaced by a SQL query
        List<String> graphicsList = List.of("GeForce GTX 6GB","Geforce infinity","Xbox 360 or sum shit","GeForce GTX 6GB");
        List<String> processorList = List.of("Intel Core i5","Intel core processor 502940","Intel Core process 45","AMD Athlon");

        return Map.of(
            "processors",processorList,
                "graphics",graphicsList
        );

    }
}
