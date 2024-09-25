package kaba4cow.traderclient.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderBook {

	public final OrderBookEntry[] bids;
	public final OrderBookEntry[] asks;

	public final double minBidPrice;
	public final double maxBidPrice;
	public final double minAskPrice;
	public final double maxAskPrice;

	public final double totalBidVolume;
	public final double totalAskVolume;
	public final double totalVolume;

	public final double bidVolumePercent;
	public final double askVolumePercent;

	public OrderBook(JSONObject json) {
		double total;

		JSONArray bidsJson = json.getJSONArray("bids");
		bids = new OrderBookEntry[bidsJson.length()];
		total = 0d;
		for (int i = 0; i < bids.length; i++) {
			bids[i] = new OrderBookEntry(bidsJson.getJSONArray(i));
			total += bids[i].quantity;
		}
		totalBidVolume = total;
		minBidPrice = bids[0].price;
		maxBidPrice = bids[bids.length - 1].price;

		JSONArray asksJson = json.getJSONArray("asks");
		asks = new OrderBookEntry[asksJson.length()];
		total = 0d;
		for (int i = 0; i < asks.length; i++) {
			asks[i] = new OrderBookEntry(asksJson.getJSONArray(i));
			total += asks[i].quantity;
		}
		totalAskVolume = total;
		minAskPrice = asks[0].price;
		maxAskPrice = asks[asks.length - 1].price;

		totalVolume = totalBidVolume + totalAskVolume;
		bidVolumePercent = totalBidVolume / totalVolume;
		askVolumePercent = totalAskVolume / totalVolume;
	}

	public double calculateMidPrice() {
		return 0.5d * (asks[0].price + bids[0].price);
	}

	public static class OrderBookEntry {

		public final double price;
		public final double quantity;

		public OrderBookEntry(JSONArray json) {
			price = json.getDouble(0);
			quantity = json.getDouble(1);
		}

		public double getPrice() {
			return price;
		}

		public double getQuantity() {
			return quantity;
		}

	}

}
