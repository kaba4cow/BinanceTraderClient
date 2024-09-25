package kaba4cow.traderclient.data;

import org.json.JSONObject;

public class Ticker {

	public final double priceChange;
	public final double priceChangePercent;
	public final double lowPrice;
	public final double highPrice;
	public final double lastPrice;
	public final double baseVolume;
	public final double quoteVolume;
	public final long trades;

	public Ticker(JSONObject json) {
		priceChange = json.getDouble("priceChange");
		priceChangePercent = json.getDouble("priceChangePercent");
		lowPrice = json.getDouble("lowPrice");
		highPrice = json.getDouble("highPrice");
		lastPrice = json.getDouble("lastPrice");
		baseVolume = json.getDouble("volume");
		quoteVolume = json.getDouble("quoteVolume");
		trades = json.getLong("count");
	}

}
