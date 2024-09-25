package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.CCIIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class CCICorrectionStrategyBuilder extends StrategyBuilder {

	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 1, max = 100)
	private int longBars;

	public CCICorrectionStrategyBuilder() {
		super("CCI Correction");
		this.shortBars = 5;
		this.longBars = 50;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> shortCCI = new CCIIndicator(series, shortBars);
		Indicator<Num> longCCI = new CCIIndicator(series, longBars);

		Num plus100 = series.hundred();
		Num minus100 = series.numOf(-100);

		Rule entryRule = new OverIndicatorRule(longCCI, plus100).and(new UnderIndicatorRule(shortCCI, minus100));
		Rule exitRule = new UnderIndicatorRule(longCCI, minus100).and(new OverIndicatorRule(shortCCI, plus100));

		return new BuilderStrategy(this, entryRule, exitRule, shortCCI, longCCI);
	}

}
