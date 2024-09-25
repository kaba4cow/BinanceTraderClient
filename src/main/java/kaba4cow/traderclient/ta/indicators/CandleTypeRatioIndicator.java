package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class CandleTypeRatioIndicator implements Indicator<Num> {

	private final BarSeries series;
	private final int barCount;

	public CandleTypeRatioIndicator(BarSeries series, int barCount) {
		this.series = series;
		this.barCount = barCount;
	}

	@Override
	public Num getValue(int index) {
		int beginIndex = index - barCount;
		int endIndex = index;
		Num bullCount = series.zero();
		Num bearCount = series.zero();
		for (int i = beginIndex; i <= endIndex; i++) {
			Bar bar = series.getBar(i);
			if (bar.isBullish())
				bullCount = bullCount.plus(series.one());
			if (bar.isBearish())
				bearCount = bearCount.plus(series.one());
		}
		return bullCount.minus(bearCount).dividedBy(bearCount);
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}

	@Override
	public BarSeries getBarSeries() {
		return series;
	}

}
