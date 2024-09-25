package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.BooleanIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.DownWilliamsFractalsIndicator;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.indicators.UpWilliamsFractalsIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class WilliamsFractalsStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Moving Average", type = MovingAverage.class)
	private MovingAverage movingAverage;
	@StrategyParameterInt(name = "Close Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "ADX Period", min = 50, max = 150)
	private int mediumBars;
	@StrategyParameterInt(name = "ADX Period", min = 100, max = 200)
	private int longBars;

	public WilliamsFractalsStrategyBuilder() {
		super("Williams Fractals");
		this.movingAverage = MovingAverage.SMA;
		this.shortBars = 20;
		this.mediumBars = 50;
		this.longBars = 100;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);

		Indicator<Num> shortMa = new MAIndicator(close, movingAverage, shortBars);
		Indicator<Num> mediumMa = new MAIndicator(close, movingAverage, mediumBars);
		Indicator<Num> longMa = new MAIndicator(close, movingAverage, longBars);

		Indicator<Boolean> up = new UpWilliamsFractalsIndicator(series, 5);
		Indicator<Boolean> down = new DownWilliamsFractalsIndicator(series, 5);

		Rule upTrendRule = and(new OverIndicatorRule(shortMa, mediumMa), new OverIndicatorRule(mediumMa, longMa));

		Rule entryRule = new BooleanIndicatorRule(up).and(upTrendRule);
		Rule exitRule = new BooleanIndicatorRule(down);

		return new BuilderStrategy(this, entryRule, exitRule, shortMa, mediumMa, longMa, up, down);
	}

}
