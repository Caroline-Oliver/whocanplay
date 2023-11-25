package com.example.whocanplay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
@Getter
@Setter
@ToString(includeFieldNames = true)



public class Game {
    private String name,graphics,directX,processor,description,percent,imageURL;

    public Game(String name, String graphics, String directX,String processor,String description,String percent,String imageURL){
        this.setName(name);
        this.setGraphics(graphics);
        this.setDirectX(directX);
        this.setProcessor(processor);
        this.setDescription(description);
        this.setPercent(percent);
        this.setImageURL(imageURL);
    }





    public Map<String,String> getGameData(){
        return Map.of(
                "name",getName(),
                "graphics",getGraphics(),
                "directX",getDirectX(),
                "processor",getProcessor(),
                "description",getDescription(),
                "percent",getPercent(),
                "imageURL",getImageURL()
        );
    }
}
