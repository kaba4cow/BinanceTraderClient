package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class MovingMomentumStrategyBuilder extends StrategyBuilder {

	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 1, max = 100)
	private int longBars;
	@StrategyParameterInt(name = "SOK Period", min = 1, max = 100)
	private int sokBars;
	@StrategyParameterInt(name = "MACD Period", min = 1, max = 100)
	private int macdBars;

	public MovingMomentumStrategyBuilder() {
		super("Moving Momentum");
		this.shortBars = 10;
		this.longBars = 25;
		this.sokBars = 15;
		this.macdBars = 20;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> closePrice = new ClosePriceIndicator(series);
		Indicator<Num> shortEma = new EMAIndicator(closePrice, shortBars);
		Indicator<Num> longEma = new EMAIndicator(closePrice, longBars);
		Indicator<Num> sok = new StochasticOscillatorKIndicator(series, sokBars);
		Indicator<Num> macd = new MACDIndicator(closePrice, shortBars, longBars);
		Indicator<Num> macdEma = new EMAIndicator(macd, macdBars);

		Rule entryRule = new OverIndicatorRule(shortEma, longEma).and(new CrossedDownIndicatorRule(sok, 20))
				.and(new OverIndicatorRule(macd, macdEma));
		Rule exitRule = new UnderIndicatorRule(shortEma, longEma).and(new CrossedUpIndicatorRule(sok, 80))
				.and(new UnderIndicatorRule(macd, macdEma));

		return new BuilderStrategy(this, entryRule, exitRule, shortEma, longEma, sok, macd, macdEma);
	}

}
