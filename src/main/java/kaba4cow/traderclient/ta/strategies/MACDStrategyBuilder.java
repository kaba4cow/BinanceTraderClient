package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class MACDStrategyBuilder extends StrategyBuilder {

	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 1, max = 100)
	private int longBars;

	public MACDStrategyBuilder() {
		super("MACD");
		this.shortBars = 5;
		this.longBars = 25;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> macd = new MACDIndicator(new ClosePriceIndicator(series), shortBars, longBars);

		Rule entryRule = new CrossedUpIndicatorRule(macd, 0d);
		Rule exitRule = new CrossedDownIndicatorRule(macd, 0d);

		return new BuilderStrategy(this, entryRule, exitRule, macd);
	}

}
