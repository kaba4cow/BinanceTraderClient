package kaba4cow.traderclient.ta.indicators;

import java.util.Random;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;

public class RandomIndicator implements Indicator<Boolean> {

	private final BarSeries series;
	private final int chance;

	private final Random random;

	public RandomIndicator(BarSeries series, int chance) {
		this.series = series;
		this.chance = chance;
		this.random = new Random(series.getBarCount() ^ chance);
	}

	@Override
	public Boolean getValue(int index) {
		return random.nextInt(0, 100) <= chance;
	}

	@Override
	public int getUnstableBars() {
		return 0;
	}

	@Override
	public BarSeries getBarSeries() {
		return series;
	}

}
