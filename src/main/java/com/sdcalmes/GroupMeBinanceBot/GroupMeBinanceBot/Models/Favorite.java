package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models;

import com.google.common.collect.EvictingQueue;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Entity
public @Data class Favorite {
   private @Id String id;
   private double lastEthPrice;
   private double currentEthPrice;
   private double lastUsdPrice;
   private double currentUsdPrice;
   @ElementCollection
   private List<Double> priceQueue;
   private String ticker;
   private String addedBy;
   private boolean alerted;
}
