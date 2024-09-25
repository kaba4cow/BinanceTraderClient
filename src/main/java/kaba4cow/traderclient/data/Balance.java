package kaba4cow.traderclient.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import kaba4cow.traderclient.BinanceSpot;

public class Balance {

	private final Map<String, Asset> balance;

	public Balance(JSONArray json) {
		balance = new LinkedHashMap<>();
		for (int i = 0; i < json.length(); i++) {
			String name = json.getJSONObject(i).getString("asset");
			Asset asset = new Asset(json.getJSONObject(i));
			balance.put(name, asset);
		}
	}

	public Set<String> assets() {
		return balance.keySet();
	}

	public Asset asset(String asset) {
		if (!balance.containsKey(asset))
			return new Asset();
		return balance.get(asset);
	}

	public int assetCount() {
		return balance.size();
	}

	public double total() {
		double total = asset("USDT").total;
		for (String asset : balance.keySet())
			if (!asset.equals("USDT"))
				total += asset(asset).total * BinanceSpot.getPrice(asset + "USDT");
		return total;
	}

	public static class Asset {

		public final double free;
		public final double locked;
		public final double total;

		public Asset(JSONObject json) {
			this.free = json.getDouble("free");
			this.locked = json.getDouble("locked");
			this.total = free + locked;
		}

		public Asset() {
			this.free = 0d;
			this.locked = 0d;
			this.total = 0d;
		}

	}

}
