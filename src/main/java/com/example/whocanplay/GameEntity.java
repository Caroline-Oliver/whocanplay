package com.example.whocanplay;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString()

public class GameEntity {
    private Integer gameID,steamGameID,gpuID,vRAM;
    private String gameName;

    public GameEntity(Integer gameID,Integer steamGameID,String gameName,Integer gpuID,Integer vRAM){
        this.setGameID(gameID);
        this.setSteamGameID(steamGameID);
        this.setGameName(gameName);
        this.setGpuID(gpuID);
        this.setVRAM(vRAM);
    }




}
