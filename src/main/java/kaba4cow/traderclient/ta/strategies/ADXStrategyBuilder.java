package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class ADXStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Moving Average", type = MovingAverage.class)
	private MovingAverage movingAverage;
	@StrategyParameterInt(name = "Close Period", min = 1, max = 100)
	private int closeBars;
	@StrategyParameterInt(name = "ADX Period", min = 1, max = 100)
	private int adxBars;
	@StrategyParameterInt(name = "Threshold", min = 1, max = 99)
	private int threshold;

	public ADXStrategyBuilder() {
		super("ADX");
		this.movingAverage = MovingAverage.SMA;
		this.closeBars = 50;
		this.adxBars = 10;
		this.threshold = 20;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> closeMa = new MAIndicator(close, movingAverage, closeBars);
		Indicator<Num> adx = new ADXIndicator(series, adxBars);
		Indicator<Num> plus = new PlusDIIndicator(series, adxBars);
		Indicator<Num> minus = new MinusDIIndicator(series, adxBars);

		Rule overThresholdRule = new OverIndicatorRule(adx, threshold);

		Rule plusCrossedUpMinus = new CrossedUpIndicatorRule(plus, minus);
		Rule plusCrossedDownMinus = new CrossedDownIndicatorRule(plus, minus);

		Rule closePriceOverMa = new OverIndicatorRule(close, closeMa);
		Rule closePriceUnderMa = new UnderIndicatorRule(close, closeMa);

		Rule entryRule = overThresholdRule.and(plusCrossedUpMinus).and(closePriceOverMa);
		Rule exitRule = overThresholdRule.and(plusCrossedDownMinus).and(closePriceUnderMa);

		return new BuilderStrategy(this, entryRule, exitRule, closeMa, adx);
	}

	public ADXStrategyBuilder setSmaBars(int smaBars) {
		this.closeBars = smaBars;
		return this;
	}

	public ADXStrategyBuilder setAdxBars(int adxBars) {
		this.adxBars = adxBars;
		return this;
	}

}
