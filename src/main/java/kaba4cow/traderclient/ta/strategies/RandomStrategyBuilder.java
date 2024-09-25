package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.BooleanIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.indicators.RandomIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class RandomStrategyBuilder extends StrategyBuilder {

	@StrategyParameterInt(name = "Entry Change", min = 1, max = 99)
	private int entryChance;
	@StrategyParameterInt(name = "Exit Change", min = 1, max = 99)
	private int exitChance;
	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 1, max = 100)
	private int longBars;

	public RandomStrategyBuilder() {
		super("Random");
		this.entryChance = 50;
		this.exitChance = 50;
		this.shortBars = 20;
		this.longBars = 50;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> shortTrend = new EMAIndicator(close, shortBars);
		Indicator<Num> longTrend = new EMAIndicator(close, longBars);
		Indicator<Boolean> entry = new RandomIndicator(series, entryChance);
		Indicator<Boolean> exit = new RandomIndicator(series, exitChance);

		Rule entryRule = new BooleanIndicatorRule(entry).and(new OverIndicatorRule(shortTrend, longTrend));
		Rule exitRule = new BooleanIndicatorRule(exit).or(new UnderIndicatorRule(shortTrend, longTrend));

		return new BuilderStrategy(this, entryRule, exitRule, shortTrend, longTrend);
	}

}
