package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class LoHiCrossoverStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Close MA", type = MovingAverage.class)
	private MovingAverage closeMovingAverage;
	@StrategyParameterInt(name = "Close Period", min = 1, max = 100)
	private int closeBars;

	@StrategyParameterEnum(name = "Low MA", type = MovingAverage.class)
	private MovingAverage lowMovingAverage;
	@StrategyParameterInt(name = "Low Period", min = 1, max = 100)
	private int lowBars;

	@StrategyParameterEnum(name = "High MA", type = MovingAverage.class)
	private MovingAverage highMovingAverage;
	@StrategyParameterInt(name = "High Period", min = 1, max = 100)
	private int highBars;

	public LoHiCrossoverStrategyBuilder() {
		super("LoHi Crossover");
		this.closeMovingAverage = MovingAverage.HMA;
		this.closeBars = 5;
		this.lowMovingAverage = MovingAverage.SMA;
		this.lowBars = 10;
		this.highMovingAverage = MovingAverage.EMA;
		this.highBars = 10;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> closeMa = new MAIndicator(new ClosePriceIndicator(series), closeMovingAverage, closeBars);
		Indicator<Num> lowMa = new MAIndicator(new LowPriceIndicator(series), lowMovingAverage, lowBars);
		Indicator<Num> highMa = new MAIndicator(new HighPriceIndicator(series), highMovingAverage, highBars);

		Rule entryRule = new CrossedUpIndicatorRule(closeMa, lowMa).or(new CrossedUpIndicatorRule(closeMa, highMa));
		Rule exitRule = new CrossedDownIndicatorRule(closeMa, highMa).or(new CrossedDownIndicatorRule(closeMa, lowMa));

		return new BuilderStrategy(this, entryRule, exitRule, closeMa, lowMa, highMa);
	}

}
