package kaba4cow.traderclient.data;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class Symbol {

	public final String symbol;
	public final String baseAsset;
	public final String quoteAsset;
	public final double minTradeQuantity;
	public final double maxTradeQuantity;
	public final HashSet<String> orderTypes;

	public Symbol(JSONObject json) {
		baseAsset = json.getString("baseAsset");
		quoteAsset = json.getString("quoteAsset");
		symbol = baseAsset + quoteAsset;
		minTradeQuantity = getMinTradeQuantity(json);
		maxTradeQuantity = getMaxTradeQuantity(json);
		JSONArray jsonOrderTypes = json.getJSONArray("orderTypes");
		orderTypes = new HashSet<>();
		for (int i = 0; i < jsonOrderTypes.length(); i++)
			orderTypes.add(jsonOrderTypes.getString(i));
	}

	private static double getMinTradeQuantity(JSONObject json) {
		JSONArray jsonFilters = json.getJSONArray("filters");
		for (int i = 0; i < jsonFilters.length(); i++) {
			JSONObject filter = jsonFilters.getJSONObject(i);
			if (filter.getString("filterType").equals("NOTIONAL"))
				return filter.getDouble("minNotional");
		}
		return 1d;
	}

	private static double getMaxTradeQuantity(JSONObject json) {
		JSONArray jsonFilters = json.getJSONArray("filters");
		for (int i = 0; i < jsonFilters.length(); i++) {
			JSONObject filter = jsonFilters.getJSONObject(i);
			if (filter.getString("filterType").equals("NOTIONAL"))
				return filter.getDouble("maxNotional");
		}
		return 1d;
	}

}
