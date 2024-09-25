package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.CummulativeDeltaIndicator;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class DeltaCrossoverStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Price MA", type = MovingAverage.class)
	private MovingAverage movingAverage;
	@StrategyParameterInt(name = "Price Period", min = 1, max = 100)
	private int priceBars;

	@StrategyParameterInt(name = "Short Period", min = 10, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 10, max = 100)
	private int longBars;

	public DeltaCrossoverStrategyBuilder() {
		super("Delta Crossover");
		this.movingAverage = MovingAverage.SMA;
		this.priceBars = 5;
		this.shortBars = 20;
		this.longBars = 50;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> priceMa = new MAIndicator(close, movingAverage, priceBars);
		Indicator<Num> shortDelta = new CummulativeDeltaIndicator(priceMa, shortBars);
		Indicator<Num> longDelta = new CummulativeDeltaIndicator(priceMa, longBars);

		Rule entryRule = new CrossedDownIndicatorRule(shortDelta, longDelta);
		Rule exitRule = new CrossedUpIndicatorRule(shortDelta, longDelta);

		return new BuilderStrategy(this, entryRule, exitRule, priceMa, shortDelta, longDelta);
	}

}
