package kaba4cow.traderclient.ui.panels.graphs.indicators;

import java.awt.Graphics2D;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import kaba4cow.traderclient.ui.UIColors;
import kaba4cow.traderclient.ui.panels.graphs.Graph;

public class VolumeGraphIndicator implements GraphIndicator {

	private final BarSeries series;

	public VolumeGraphIndicator(BarSeries series) {
		this.series = series;
	}

	@Override
	public void beginDraw(Graphics2D graphics, Graph graph) {
	}

	@Override
	public void drawIndex(Graphics2D graphics, Graph graph, int index) {
		Bar bar = series.getBar(index);
		int height = graph.toScreenY(bar.getVolume().doubleValue());
		graphics.setColor(bar.isBullish() ? UIColors.bullish : UIColors.bearish);
		graphics.fillRect(graph.toScreenX(index), graph.getHeight(), graph.toScreenSize(0.9d),
				-graph.getHeight() + height);
	}

	@Override
	public void endDraw(Graphics2D graphics, Graph graph) {
	}

	@Override
	public double getMinValue(int index) {
		return 0d;
	}

	@Override
	public double getMaxValue(int index) {
		return series.getBar(index).getVolume().doubleValue();
	}

	@Override
	public int getUnstableBars() {
		return 0;
	}

}
