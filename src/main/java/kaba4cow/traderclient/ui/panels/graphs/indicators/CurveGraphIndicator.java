package kaba4cow.traderclient.ui.panels.graphs.indicators;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import kaba4cow.traderclient.ui.panels.graphs.Graph;

public class CurveGraphIndicator implements GraphIndicator {

	private final Indicator<Num> indicator;

	private final Color color;

	private Path2D path;

	public CurveGraphIndicator(Indicator<Num> indicator, Color color) {
		this.indicator = indicator;
		this.color = color;
		this.path = null;
	}

	@Override
	public void beginDraw(Graphics2D graphics, Graph graph) {
		path = null;
	}

	@Override
	public void drawIndex(Graphics2D graphics, Graph graph, int index) {
		double x = graph.toScreenX(index + 0.5d);
		double y = graph.toScreenY(indicator.getValue(index).doubleValue());
		if (path == null) {
			path = new Path2D.Double();
			path.moveTo(x, y);
		} else
			path.lineTo(x, y);
	}

	@Override
	public void endDraw(Graphics2D graphics, Graph graph) {
		if (path == null)
			return;
		graphics.setColor(color);
		graphics.draw(path);
		path = null;
	}

	@Override
	public double getMinValue(int index) {
		return indicator.getValue(index).doubleValue();
	}

	@Override
	public double getMaxValue(int index) {
		return indicator.getValue(index).doubleValue();
	}

	@Override
	public int getUnstableBars() {
		return indicator.getUnstableBars();
	}

}
