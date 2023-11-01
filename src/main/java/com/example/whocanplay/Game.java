package com.example.whocanplay;

import java.util.Map;

public class Game {
    private String name,graphics,directX,processor,description,percent;

    public Game(String name, String graphics, String directX,String processor,String description,String percent){
        this.setName(name);
        this.setGraphics(graphics);
        this.setDirectX(directX);
        this.setProcessor(processor);
        this.setDescription(description);
        this.setPercent(percent);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGraphics() {
        return graphics;
    }

    public void setGraphics(String graphics) {
        this.graphics = graphics;
    }

    public String getDirectX() {
        return directX;
    }

    public void setDirectX(String directX) {
        this.directX = directX;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + getName() + '\'' +
                ", graphics='" + getGraphics() + '\'' +
                ", directX='" + getDirectX() + '\'' +
                ", processor='" + getProcessor() + '\'' +
                '}';
    }
    public Map<String,String> getGameData(){
        return Map.of(
                "name",getName(),
                "graphics",getGraphics(),
                "directX",getDirectX(),
                "processor",getProcessor()
        );
    }
}
