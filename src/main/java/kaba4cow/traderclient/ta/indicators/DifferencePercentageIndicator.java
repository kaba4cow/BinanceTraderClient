package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class DifferencePercentageIndicator implements Indicator<Num> {

	private final Indicator<Num> prev;
	private final Indicator<Num> next;

	public DifferencePercentageIndicator(Indicator<Num> prev, Indicator<Num> next) {
		this.prev = prev;
		this.next = next;
	}

	@Override
	public Num getValue(int index) {
		Num prevValue = prev.getValue(index);
		Num nextValue = next.getValue(index);
		return nextValue.minus(prevValue).dividedBy(prevValue);
	}

	@Override
	public int getUnstableBars() {
		return Math.max(prev.getUnstableBars(), next.getUnstableBars());
	}

	@Override
	public BarSeries getBarSeries() {
		return prev.getBarSeries();
	}

}
