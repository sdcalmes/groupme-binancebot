package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Services;

import com.google.common.collect.EvictingQueue;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models.Favorite;
import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models.Message;
import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Repositories.FavoriteRepository;
import com.sun.javafx.fxml.builder.URLBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class ChatService {

    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    BinanceService binanceService;

    @Value("${bot_id}")
    private String BOT_ID;

    Logger logger = LoggerFactory.getLogger(ChatService.class);

    static String BASE_URL = "https://api.groupme.com/v3/bots/post";

    public void send(String text) throws Exception{
        sendChat(text);
    }

    public void handle(Message m) throws Exception{
        handleMessage(m);
    }


    private void handleMessage(Message m) throws Exception{
        String text = m.getText();
        String sender = m.getName();

        String[] splitOnSpaces = text.split(" ");

        switch(splitOnSpaces[0].toLowerCase()){
            case "bbhelp":
                sendChat("Available commands:\necho [text]\njokeplz\ncheckfave [ticker]\nsavefave [ticker]\nshowfaves\nshowfaves [ticker]");
                break;
            case "echo":
                sendChat(m.getText().substring(5));
               break;
            case "jokeplz":
                sendChat("I heard that " + sender + " is gay!");
                break;
            case "checkfave":
                sendChat(binanceService.getTicker(splitOnSpaces[1].toUpperCase()));
                break;
            case "savefave":
                Favorite fave = new Favorite();
                fave.setTicker(splitOnSpaces[1].toUpperCase());
                fave.setAddedBy(m.getName());
                fave.setPriceQueue(new ArrayList<Double>());
                Double lastPrice = binanceService.getEthPrice(fave.getTicker());
                fave.setCurrentEthPrice(lastPrice);
                fave.setLastEthPrice(lastPrice);
                logger.info("Got favorite: " + fave.getTicker());
                if(favoriteRepository.findByTicker(fave.getTicker()) == null) {
                    //TODO: check binance to make sure this exists
                    favoriteRepository.save(fave);
                    sendChat("Saved favorite: " + fave.getTicker());
                } else {
                    sendChat("Fave already saved!");
                }
                break;
            case "showfaves":
                StringBuilder sb = new StringBuilder();

                if(splitOnSpaces.length > 1){
                   Favorite singleFave = favoriteRepository.findByTicker(splitOnSpaces[1].toUpperCase());
                   if(singleFave != null) {
                       sb.append("Ticker: " + singleFave.getTicker() + "\n");
                       sb.append("Added by: " + singleFave.getAddedBy() + "\n");
                       sb.append("Current Price (ETH): " + singleFave.getCurrentEthPrice());
                       sendChat(sb.toString());
                   } else {
                       sendChat("Fave not found.");
                   }
                   break;
                }
                List<Favorite> faves = favoriteRepository.findAll();
                if(faves.size() <= 0){
                    sendChat("No faves found.");
                    break;
                }
                sb.append("Current favorites: ");
                for(Favorite f : faves){
                    sb.append(f.getTicker() + ", ");
                }
                sb.deleteCharAt(sb.length() - 2);
                sendChat(sb.toString());
                break;
            default:
                return;
        }
    }

    private void parseMessage(Message m){

    }

    public void testMessage() throws Exception{
        HttpRequestWithBody h = Unirest.post(BASE_URL);
        HttpResponse<JsonNode> jsonResponse = Unirest.post(BASE_URL)
                .header("accept", "application/json")
                .field("bot_id", BOT_ID)
                .field("text", "TEST!")
                .asJson();
        logger.info(jsonResponse.getBody().toString());
    }

    private void sendChat(String text) throws Exception{
        HttpResponse<JsonNode> jsonResponse = Unirest.post(BASE_URL)
                .header("accept", "application/json")
                .field("bot_id", BOT_ID)
                .field("text", text)
                .asJson();
        logger.info(jsonResponse.getBody().toString());
    }
}
