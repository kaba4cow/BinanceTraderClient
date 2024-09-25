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

public class TrendFollowerStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Short MA", type = MovingAverage.class)
	private MovingAverage shortMa;
	@StrategyParameterInt(name = "Short Period", min = 1, max = 200)
	private int shortBars;
	@StrategyParameterEnum(name = "Long MA", type = MovingAverage.class)
	private MovingAverage longMa;
	@StrategyParameterInt(name = "Long Period", min = 100, max = 300)
	private int longBars;

	public TrendFollowerStrategyBuilder() {
		super("Trend Follower");
		this.shortMa = MovingAverage.SMA;
		this.shortBars = 100;
		this.longMa = MovingAverage.SMA;
		this.longBars = 200;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> shortTrend = new MAIndicator(close, shortMa, shortBars);
		Indicator<Num> longTrend = new MAIndicator(close, longMa, longBars);

		Rule entryRule = and(//
				new CrossedUpIndicatorRule(close, shortTrend), //
				new OverIndicatorRule(shortTrend, longTrend));
		Rule exitRule = new CrossedDownIndicatorRule(close, shortTrend);

		return new BuilderStrategy(this, entryRule, exitRule, shortTrend);
	}

}
