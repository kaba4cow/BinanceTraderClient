package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class MACrossoverStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Short MA", type = MovingAverage.class)
	private MovingAverage shortMovingAverage;
	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;

	@StrategyParameterEnum(name = "Long MA", type = MovingAverage.class)
	private MovingAverage longMovingAverage;
	@StrategyParameterInt(name = "Long Period", min = 10, max = 150)
	private int longBars;

	@StrategyParameterEnum(name = "Trend MA", type = MovingAverage.class)
	private MovingAverage trendMovingAverage;
	@StrategyParameterInt(name = "Trend Period", min = 50, max = 250)
	private int trendBars;

	public MACrossoverStrategyBuilder() {
		super("MA Crossover");
		this.shortMovingAverage = MovingAverage.SMA;
		this.shortBars = 20;
		this.longMovingAverage = MovingAverage.SMA;
		this.longBars = 50;
		this.trendMovingAverage = MovingAverage.SMA;
		this.trendBars = 200;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> shortMa = new MAIndicator(close, shortMovingAverage, shortBars);
		Indicator<Num> longMa = new MAIndicator(close, longMovingAverage, longBars);
		Indicator<Num> trendMa = new MAIndicator(close, trendMovingAverage, trendBars);

		Rule entryRule = new CrossedUpIndicatorRule(shortMa, longMa).and(new OverIndicatorRule(longMa, trendMa));
		Rule exitRule = new CrossedDownIndicatorRule(shortMa, longMa);

		return new BuilderStrategy(this, entryRule, exitRule, shortMa, longMa, trendMa);
	}

}
