package com.example.whocanplay;

import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<GameEntity,Integer> {

}
