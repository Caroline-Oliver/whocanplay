package com.example.whocanplay;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin
@RestController
public class WhocanplayController {

    @GetMapping("/")
    public String index () {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/explore")
    public String explore(){
        return "This is where explore will be!";
    }

    @GetMapping("/search")
    public List<Map<String,String>> search(){
        Game fortnite = new Game("Fortnite","GeForce GTX 6GB",
                "Version 12","4 hardware CPU Threads Intel Core i5 750 or higher","Fortnite battle pass","80%");
        Game minecraft = new Game("Minecraft","Geforce infinity",
                "version 12","Intel core processor 502940","So we back in the mine","70%");
        Game fallGuys = new Game("Fall Guys","Xbox 360 or sum shit","Version 12",
                "Intel Core process 45","Idk a meme for this one","80%");
        Game Fnaf = new Game("Five Night's at Freddy's: Sister Location","GeForce GTX 6GB","Version 12",
                "A random processor","Har Har Har Har","100%");
        return new ArrayList<>(List.of(fortnite.getGameData(),minecraft.getGameData(),fallGuys.getGameData(),Fnaf.getGameData()));
    }
}
