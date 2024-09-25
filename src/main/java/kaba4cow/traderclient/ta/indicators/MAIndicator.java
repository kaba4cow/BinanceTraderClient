package kaba4cow.traderclient.ta.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.HMAIndicator;
import org.ta4j.core.indicators.LWMAIndicator;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.WMAIndicator;
import org.ta4j.core.indicators.ZLEMAIndicator;
import org.ta4j.core.num.Num;

import kaba4cow.traderclient.ta.MovingAverage;

public class MAIndicator implements Indicator<Num> {

	private final Indicator<Num> indicator;

	public MAIndicator(Indicator<Num> source, MovingAverage type, int barCount) {
		this.indicator = createMAIndicator(source, type, barCount);
	}

	private static Indicator<Num> createMAIndicator(Indicator<Num> source, MovingAverage type, int barCount) {
		switch (type) {
		case SMA:
			return new SMAIndicator(source, barCount);
		case EMA:
			return new EMAIndicator(source, barCount);
		case HMA:
			return new HMAIndicator(source, barCount);
		case MMA:
			return new MMAIndicator(source, barCount);
		case LWMA:
			return new LWMAIndicator(source, barCount);
		case ZLEMA:
			return new ZLEMAIndicator(source, barCount);
		case WMA:
			return new WMAIndicator(source, barCount);
		}
		return new EMAIndicator(source, barCount);
	}

	@Override
	public Num getValue(int index) {
		return indicator.getValue(index);
	}

	@Override
	public int getUnstableBars() {
		return indicator.getUnstableBars();
	}

	@Override
	public BarSeries getBarSeries() {
		return indicator.getBarSeries();
	}

}
