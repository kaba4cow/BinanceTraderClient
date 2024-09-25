package kaba4cow.traderclient.ta;

import org.ta4j.core.Bar;
import org.ta4j.core.num.Num;

public enum BarInfo {

	OPEN("Open") {
		@Override
		public Num get(Bar bar) {
			return bar.getOpenPrice();
		}
	}, //
	HIGH("High") {
		@Override
		public Num get(Bar bar) {
			return bar.getHighPrice();
		}
	}, //
	LOW("Low") {
		@Override
		public Num get(Bar bar) {
			return bar.getLowPrice();
		}
	}, //
	CLOSE("Close") {
		@Override
		public Num get(Bar bar) {
			return bar.getClosePrice();
		}
	};

	private final String string;

	private BarInfo(String string) {
		this.string = string;
	}

	public abstract Num get(Bar bar);

	@Override
	public String toString() {
		return string;
	}

}
