package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class MAMultiCrossoverStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Short MA", type = MovingAverage.class)
	private MovingAverage shortMovingAverage;
	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;

	@StrategyParameterEnum(name = "Long MA", type = MovingAverage.class)
	private MovingAverage mediumMovingAverage;
	@StrategyParameterInt(name = "Long Period", min = 10, max = 150)
	private int mediumBars;

	@StrategyParameterEnum(name = "Trend MA", type = MovingAverage.class)
	private MovingAverage longMovingAverage;
	@StrategyParameterInt(name = "Trend Period", min = 50, max = 250)
	private int longBars;

	public MAMultiCrossoverStrategyBuilder() {
		super("MA Multi Crossover");
		this.shortMovingAverage = MovingAverage.SMA;
		this.shortBars = 20;
		this.mediumMovingAverage = MovingAverage.SMA;
		this.mediumBars = 50;
		this.longMovingAverage = MovingAverage.SMA;
		this.longBars = 200;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> shortMa = new MAIndicator(close, shortMovingAverage, shortBars);
		Indicator<Num> mediumMa = new MAIndicator(close, mediumMovingAverage, mediumBars);
		Indicator<Num> longMa = new MAIndicator(close, longMovingAverage, longBars);

		Rule entryRule = or(//
				new CrossedUpIndicatorRule(shortMa, mediumMa), //
				new CrossedUpIndicatorRule(shortMa, longMa) //
		);
		Rule exitRule = or(//
				new CrossedDownIndicatorRule(shortMa, mediumMa), //
				new CrossedDownIndicatorRule(shortMa, longMa) //
		);

		return new BuilderStrategy(this, entryRule, exitRule, shortMa, mediumMa, longMa);
	}

}
