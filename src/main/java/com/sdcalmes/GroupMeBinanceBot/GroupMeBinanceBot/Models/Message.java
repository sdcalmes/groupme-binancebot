package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models;

import lombok.Data;

import java.util.List;

public @Data class Message {
    private List<String> attachments;
    private String avatar_url;
    private long created_at;
    private long group_id;
    private long id;
    private String name;
    private int sender_id;
    private String sender_type;
    private String source_guid;
    private boolean system;
    private String text;
    private int user_id;
}
