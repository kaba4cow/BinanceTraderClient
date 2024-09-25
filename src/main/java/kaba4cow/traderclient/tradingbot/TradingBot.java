package kaba4cow.traderclient.tradingbot;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseTradingRecord;
import org.ta4j.core.Position;
import org.ta4j.core.Trade;
import org.ta4j.core.Trade.TradeType;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.DecimalNum;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import com.binance.connector.client.utils.websocketcallback.WebSocketMessageCallback;

import kaba4cow.traderclient.BinanceSpot;
import kaba4cow.traderclient.data.Order;
import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.ta.bars.StreamBar;
import kaba4cow.traderclient.ta.strategies.BuilderStrategy;
import kaba4cow.traderclient.ta.strategies.StrategyBuilder;
import kaba4cow.traderclient.utils.IOUtils;
import kaba4cow.traderclient.utils.TimeUtils;
import kaba4cow.traderclient.utils.TradingReport;

public class TradingBot implements WebSocketMessageCallback {

	private static final List<TradingBot> pool = new ArrayList<>();

	private final String baseAsset;
	private final String quoteAsset;
	private final BarInterval interval;
	private final double tradeQuantity;

	private final BuilderStrategy strategy;
	private final TradingRecord record;
	private final BarSeries series;

	private final ZonedDateTime createTime;
	private final List<Long> orders;

	private final WebSocketStreamClient client;
	private boolean active;

	public TradingBot(String baseAsset, String quoteAsset, BarInterval interval, double tradeQuantity,
			StrategyBuilder strategy) {
		this.createTime = ZonedDateTime.now();
		this.baseAsset = baseAsset;
		this.quoteAsset = quoteAsset;
		this.interval = interval;
		this.tradeQuantity = tradeQuantity;
		this.record = new BaseTradingRecord();
		this.series = BinanceSpot.getBarSeries(baseAsset + quoteAsset, interval);
		this.strategy = strategy.build(series);
		this.orders = new ArrayList<>();
		this.client = new WebSocketStreamClientImpl();
		this.active = false;
		pool.add(this);
		saveTradingBots();
	}

	public TradingBot(JSONObject json) {
		this.createTime = TimeUtils.getDateTime(json.getLong("time"));
		this.baseAsset = json.getString("base");
		this.quoteAsset = json.getString("quote");
		this.interval = BarInterval.get(json.getString("interval"));
		this.tradeQuantity = json.getDouble("quantity");
		this.record = new BaseTradingRecord();
		this.series = BinanceSpot.getBarSeries(baseAsset + quoteAsset, interval);
		this.strategy = StrategyBuilder.deserialize(json.getJSONObject("strategy")).build(series);
		this.orders = new ArrayList<>();
		this.client = new WebSocketStreamClientImpl();
		this.active = false;
		JSONArray jsonOrderHistory = json.getJSONArray("orders");
		for (int i = 0; i < jsonOrderHistory.length(); i++)
			orders.add(jsonOrderHistory.getLong(i));
		JSONArray jsonTradingRecord = json.getJSONArray("record");
		for (int i = 0; i < jsonTradingRecord.length(); i++) {
			JSONArray jsonTrade = jsonTradingRecord.getJSONArray(i);
			TradeType type = TradeType.valueOf(jsonTrade.getString(0));
			double price = jsonTrade.getDouble(1);
			double quantity = jsonTrade.getDouble(2);
			if (type == TradeType.BUY)
				record.enter(0, DecimalNum.valueOf(price), DecimalNum.valueOf(quantity));
			else if (type == TradeType.SELL)
				record.exit(0, DecimalNum.valueOf(price), DecimalNum.valueOf(quantity));
		}
		pool.add(this);
		if (json.getBoolean("active"))
			start();
	}

	public static void loadTradingBots() {
		JSONArray jsonArray = IOUtils.readJSONArray("bots.json");
		for (int i = 0; i < jsonArray.length(); i++)
			new TradingBot(jsonArray.getJSONObject(i));
		new Thread("Trading Bot Autosave Thread") {
			@Override
			public void run() {
				while (true)
					try {
						Thread.sleep(60000l);
						saveTradingBots();
					} catch (InterruptedException e) {
					}
			}
		}.start();
	}

