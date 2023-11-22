package com.example.whocanplay;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "GameInfo")
public class GameEntity {


//    @GeneratedValue(strategy = GenerationType.AUTO) I do not think this is needed as we will never insert into the table
    @Id
    @Column(name = "game_id")
    private Integer id;

    @Column(name="steam_game_id")
    private Integer steamGameID;

    @Column(name="game_name")
    private String gameName;

    @Column(name = "gpu_id")
    private Integer gpuID;

    @Column(name="vram")
    private Integer vRAM;




}
