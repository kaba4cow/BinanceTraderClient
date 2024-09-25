package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class CummulativeDeltaIndicator implements Indicator<Num> {

	private final Indicator<Num> indicator;
	private final int barCount;

	public CummulativeDeltaIndicator(Indicator<Num> indicator, int barCount) {
		this.indicator = new DeltaIndicator(indicator);
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		Num cummulativeDelta = indicator.zero();
		int beginIndex = index - barCount + 1;
		int endIndex = index;
		for (int i = beginIndex; i <= endIndex; i++) {
			Num delta = indicator.getValue(i);
			cummulativeDelta = cummulativeDelta.plus(delta);
		}
		return cummulativeDelta;
	}

	@Override
	public int getUnstableBars() {
		return Math.max(barCount, indicator.getUnstableBars());
	}

	@Override
	public BarSeries getBarSeries() {
		return indicator.getBarSeries();
	}

}
