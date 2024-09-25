package kaba4cow.traderclient;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBarSeries;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.binance.connector.futures.client.impl.futures.Market;

import kaba4cow.traderclient.data.OrderBook;
import kaba4cow.traderclient.data.Symbol;
import kaba4cow.traderclient.data.Ticker;
import kaba4cow.traderclient.ta.bars.ApiBar;
import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.utils.Parameters;
import kaba4cow.traderclient.utils.TimeUtils;

public class BinanceFutures {

	private static final BinanceFutures instance = new BinanceFutures();

	private final FuturesClient client;
	private final Market market;

	private final Map<String, List<String>> assets;
	private final Map<String, Symbol> symbols;

	private BinanceFutures() {
		client = new UMFuturesClientImpl();
		market = client.market();

		assets = new LinkedHashMap<>();
		symbols = new LinkedHashMap<>();

		JSONArray json = new JSONObject(market.exchangeInfo()).getJSONArray("symbols");
		for (int i = 0; i < json.length(); i++) {
			Symbol symbol = new Symbol(json.getJSONObject(i));
			if (symbol.orderTypes.contains("LIMIT") && symbol.orderTypes.contains("MARKET")) {
				if (!assets.containsKey(symbol.baseAsset))
					assets.put(symbol.baseAsset, new ArrayList<>());
				assets.get(symbol.baseAsset).add(symbol.quoteAsset);
				symbols.put(symbol.baseAsset + symbol.quoteAsset, symbol);
			}
		}
	}

	public static ZonedDateTime getServerTime() {
		return TimeUtils.getDateTime(new JSONObject(instance.market.time()).getLong("serverTime"));
	}

	public static BaseBarSeries getBarSeries(String symbol, BarInterval interval) {
		return getBarSeries(symbol, interval, 1500);
	}

	public static BaseBarSeries getBarSeries(String symbol, BarInterval interval, int limit) {
		JSONArray json = new JSONArray(//
				instance.market.klines(new Parameters()//
						.put("symbol", symbol)//
						.put("interval", interval.toString())//
						.put("limit", limit)//
						.get()));
		BaseBarSeries series = new BaseBarSeries();
		for (int i = 0; i < json.length(); i++)
			series.addBar(new ApiBar(json.getJSONArray(i), interval));
		return series;
	}

	public static Bar getLastBar(String symbol, BarInterval interval) {
		JSONArray json = new JSONArray(//
				instance.market.klines(new Parameters()//
						.put("symbol", symbol)//
						.put("interval", interval.toString())//
						.put("limit", 1)//
						.get()))
				.getJSONArray(0);
		return new ApiBar(json, interval);
	}

	public static Ticker getTicker(String symbol) {
		JSONObject json = new JSONObject(//
				instance.market.ticker24H(new Parameters()//
						.put("symbol", symbol)//
						.get()));
		return new Ticker(json);
	}

	public static double getPrice(String symbol) {
		JSONObject json = new JSONObject(//
				instance.market.tickerSymbol(new Parameters()//
						.put("symbol", symbol)//
						.get()));
		return json.getDouble("price");
	}

	public static OrderBook getOrderBook(String symbol, int limit) {
		return new OrderBook(new JSONObject(//
				instance.market.depth(new Parameters()//
						.put("symbol", symbol)//
						.put("limit", limit)//
						.get())));
	}

	public static Set<String> getBaseAssets() {
		return instance.assets.keySet();
	}

	public static List<String> getQuoteAssets(String baseAsset) {
		if (baseAsset == null)
			return new ArrayList<>();
		return instance.assets.get(baseAsset);
	}

	public static Symbol getSymbol(String symbol) {
		return instance.symbols.get(symbol);
	}

	public static Set<String> getAssets() {
		return new LinkedHashSet<>(instance.assets.keySet());
	}

	public static Set<String> getSymbols() {
		return new LinkedHashSet<>(instance.symbols.keySet());
	}

}
