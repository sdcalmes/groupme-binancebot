package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models.Message;
import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Services.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    static String BOT_NAME = "BinanceBot";

    @Autowired
    ChatService chatService;

    @RequestMapping(value = "/message", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void receiveMessage(HttpEntity<Message> m) throws Exception{
        Message message = m.getBody();
        //make sure to check that the message isn't from myself!
        if(message.getName().equals(BOT_NAME))
            return;
        logger.info("Received message from " + message.getName() + " saying \"" + message.getText() + "\"");

        chatService.handle(message);
        return;
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public int returnMessage(){
       return 1;
    }
}
