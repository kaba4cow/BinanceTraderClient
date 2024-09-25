package kaba4cow.traderclient.utils;

import java.util.List;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Position;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.criteria.pnl.ProfitLossPercentageCriterion;

public class TradingReport implements Comparable<TradingReport> {

	public final double profit;
	public final double density;
	public final double winrate;

	public TradingReport(TradingRecord record, BarSeries series) {
		this.profit = new ProfitLossPercentageCriterion().calculate(series, record).doubleValue() / 100d;
		this.density = (double) record.getPositionCount() / (double) series.getBarCount();
		if (density > 0d)
			this.winrate = (double) calculateWinPositions(record.getPositions()) / (double) record.getPositionCount();
		else
			this.winrate = 0d;
	}

	private static int calculateWinPositions(List<Position> positions) {
		int total = 0;
		for (Position position : positions)
			if (position.isClosed()
					&& position.getExit().getNetPrice().isGreaterThan(position.getEntry().getNetPrice()))
				total++;
		return total;
	}

	@Override
	public int compareTo(TradingReport o) {
		return Double.compare(o.profit, profit);
	}

	@Override
	public String toString() {
		return "TradingReport [profit=" + profit + ", density=" + density + ", winrate=" + winrate + "]";
	}

}
