package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class RSI2StrategyBuilder extends StrategyBuilder {

	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 1, max = 100)
	private int longBars;

	public RSI2StrategyBuilder() {
		super("2-Period RSI");
		this.shortBars = 5;
		this.longBars = 100;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> closePrice = new ClosePriceIndicator(series);
		Indicator<Num> shortSma = new SMAIndicator(closePrice, shortBars);
		Indicator<Num> longSma = new SMAIndicator(closePrice, longBars);
		Indicator<Num> rsi = new RSIIndicator(closePrice, 1);

		Rule entryRule = new OverIndicatorRule(shortSma, longSma).and(new CrossedDownIndicatorRule(rsi, 5))
				.and(new OverIndicatorRule(shortSma, closePrice));
		Rule exitRule = new UnderIndicatorRule(shortSma, longSma).and(new CrossedUpIndicatorRule(rsi, 95))
				.and(new UnderIndicatorRule(shortSma, closePrice));

		return new BuilderStrategy(this, entryRule, exitRule, shortSma, longSma, rsi);
	}

}
