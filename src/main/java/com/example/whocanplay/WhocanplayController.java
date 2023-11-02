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
        String URL = "https://ih1.redbubble.net/image.3553194397.0820/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg";
        Game fortnite = new Game("Fortnite","GeForce GTX 6GB",
                "Version 12","4 hardware CPU Threads Intel Core i5 750 or higher","Fortnite battle pass","80%", URL);
        Game minecraft = new Game("Minecraft","Geforce infinity",
                "version 12","Intel core processor 502940","So we back in the mine","70%", URL);
        Game fallGuys = new Game("Fall Guys","Xbox 360 or sum shit","Version 12",
                "Intel Core process 45","Idk a meme for this one","80%", URL);
        Game Fnaf = new Game("Five Night's at Freddy's: Sister Location","GeForce GTX 6GB","Version 12",
                "A random processor","Har Har Har Har","100%", URL);
        return new ArrayList<>(List.of(fortnite.getGameData(),minecraft.getGameData(),fallGuys.getGameData(),Fnaf.getGameData()));
    }
}
