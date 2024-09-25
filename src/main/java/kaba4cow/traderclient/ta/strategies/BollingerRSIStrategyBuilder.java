package kaba4cow.traderclient.ta.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import kaba4cow.traderclient.ta.MovingAverage;
import kaba4cow.traderclient.ta.indicators.MAIndicator;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterEnum;
import kaba4cow.traderclient.ta.strategies.annotations.StrategyParameterInt;

public class BollingerRSIStrategyBuilder extends StrategyBuilder {

	@StrategyParameterEnum(name = "Moving Average", type = MovingAverage.class)
	private MovingAverage movingAverage;
	@StrategyParameterInt(name = "Bollinger Period", min = 1, max = 100)
	private int bollingerBars;
	@StrategyParameterInt(name = "RSI Period", min = 1, max = 100)
	private int rsiBars;
	@StrategyParameterInt(name = "Low Threshold", min = 1, max = 99)
	private int lowThreshold;
	@StrategyParameterInt(name = "High Threshold", min = 1, max = 99)
	private int highThreshold;

	public BollingerRSIStrategyBuilder() {
		super("Bollinger + RSI");
		this.movingAverage = MovingAverage.SMA;
		this.bollingerBars = 30;
		this.rsiBars = 15;
		this.lowThreshold = 30;
		this.highThreshold = 70;
	}

	@Override
	public BuilderStrategy build(BarSeries series) {
		Indicator<Num> close = new ClosePriceIndicator(series);
		Indicator<Num> low = new LowPriceIndicator(series);
		Indicator<Num> high = new HighPriceIndicator(series);

		Indicator<Num> closeMa = new MAIndicator(close, movingAverage, bollingerBars);
		BollingerBandsMiddleIndicator bollMid = new BollingerBandsMiddleIndicator(closeMa);
		Indicator<Num> deviation = new StandardDeviationIndicator(close, bollingerBars);
		Num deviationScale = DecimalNum.valueOf(2);
		Indicator<Num> bollUpper = new BollingerBandsUpperIndicator(bollMid, deviation, deviationScale);
		Indicator<Num> bollLower = new BollingerBandsLowerIndicator(bollMid, deviation, deviationScale);
		Indicator<Num> rsi = new RSIIndicator(close, rsiBars);

		Rule entryRule = new CrossedUpIndicatorRule(low, bollLower)//
				.and(new UnderIndicatorRule(rsi, lowThreshold));
		Rule exitRule = new CrossedUpIndicatorRule(high, bollUpper)//
				.and(new OverIndicatorRule(rsi, highThreshold))//
				.or(new CrossedDownIndicatorRule(low, bollLower));

		return new BuilderStrategy(this, entryRule, exitRule, closeMa, rsi);
	}

}
