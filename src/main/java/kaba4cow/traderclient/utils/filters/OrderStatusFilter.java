package kaba4cow.traderclient.utils.filters;

import java.util.prefs.Preferences;

import kaba4cow.traderclient.data.Order;
import kaba4cow.traderclient.data.Order.OrderStatus;

public class OrderStatusFilter {

	private final Preferences preferences;

	public OrderStatusFilter(Preferences preferences) {
		this.preferences = preferences;
	}

	public OrderStatusFilter set(OrderStatus status, boolean accept) {
		preferences.putBoolean(status.toString(), accept);
		return this;
	}

	public boolean get(OrderStatus status) {
		return preferences.getBoolean(status.toString(), false);
	}

	public boolean accept(Order order) {
		return get(order.status);
	}

}
