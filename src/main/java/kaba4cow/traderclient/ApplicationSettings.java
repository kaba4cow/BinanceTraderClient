package kaba4cow.traderclient;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import kaba4cow.traderclient.listeners.GraphListener;
import kaba4cow.traderclient.listeners.OrderFilterListener;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.utils.filters.OrderStatusFilter;
import kaba4cow.traderclient.utils.filters.OrderTypeFilter;

public class ApplicationSettings {

	private static final String KEY_BASE_ASSET = "asset_base";
	private static final String KEY_QUOTE_ASSET = "asset_quote";
	private static final String KEY_INTERVAL = "interval";
	private static final String KEY_STATUS_FILTER = "filter_status";
	private static final String KEY_TYPE_FILTER = "filter_type";

	private static final Set<RefreshListener> refreshListeners = new HashSet<>();
	private static final Set<GraphListener> graphListeners = new HashSet<>();
	private static final Set<OrderFilterListener> orderFilterListeners = new HashSet<>();

	private static final Preferences preferences = Preferences.userNodeForPackage(Application.class);

	private static BarSeries series;
	private static OrderStatusFilter orderStatusFilter;
	private static OrderTypeFilter orderTypeFilter;

	private ApplicationSettings() {
	}

	public static void initialize() {
		orderStatusFilter = new OrderStatusFilter(preferences.node(KEY_STATUS_FILTER));
		orderTypeFilter = new OrderTypeFilter(preferences.node(KEY_TYPE_FILTER));
	}

	public static void setSeries(String baseAsset, String quoteAsset, BarInterval interval) {
		preferences.put(KEY_BASE_ASSET, baseAsset);
		preferences.put(KEY_QUOTE_ASSET, quoteAsset);
		preferences.put(KEY_INTERVAL, interval.toString());
		series = BinanceSpot.getBarSeries(baseAsset + quoteAsset, interval);
		for (GraphListener listener : graphListeners)
			listener.onGraphUpdated();
		for (RefreshListener listener : refreshListeners)
			listener.onRefresh();
	}

	public static void updateLastBar() {
		Bar bar = BinanceSpot.getLastBar(getSymbol(), getInterval());
		series.addBar(bar, !bar.getEndTime().isAfter(series.getLastBar().getEndTime()));
		for (RefreshListener listener : refreshListeners)
			listener.onRefresh();
	}

	public static void updateOrderFilter() {
		for (OrderFilterListener listener : orderFilterListeners)
			listener.onOrderFilterUpdated();
	}

	public static BarSeries getSeries() {
		return series;
	}

	public static String getBaseAsset() {
		return preferences.get(KEY_BASE_ASSET, "BTC");
	}

	public static String getQuoteAsset() {
		return preferences.get(KEY_QUOTE_ASSET, "USDT");
	}

	public static String getSymbol() {
		return getBaseAsset() + getQuoteAsset();
	}

	public static BarInterval getInterval() {
		return BarInterval.get(preferences.get(KEY_INTERVAL, BarInterval.MINUTE1.toString()));
	}

	public static OrderStatusFilter getOrderStatusFilter() {
		return orderStatusFilter;
	}

	public static OrderTypeFilter getOrderTypeFilter() {
		return orderTypeFilter;
	}

	public static void addRefreshListener(RefreshListener listener) {
		refreshListeners.add(listener);
	}

	public static void addGraphListener(GraphListener listener) {
		graphListeners.add(listener);
	}

	public static void addOrderFilterListener(OrderFilterListener listener) {
		orderFilterListeners.add(listener);
	}

}
