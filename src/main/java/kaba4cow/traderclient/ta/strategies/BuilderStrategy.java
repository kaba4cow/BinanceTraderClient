package kaba4cow.traderclient.ta.strategies;

import org.json.JSONObject;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;

public class BuilderStrategy extends BaseStrategy {

	private final JSONObject json;

	public BuilderStrategy(StrategyBuilder strategy, Rule entryRule, Rule exitRule, Indicator<?>... indicators) {
		super(strategy.getName(), entryRule, exitRule, getUnstableBars(indicators));
		this.json = strategy.serialize();
	}

	private static int getUnstableBars(Indicator<?>... indicators) {
		int max = 0;
		for (Indicator<?> indicator : indicators)
			max = Math.max(max, indicator.getUnstableBars());
		return max;
	}

	public JSONObject serialize() {
		return json;
	}

}
