package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Services;

import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models.Favorite;
import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Repositories.FavoriteRepository;
import org.knowm.xchange.ExchangeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class SchedulerService {

    @Autowired
    ChatService chatService;

    @Autowired
    BinanceService binanceService;

    @Autowired
    FavoriteRepository favoriteRepository;


    @Scheduled(cron = "0 */2 * * * *")
    public void checkPriceJumps() throws Exception {
        binanceService.checkForJumps();
    }
    @Scheduled(cron = "*/30 * * * * *")
    public void test() throws Exception{
        List<Favorite> faves = favoriteRepository.findAll();
        for(Favorite f : faves){
            List<Double> prices = f.getPriceQueue();
            if(prices.size() > 10){
                prices.remove(prices.size() - 1);
            }
            //get new price and .add(0, price);
            Double currLast = binanceService.getEthPrice(f.getTicker());
            prices.add(0, currLast);
            f.setLastEthPrice(f.getCurrentEthPrice());
            f.setCurrentEthPrice(currLast);
            f.setPriceQueue(prices);
            favoriteRepository.save(f);

            //check for diff
//            Double newest = f.getPriceQueue().get(0);
//            Double oldest = f.getPriceQueue().get(f.getPriceQueue().size()-1);
//            System.out.println(f.getTicker() + "\t\tNew: " + newest + ", Old: " + oldest + ", X1.05: " + (oldest*1.05));
            /*if(newest > (oldest*1.05) && f.isAlerted()){
                System.out.println("***********ALERTED FOR " + f.getTicker() + "***************");
                chatService.send(f.getTicker() + " greater than 5% change in past 5 mins!");
                f.setAlerted(true);
                favoriteRepository.save(f);
            }*/
        }
       //chatService.send("TEST: " + i);
       return;
    }


    @Scheduled(cron = "0 */10 * * * *")
    private void resetAlerted(){
        List<Favorite> faves = favoriteRepository.findAll();
        for(Favorite f : faves){
            f.setAlerted(false);
            favoriteRepository.save(f);
        }
    }
}
