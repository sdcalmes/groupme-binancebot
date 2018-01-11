package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models;

import lombok.Data;
import org.knowm.xchange.currency.CurrencyPair;

public @Data class CurrencyPairPriceJumps {
    private double priceOld;
    private double priceNew;
    private CurrencyPair cp;
}
