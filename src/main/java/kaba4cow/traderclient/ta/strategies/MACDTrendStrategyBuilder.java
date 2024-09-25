package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class MACDTrendStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "MACD MA", type = MovingAverage.class)
	private MovingAverage macdMa;
	@StrategyParameterInt(name = "Short Period", min = 1, max = 100)
	private int shortBars;
	@StrategyParameterInt(name = "Long Period", min = 1, max = 100)
	private int longBars;
	@StrategyParameterInt(name = "MACD Period", min = 1, max = 100)
	private int macdBars;
	@StrategyParameterEnum(name = "Trend MA", type = MovingAverage.class)
	private MovingAverage trendMa;
	@StrategyParameterInt(name = "Trend Period", min = 50, max = 250)
	private int trendBars;

	public MACDTrendStrategyBuilder() {
		super("MACD Trend");
		this.macdMa = MovingAverage.SMA;
		this.shortBars = 12;
		this.longBars = 26;
		this.macdBars = 9;
		this.trendMa = MovingAverage.SMA;
		this.trendBars = 100;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> macd = new MACDIndicator(close, shortBars, longBars);
		Indicator<Num> ma = new MAIndicator(macd, macdMa, macdBars);
		Indicator<Num> trend = new MAIndicator(close, trendMa, trendBars);

		Rule entryRule = new CrossedUpIndicatorRule(macd, ma).and(new UnderIndicatorRule(macd, 0d))
				.and(new OverIndicatorRule(close, trend));
		Rule exitRule = new CrossedDownIndicatorRule(macd, ma).and(new OverIndicatorRule(macd, 0d));

		return new BuilderStrategy(this, entryRule, exitRule, macd, ma, trend);
	}

}