	public static void saveTradingBots() {
		JSONArray jsonArray = new JSONArray();
		for (TradingBot bot : pool)
			jsonArray.put(bot.serialize());
		IOUtils.writeJSONArray(jsonArray, "bots.json");
	}

	public JSONObject serialize() {
		JSONObject json = new JSONObject();
		json.put("time", TimeUtils.getTimestamp(createTime));
		json.put("base", baseAsset);
		json.put("quote", quoteAsset);
		json.put("interval", interval.toString());
		json.put("quantity", tradeQuantity);
		json.put("strategy", strategy.serialize());
		json.put("active", active);
		JSONArray jsonOrders = new JSONArray();
		for (Long id : orders)
			jsonOrders.put(id);
		json.put("orders", jsonOrders);
		JSONArray jsonRecord = new JSONArray();
		List<Trade> trades = new ArrayList<>();
		for (Position position : record.getPositions()) {
			trades.add(position.getEntry());
			if (position.isClosed())
				trades.add(position.getExit());
		}
		for (Trade trade : trades) {
			JSONArray jsonTrade = new JSONArray();
			jsonTrade.put(trade.getType().toString());
			jsonTrade.put(trade.getNetPrice().doubleValue());
			jsonTrade.put(trade.getAmount().doubleValue());
			jsonRecord.put(jsonTrade);
		}
		json.put("record", jsonRecord);
		return json;
	}

	@Override
	public void onMessage(String data) {
		JSONObject json = new JSONObject(data);
		Bar newBar = new StreamBar(json, interval);
		Bar lastBar = series.getLastBar();
		if (newBar.getBeginTime().isAfter(lastBar.getBeginTime())) {
			series.addBar(newBar);
			int index = series.getEndIndex();
			if (strategy.shouldEnter(index)) {
				double price = BinanceSpot.getPrice(baseAsset + quoteAsset);
				if (record.enter(index, DecimalNum.valueOf(price), DecimalNum.valueOf(tradeQuantity)))
					placeOrder(TradeType.BUY);
			} else if (strategy.shouldExit(index)) {
				double price = BinanceSpot.getPrice(baseAsset + quoteAsset);
				if (record.exit(index, DecimalNum.valueOf(price), DecimalNum.valueOf(tradeQuantity)))
					placeOrder(TradeType.SELL);
			}
		}
	}

	private void placeOrder(TradeType side) {
		try {
			Order order = BinanceSpot.newMarketOrder(baseAsset + quoteAsset, side, tradeQuantity);
			if (order != null) {
				orders.add(order.id);
				System.out.println(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		if (active || !pool.contains(this))
			return;
		client.klineStream(baseAsset + quoteAsset, interval.toString(), this);
		active = true;
		System.out.println("Started " + this);
		saveTradingBots();
	}

	public void stop() {
		if (!active || !pool.contains(this))
			return;
		client.closeAllConnections();
		active = false;
		System.out.println("Stopped " + this);
		saveTradingBots();
	}

	public void remove() {
		if (!pool.contains(this))
			return;
		stop();
		pool.remove(this);
		System.out.println("Removed " + this);
		saveTradingBots();
	}

	public boolean isActive() {
		return active;
	}

	public TradingReport getTradingReport() {
		return new TradingReport(record, series);
	}

	public String getBaseAsset() {
		return baseAsset;
	}

	public String getQuoteAsset() {
		return quoteAsset;
	}

	public BarInterval getInterval() {
		return interval;
	}

	public double getTradeQuantity() {
		return tradeQuantity;
	}

	public BuilderStrategy getStrategy() {
		return strategy;
	}

	public List<Long> getOrderHistory() {
		return new ArrayList<>(orders);
	}

	public int getOrderCount() {
		return orders.size();
	}

	public ZonedDateTime getCreateTime() {
		return createTime;
	}

	public static List<TradingBot> getAllBots() {
		return new ArrayList<>(pool);
	}

	@Override
	public String toString() {
		return "TradingBot [symbol=" + baseAsset + quoteAsset + ", interval=" + interval + ", tradeQuantity="
				+ tradeQuantity + ", strategy=" + strategy.getName() + "]";
	}

}
