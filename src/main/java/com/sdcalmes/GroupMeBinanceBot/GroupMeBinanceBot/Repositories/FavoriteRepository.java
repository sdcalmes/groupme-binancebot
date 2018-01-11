package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Repositories;


import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    Favorite findByTicker(String ticker);

}
