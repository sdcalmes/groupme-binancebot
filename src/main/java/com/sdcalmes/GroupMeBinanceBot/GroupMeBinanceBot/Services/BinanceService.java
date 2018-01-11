package com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Services;

import com.sdcalmes.GroupMeBinanceBot.GroupMeBinanceBot.Models.CurrencyPairPriceJumps;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class BinanceService {

    static Exchange BINANCE = ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName());
    static MarketDataService MDS = BINANCE.getMarketDataService();

    int ru = BigDecimal.ROUND_UP;

    List<CurrencyPairPriceJumps> pJumps = new ArrayList<>();


    @PostConstruct
    private void getCurrencyPairs() throws Exception{
        List<CurrencyPair> cps = BINANCE.getExchangeSymbols();
        for(CurrencyPair cp : cps){
            Ticker cpt = MDS.getTicker(cp);
            CurrencyPairPriceJumps p = new CurrencyPairPriceJumps();
            p.setCp(cp);
            p.setPriceNew(Double.parseDouble(cpt.getLast().toString()));
            p.setPriceOld(Double.parseDouble(cpt.getLast().toString()));
            pJumps.add(p);
        }

    }
    public void checkForJumps() throws Exception{
        for(CurrencyPairPriceJumps cppj : pJumps){
            cppj.setPriceOld(cppj.getPriceNew());
            cppj.setPriceNew(getPairPrice(cppj.getCp()));
            double wut = (1 - cppj.getPriceNew()/cppj.getPriceOld()) * 100;
            if((cppj.getPriceNew()/cppj.getPriceOld()) > 2){
                double pct = ((cppj.getPriceNew()/cppj.getPriceOld()) - 1) * 100;
                System.out.println("Price for ticker: " + cppj.getCp().toString() + " is up " + pct + "% over the last 2 mins.");
            }
        }
    }


    public String getTicker(String ticker) throws Exception{
        BigDecimal tickask, tickbid, ticklast, ethlast, ethask, ethbid;
        StringBuilder sb = new StringBuilder();
        if(ticker.equals("ETH")){
            CurrencyPair cp = new CurrencyPair("ETH", "USDT");
            Ticker ethusdt = MDS.getTicker(cp);
            sb.append("(" + ticker + "USDT)\n");
            sb.append("Curr ask: " + ethusdt.getAsk() + "\n");
            sb.append("Curr bid: " + ethusdt.getBid() + "\n");
            sb.append("Last: " + ethusdt.getLast() + "\n");
            return sb.toString();
        }
        CurrencyPair cp = new CurrencyPair(ticker, "ETH");
        CurrencyPair cp2 = new CurrencyPair("ETH", "USDT");
        Ticker ethusd = MDS.getTicker(cp2);
        Ticker ticketh = MDS.getTicker(cp);
        tickask = ticketh.getAsk();
        tickbid = ticketh.getBid();
        ticklast = ticketh.getLast();
        ethlast = ethusd.getLast();
        ethask = ethusd.getAsk();
        ethbid = ethusd.getBid();

        sb.append("(" + ticker + "USDT)\n");
        sb.append("Curr ask: " + tickask.multiply(ethask).setScale(4, ru) + "\n");
        sb.append("Curr bid: " + tickbid.multiply(ethbid).setScale(4, ru) + "\n");
        sb.append("Last: " + ticklast.multiply(ethlast).setScale(4, ru) + "\n");
        sb.append("\n");
        sb.append("(" + ticker + "ETH)\n");
        sb.append("Curr ask: " + ticketh.getAsk() + "\n");
        sb.append("Curr bid: " + ticketh.getBid() + "\n");
        sb.append("Last: " + ticketh.getLast() + "\n");
        sb.append("VWAP: " + ticketh.getVwap());

        return sb.toString();
    }

    public double getPairPrice(CurrencyPair cp) throws Exception{
        Ticker t = MDS.getTicker(cp);
        return Double.parseDouble(t.getLast().toString());
    }

    public double getEthPrice(String ticker) throws Exception{
        CurrencyPair cp = new CurrencyPair(ticker, "ETH");
        Ticker ticketh = MDS.getTicker(cp);
        BigDecimal ticklast = ticketh.getLast();
        return Double.parseDouble(ticklast.toString());
    }


}
