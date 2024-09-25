package kaba4cow.traderclient.ui.panels.graphs.indicators;

import java.awt.Graphics2D;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import kaba4cow.traderclient.ui.UIColors;
import kaba4cow.traderclient.ui.panels.graphs.Graph;

public class PriceGraphIndicator implements GraphIndicator {

	private final BarSeries series;

	public PriceGraphIndicator(BarSeries series) {
		this.series = series;
	}

	@Override
	public void beginDraw(Graphics2D graphics, Graph graph) {
	}

	@Override
	public void drawIndex(Graphics2D graphics, Graph graph, int index) {
		Bar bar = series.getBar(index);
		double lowPrice = bar.getLowPrice().doubleValue();
		double highPrice = bar.getHighPrice().doubleValue();
		double openPrice = bar.getOpenPrice().doubleValue();
		double closePrice = bar.getClosePrice().doubleValue();
		int width = graph.toScreenSize(0.9d);
		graphics.setColor(bar.isBullish() ? UIColors.bullish : UIColors.bearish);
		{
			int minY = graph.toScreenY(lowPrice);
			int maxY = graph.toScreenY(highPrice);
			graphics.drawLine(graph.toScreenX(index) + width / 2, minY, graph.toScreenX(index) + width / 2, maxY);
		}
		{
			int minY = graph.toScreenY(Math.min(openPrice, closePrice));
			int maxY = graph.toScreenY(Math.max(openPrice, closePrice));
			graphics.fillRect(graph.toScreenX(index), minY, width, maxY - minY + 1);
		}
	}

	@Override
	public void endDraw(Graphics2D graphics, Graph graph) {
	}

	@Override
	public double getMinValue(int index) {
		return series.getBar(index).getLowPrice().doubleValue();
	}

	@Override
	public double getMaxValue(int index) {
		return series.getBar(index).getHighPrice().doubleValue();
	}

	@Override
	public int getUnstableBars() {
		return 0;
	}

}
