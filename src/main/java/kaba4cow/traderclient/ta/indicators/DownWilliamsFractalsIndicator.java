package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class DownWilliamsFractalsIndicator extends CachedIndicator<Boolean> {

	private final int barCount;

	public DownWilliamsFractalsIndicator(BarSeries series, int barCount) {
		super(series);
		this.barCount = barCount;
	}

	@Override
	protected Boolean calculate(int index) {
		if (index < barCount || index >= getBarSeries().getBarCount() - barCount)
			return false;
		Num middleLow = getBarSeries().getBar(index).getLowPrice();
		for (int i = 1; i <= barCount; i++)
			if (getBarSeries().getBar(index - i).getLowPrice().isLessThan(middleLow)
					|| getBarSeries().getBar(index + i).getLowPrice().isLessThan(middleLow))
				return false;
		return true;
	}

	@Override
	public int getUnstableBars() {
		return barCount;
	}

}
