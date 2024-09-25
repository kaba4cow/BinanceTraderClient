package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class DeltaPercentageIndicator implements Indicator<Num> {

	private final Indicator<Num> indicator;

	public DeltaPercentageIndicator(Indicator<Num> indicator) {
		this.indicator = indicator;
	}

	@Override
	public Num getValue(int index) {
		if (index == 0)
			return indicator.zero();
		Num prev = indicator.getValue(index - 1);
		if (prev.isZero())
			return prev.zero();
		Num velocity = indicator.getValue(index).minus(prev);
		return velocity.dividedBy(prev);
	}

	@Override
	public int getUnstableBars() {
		return 1;
	}

	@Override
	public BarSeries getBarSeries() {
		return indicator.getBarSeries();
	}

}
