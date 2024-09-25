package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class DeltaIndicator implements Indicator<Num> {

	private final Indicator<Num> indicator;

	public DeltaIndicator(Indicator<Num> indicator) {
		this.indicator = indicator;
	}

	@Override
	public Num getValue(int index) {
		return indicator.getValue(index).minus(indicator.getValue(index - 1));
	}

	@Override
	public int getUnstableBars() {
		return 1 + indicator.getUnstableBars();
	}

	@Override
	public BarSeries getBarSeries() {
		return indicator.getBarSeries();
	}

}
