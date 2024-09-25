package kaba4cow.traderclient.tradingbot;

import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.ta.strategies.MACrossoverStrategyBuilder;
import kaba4cow.traderclient.ta.strategies.StrategyBuilder;

public class TradingBotBuilder {

	private String baseAsset;
	private String quoteAsset;
	private BarInterval interval;
	private double tradeQuantity;
	private StrategyBuilder strategy;

	public TradingBotBuilder() {
		this.baseAsset = "BTC";
		this.quoteAsset = "USDT";
		this.interval = BarInterval.MINUTE1;
		this.tradeQuantity = 1d;
		this.strategy = new MACrossoverStrategyBuilder();
	}

	public TradingBotBuilder setBaseAsset(String baseAsset) {
		this.baseAsset = baseAsset;
		return this;
	}

	public TradingBotBuilder setQuoteAsset(String quoteAsset) {
		this.quoteAsset = quoteAsset;
		return this;
	}

	public TradingBotBuilder setInterval(BarInterval interval) {
		this.interval = interval;
		return this;
	}

	public TradingBotBuilder setTradeQuantity(double tradeQuantity) {
		this.tradeQuantity = tradeQuantity;
		return this;
	}

	public TradingBotBuilder setStrategy(StrategyBuilder strategy) {
		this.strategy = strategy;
		return this;
	}

	public TradingBot build() {
		return new TradingBot(baseAsset, quoteAsset, interval, tradeQuantity, strategy);
	}

}
