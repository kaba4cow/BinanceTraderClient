package kaba4cow.traderclient.ui.panels.graphs.indicators;

import java.awt.Graphics2D;

import kaba4cow.traderclient.ui.panels.graphs.Graph;

public interface GraphIndicator {

	public void beginDraw(Graphics2D graphics, Graph graph);

	public void drawIndex(Graphics2D graphics, Graph graph, int index);

	public void endDraw(Graphics2D graphics, Graph graph);

	public double getMinValue(int index);

	public double getMaxValue(int index);

	public int getUnstableBars();

}
