package com.example.whocanplay;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@CrossOrigin
@RestController
public class WhocanplayController {
    @GetMapping("/")
    public String index () {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/search")
    public String search(){
        return "This is where Search will be!";
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
                    "Version 12","4 hardware CPU Threads Intel Core i5 750 or higher","Fortnite battle pass","80%", FortniteURL).getGameData(),
                    new Game("Minecraft","Geforce infinity",
                            "version 12","Intel core processor 502940","So we back in the mine","70%", MinecarftURL).getGameData(),
                    new Game("Fall Guys","Xbox 360 or sum shit","Version 12",
                            "Intel Core process 45","Idk a meme for this one","80%", FallGuysURL).getGameData(),
                    new Game("Five Night's at Freddy's: Sister Location","GeForce GTX 6GB","Version 12",
                            "A random processor","Har Har Har Har","100%", SisterLocationURL).getGameData()
                    ));
        }


        return games;
    }
}
