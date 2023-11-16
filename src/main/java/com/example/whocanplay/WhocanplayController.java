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
    public String search(
            @RequestParam(name = "searchArgs", required = false) String searchArgs
    ) throws JsonProcessingException {
        if ( null == searchArgs || searchArgs.isEmpty()){
            return "ERROR! Parameters required >:(";
        }
        Map<String,String> toInsrt = Map.of(
                "name","Fortnie"
        );
        lruCache.lruPut("Test",toInsrt);
        Map<String,String> result = lruCache.lruGet("Test");
        System.out.printf("The result of the get of name is: %s%n",result.getOrDefault("name","ERROR"));

        //Creates our object parser
        ObjectMapper gameArgumentParser = new ObjectMapper();
        //Parses out the serialized json string into our game arguments. These arguments will then be able to be sent into the sql request
        Map<String,String> gameArguments = gameArgumentParser.readValue(searchArgs, new TypeReference<HashMap<String,String>>() {});


        return gameArguments.getOrDefault("gameName","No Game Name provided:(");
    }

    @GetMapping("/explore")
    public List<Map<String,String>> explore(){
        String FortniteURL = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcTegOws2NDAvoep6r9uCpF8ttSHdoZ8io4ZBNc1mOKxeqGklWG7";
        String MinecarftURL = "https://www.minecraft.net/content/dam/games/minecraft/key-art/Games_Subnav_Minecraft-300x465.jpg";
        String SisterLocationURL = "https://cdn.cloudflare.steamstatic.com/steam/apps/506610/header.jpg?t=1579635985";
        String FallGuysURL = "https://upload.wikimedia.org/wikipedia/en/d/d4/Fall_Guys_Post_F2P_keyart.png";
        List<Map<String,String>> games = new ArrayList<>();
        for(int i = 0; i < 3;i++){
            games.addAll(List.of(
                    new Game("Fortnite","GeForce GTX 6GB",
                    "Version 12","4 hardware CPU Threads Intel Core i5 750 or higher", """
                            Explore large, destructible environments where no two games are ever the same. Team up with friends by sprinting, climbing and smashing your way to earn your Victory Royale, whether you choose to build up in Fortnite Battle Royale or go no-builds in Fortnite Zero Build.
                            Discover even more ways to play across thousands of creator-made game genres: adventure, roleplay, survival and more. Or, band together with up to three friends to fend off hordes of monsters in Save the World.
                            ""","80%", FortniteURL).getGameData(),

                    new Game("Minecraft","Geforce infinity",
                            "version 12","Intel core processor 502940","Minecraft is a game made up of blocks, creatures, and community. Blocks can be used to reshape the world or build fantastical creations. Creatures can be battled or befriended, depending on your playstyle. Experience epic adventures solo or with friends, there’s no wrong way to play.\n" +
                            "Unless you’re digging straight down.","70%", MinecarftURL).getGameData(),

                    new Game("Fall Guys","Xbox 360 or sum shit","Version 12",
                            "Intel Core process 45", """
                            You’re invited to dive and dodge your way to victory in the pantheon of clumsy. Rookie or pro? Solo or partied up? Fall Guys delivers ever-evolving, high-concentrated hilarity and fun. Prefer to be the maverick behind the mayhem? Build your very own obstacle course to share with friends or the wider community.
                            Create your own Course: Fall Guys Creative is a level editor that allows you to create fiendish custom Rounds and share them with the wider community.
                            Competitive & Cooperative: Tumble between competitive free-for-alls and cooperative challenges—or take on the Blunderdome with up to 3 friends!
                            Play with Friends: Fall Guys supports cross-play, cross-platform parties and cross-progression via your Epic Games Account.
                            Ever-Evolving Content: Play stays fresh with new collabs and game updates which bring new Costumes, Obstacles, and ways to play.""","80%", FallGuysURL).getGameData(),

                    new Game("Five Night's at Freddy's: Sister Location","GeForce GTX 6GB","Version 12",
                            "A random processor","Welcome to Circus Baby's Pizza World, where family fun " +
                            "and interactivity go beyond anything you've seen at those *other* pizza places! Now hiring: " +
                            "Late night technician. Must enjoy cramped spaces and be comfortable around active machinery. " +
                            "Not responsible for death or dismemberment.","100%", SisterLocationURL).getGameData()
                    ));
        }

        
        return games;
    }
}
