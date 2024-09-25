package kaba4cow.traderclient.utils.filters;

import java.util.prefs.Preferences;

import kaba4cow.traderclient.data.Order;
import kaba4cow.traderclient.data.Order.OrderType;

public class OrderTypeFilter {

	private final Preferences preferences;

	public OrderTypeFilter(Preferences preferences) {
		this.preferences = preferences;
	}

	public OrderTypeFilter set(OrderType type, boolean accept) {
		preferences.putBoolean(type.toString(), accept);
		return this;
	}

	public boolean get(OrderType type) {
		return preferences.getBoolean(type.toString(), false);
	}

	public boolean accept(Order order) {
		return get(order.type);
	}

}
