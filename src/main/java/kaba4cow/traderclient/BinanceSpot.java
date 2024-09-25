package kaba4cow.traderclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Trade.TradeType;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.spot.Trade;
import com.binance.connector.client.impl.spot.Wallet;

import kaba4cow.traderclient.data.Balance;
import kaba4cow.traderclient.data.Order;
import kaba4cow.traderclient.data.Order.OrderType;
import kaba4cow.traderclient.data.OrderBook;
import kaba4cow.traderclient.data.Symbol;
import kaba4cow.traderclient.data.Ticker;
import kaba4cow.traderclient.ta.bars.ApiBar;
import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.utils.Parameters;
import kaba4cow.traderclient.utils.Sorter;
import kaba4cow.traderclient.utils.TimeUtils;
import kaba4cow.traderclient.utils.filters.OrderStatusFilter;
import kaba4cow.traderclient.utils.filters.OrderTypeFilter;

public class BinanceSpot {

	private static final BinanceSpot instance = new BinanceSpot();

	private final SpotClient client;
	private final Market market;
	private final Wallet wallet;
	private final Trade trade;

	private final Map<String, List<String>> assets;
	private final Map<String, Symbol> symbols;

	private BinanceSpot() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("api.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (properties.containsKey("api") && properties.containsKey("secret"))
			client = new SpotClientImpl(properties.getProperty("api"), properties.getProperty("secret"));
		else
			client = new SpotClientImpl();
		market = client.createMarket();
		wallet = client.createWallet();
		trade = client.createTrade();

		assets = new LinkedHashMap<>();
		symbols = new LinkedHashMap<>();

		JSONArray json = new JSONObject(market.exchangeInfo(new HashMap<String, Object>())).getJSONArray("symbols");
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

	public static Order newLimitOrder(String symbol, TradeType side, double quantity, double price) {
		JSONObject json = new JSONObject(//
				instance.trade.newOrder(new Parameters()//
						.put("symbol", symbol)//
						.put("type", OrderType.LIMIT)//
						.put("side", side)//
						.put("quantity", quantity)//
						.put("price", price)//
						.put("timeInForce", "GTC")//
						.put("newOrderRespType", "RESULT")//
						.put("recvWindow", 10000l)//
						.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
						.get()));
		return new Order(json);
	}

	public static void testLimitOrder(String symbol, TradeType side, double quantity, double price) {
		instance.trade.testNewOrder(new Parameters()//
				.put("symbol", symbol)//
				.put("type", OrderType.LIMIT)//
				.put("side", side)//
				.put("quantity", quantity)//
				.put("price", price)//
				.put("timeInForce", "GTC")//
				.put("newOrderRespType", "RESULT")//
				.put("recvWindow", 10000l)//
				.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
				.get());
	}

	public static Order newMarketOrder(String symbol, TradeType side, double quantity) {
		JSONObject json = new JSONObject(//
				instance.trade.newOrder(new Parameters()//
						.put("symbol", symbol)//
						.put("type", OrderType.MARKET)//
						.put("side", side)//
						.put("quantity", quantity)//
						.put("newOrderRespType", "RESULT")//
						.put("recvWindow", 10000l)//
						.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
						.get()));
		return new Order(json);
	}

	public static void testMarketOrder(String symbol, TradeType side, double quantity) {
		instance.trade.testNewOrder(new Parameters()//
				.put("symbol", symbol)//
				.put("type", OrderType.MARKET)//
				.put("side", side)//
				.put("quantity", quantity)//
				.put("newOrderRespType", "RESULT")//
				.put("recvWindow", 10000l)//
				.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
				.get());
	}

	public static Order cancelOrder(String symbol, long id) {
		JSONObject json = new JSONObject(//
				instance.trade.cancelOrder(new Parameters()//
						.put("symbol", symbol)//
						.put("orderId", id)//
						.put("recvWindow", 10000l)//
						.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
						.get()));
		return new Order(json);
	}

	public static List<Order> getOrders(String symbol, OrderTypeFilter typeFilter, OrderStatusFilter statusFilter) {
		JSONArray json = new JSONArray(//
				instance.trade.getOrders(new Parameters()//
						.put("symbol", symbol)//
						.put("limit", 1000)//
						.put("recvWindow", 10000l)//
						.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
						.get()));
		List<Order> orders = new ArrayList<>();
		for (int i = 0; i < json.length(); i++) {
			Order order = new Order(json.getJSONObject(i));
			if ((typeFilter == null || typeFilter.accept(order))
					&& (statusFilter == null || statusFilter.accept(order)))
				orders.add(order);
		}
		Collections.sort(orders, Sorter.comparing((o) -> o.time, false));
		return orders;
	}

	public static Balance getBalance() {
		JSONArray json = new JSONObject(//
				instance.trade.account(new Parameters()//
						.put("omitZeroBalances", "true")//
						.put("recvWindow", 10000l)//
						.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
						.get()))
				.getJSONArray("balances");
		return new Balance(json);
	}

	public static double[] getTradeFee(String symbol) {
		JSONObject json = new JSONArray(//
				instance.wallet.tradeFee(new Parameters()//
						.put("symbol", symbol)//
						.put("recvWindow", 10000l)//
						.put("timestamp", TimeUtils.getTimestamp(ZonedDateTime.now()) - 5000l)//
						.get()))
				.getJSONObject(0);
		double makerCommission = json.getDouble("makerCommission");
		double takerCommission = json.getDouble("takerCommission");
		return new double[] { makerCommission, takerCommission };
	}

	public static BaseBarSeries getBarSeries(String symbol, BarInterval interval) {
		return getBarSeries(symbol, interval, 1000);
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
