package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class LevelBreakerStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Close MA", type = MovingAverage.class)
	private MovingAverage closeMa;
	@StrategyParameterInt(name = "Close MA Period", min = 1, max = 100)
	private int closeBars;

	@StrategyParameterEnum(name = "Low MA", type = MovingAverage.class)
	private MovingAverage lowMa;
	@StrategyParameterInt(name = "Low MA Period", min = 1, max = 100)
	private int lowMaBars;
	@StrategyParameterInt(name = "Low Period", min = 50, max = 250)
	private int lowBars;

	@StrategyParameterEnum(name = "High MA", type = MovingAverage.class)
	private MovingAverage highMa;
	@StrategyParameterInt(name = "High MA Period", min = 1, max = 100)
	private int highMaBars;
	@StrategyParameterInt(name = "High Period", min = 50, max = 250)
	private int highBars;

	public LevelBreakerStrategyBuilder() {
		super("Level Breaker");
		this.closeMa = MovingAverage.SMA;
		this.closeBars = 5;
		this.lowMa = MovingAverage.SMA;
		this.lowMaBars = 10;
		this.lowBars = 100;
		this.highMa = MovingAverage.SMA;
		this.highMaBars = 10;
		this.highBars = 100;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new MAIndicator(new ClosePriceIndicator(series), closeMa, closeBars);
		Indicator<Num> low = new MAIndicator(new LowestValueIndicator(new LowPriceIndicator(series), lowBars), lowMa,
				lowMaBars);
		Indicator<Num> high = new MAIndicator(new HighestValueIndicator(new HighPriceIndicator(series), highBars),
				highMa, highMaBars);

		Rule entryRule = new CrossedUpIndicatorRule(close, low);
		Rule exitRule = new CrossedDownIndicatorRule(close, high);

		return new BuilderStrategy(this, entryRule, exitRule, close, low, high);
	}

}
