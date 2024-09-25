package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class RSIThresholdStrategyBuilder extends StrategyBuilder {

	@StrategyParameterInt(name = "RSI Period", min = 1, max = 100)
	private int rsiBars;
	@StrategyParameterInt(name = "Low Threshold", min = 1, max = 99)
	private int lowThreshold;
	@StrategyParameterInt(name = "High Threshold", min = 1, max = 99)
	private int highThreshold;

	@StrategyParameterEnum(name = "Trend MA", type = MovingAverage.class)
	private MovingAverage trendMa;
	@StrategyParameterInt(name = "Trend Period", min = 50, max = 250)
	private int trendBars;

	public RSIThresholdStrategyBuilder() {
		super("RSI Threshold");
		this.rsiBars = 5;
		this.lowThreshold = 20;
		this.highThreshold = 80;
		this.trendMa = MovingAverage.SMA;
		this.trendBars = 100;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> rsi = new RSIIndicator(close, rsiBars);
		Indicator<Num> trend = new MAIndicator(close, trendMa, trendBars);

		Rule entryRule = and(//
				new CrossedDownIndicatorRule(rsi, lowThreshold), //
				new OverIndicatorRule(close, trend));
		Rule exitRule = new CrossedUpIndicatorRule(rsi, highThreshold);

		return new BuilderStrategy(this, entryRule, exitRule, rsi);
	}

}
