package kaba4cow.traderclient.data;

import java.time.ZonedDateTime;

import org.json.JSONObject;
import org.ta4j.core.Trade.TradeType;

import kaba4cow.traderclient.utils.TimeUtils;

public class Order {

	public final String symbol;
	public final long id;

	public final OrderStatus status;
	public final OrderType type;
	public final TradeType side;

	public final double originalQuantity;
	public final double executedQuantity;
	public final double cumulativeQuoteQuantity;

	public final double price;

	public final ZonedDateTime time;

	public Order(JSONObject json) {
		symbol = json.getString("symbol");
		id = json.getLong("orderId");

		status = OrderStatus.valueOf(json.getString("status"));
		type = OrderType.valueOf(json.getString("type"));
		side = TradeType.valueOf(json.getString("side"));

		originalQuantity = json.getDouble("origQty");
		executedQuantity = json.getDouble("executedQty");
		cumulativeQuoteQuantity = json.getDouble("cummulativeQuoteQty");

		if (type == OrderType.MARKET && executedQuantity > 0d)
			price = cumulativeQuoteQuantity / executedQuantity;
		else
			price = json.getDouble("price");

		long timestamp;
		if (json.has("time"))
			timestamp = json.getLong("time");
		else if (json.has("transactTime"))
			timestamp = json.getLong("transactTime");
		else if (json.has("updateTime"))
			timestamp = json.getLong("updateTime");
		else
			timestamp = TimeUtils.getTimestamp(ZonedDateTime.now());
		time = TimeUtils.getDateTime(timestamp);
	}

	@Override
	public String toString() {
		return "Order [symbol=" + symbol + ", id=" + id + ", status=" + status + ", type=" + type + ", side=" + side
				+ ", originalQuantity=" + originalQuantity + ", executedQuantity=" + executedQuantity
				+ ", cumulativeQuoteQuantity=" + cumulativeQuoteQuantity + ", price=" + price + ", time=" + time + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cumulativeQuoteQuantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(executedQuantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		temp = Double.doubleToLongBits(originalQuantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((side == null) ? 0 : side.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (Double.doubleToLongBits(cumulativeQuoteQuantity) != Double.doubleToLongBits(other.cumulativeQuoteQuantity))
			return false;
		if (Double.doubleToLongBits(executedQuantity) != Double.doubleToLongBits(other.executedQuantity))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(originalQuantity) != Double.doubleToLongBits(other.originalQuantity))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (side != other.side)
			return false;
		if (status != other.status)
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public static enum OrderType {

		LIMIT("Limit"), //
		MARKET("Market"), //
		STOP_LOSS("Stop-Loss"), //
		STOP_LOSS_LIMIT("Stop-Loss Limit"), //
		TAKE_PROFIT("Take-Profit"), //
		TAKE_PROFIT_LIMIT(("Take-Profit Limit")), //
		LIMIT_MAKER("Limit Maker");

		private final String name;

		private OrderType(String name) {
			this.name = name;
		}

		public static OrderType get(String name) {
			for (OrderType type : values())
				if (type.name.equals(name))
					return type;
			return null;
		}

		public String getName() {
			return name;
		}

	}

	public static enum OrderStatus {

		NEW("New"), //
		PARTIALLY_FILLED("Partially Filled"), //
		FILLED("Filled"), //
		CANCELED("Canceled"), //
		PENDING_CANCEL("Pending Cancel"), //
		REJECTED("Rejected"), //
		EXPIRED("Expired"), //
		EXPIRED_IN_MATCH("Expired in match");

		private final String name;

		private OrderStatus(String name) {
			this.name = name;
		}

		public static OrderStatus get(String name) {
			for (OrderStatus status : values())
				if (status.name.equals(name))
					return status;
			return null;
		}

		public String getName() {
			return name;
		}

	}

}
