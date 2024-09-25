package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class LoHiBounceStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Price MA", type = MovingAverage.class)
	private MovingAverage priceMa;
	@StrategyParameterInt(name = "Price Period", min = 1, max = 100)
	private int priceBars;

	@StrategyParameterEnum(name = "Trend MA", type = MovingAverage.class)
	private MovingAverage trendMa;
	@StrategyParameterInt(name = "Trend Period", min = 10, max = 150)
	private int trendBars;

	public LoHiBounceStrategyBuilder() {
		super("LoHi Bounce");
		this.priceMa = MovingAverage.EMA;
		this.priceBars = 20;
		this.trendMa = MovingAverage.SMA;
		this.trendBars = 50;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> low = new LowPriceIndicator(series);
		Indicator<Num> high = new HighPriceIndicator(series);

		Indicator<Num> lowMa = new MAIndicator(low, priceMa, priceBars);
		Indicator<Num> highMa = new MAIndicator(high, priceMa, priceBars);
		Indicator<Num> trend = new MAIndicator(high, trendMa, trendBars);

		Rule entryRule = new CrossedUpIndicatorRule(low, highMa)//
				.and(new OverIndicatorRule(low, lowMa))//
				.and(new OverIndicatorRule(low, trend));
		Rule exitRule = new CrossedDownIndicatorRule(low, highMa);

		return new BuilderStrategy(this, entryRule, exitRule, lowMa, highMa, trend);
	}

}
