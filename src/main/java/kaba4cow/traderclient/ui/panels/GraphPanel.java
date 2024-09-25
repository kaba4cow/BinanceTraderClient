package kaba4cow.traderclient.ui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.listeners.GraphListener;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.ta.indicators.CummulativeDeltaIndicator;
import kaba4cow.traderclient.ui.panels.graphs.Graph;
import kaba4cow.traderclient.ui.panels.graphs.GraphPopupMenu;
import kaba4cow.traderclient.ui.panels.graphs.GraphView;
import kaba4cow.traderclient.ui.panels.graphs.indicators.CurveGraphIndicator;
import kaba4cow.traderclient.ui.panels.graphs.indicators.GraphLine;
import kaba4cow.traderclient.ui.panels.graphs.indicators.PriceGraphIndicator;

public class GraphPanel extends JPanel implements RefreshListener, GraphListener {

	private static final long serialVersionUID = 1L;

	private final GraphView graphView;
	private final GraphPopupMenu popupMenu;

	private Graph graph1;
	private Graph graph2;

	private Point cursorPoint;

	public GraphPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Graph"));
		setLayout(new GridLayout(0, 1, 0, 2));

		graphView = new GraphView(this);
		graphView.setMinZoom(2d);
		graphView.setMaxZoom(25d);
		graphView.setZoom(10d);

		popupMenu = new GraphPopupMenu(this);

		addGraph(graph1 = new Graph(this, graphView));
		addGraph(graph2 = new Graph(this, graphView));

		cursorPoint = null;

		ApplicationSettings.addRefreshListener(this);
		ApplicationSettings.addGraphListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		BarSeries series = ApplicationSettings.getSeries();
		Indicator<Num> close = new EMAIndicator(new ClosePriceIndicator(series), 5);
		Indicator<Num> delta1 = new CummulativeDeltaIndicator(close, 20);
		Indicator<Num> delta2 = new CummulativeDeltaIndicator(close, 50);
//		Indicator<Num> delta3 = new CummulativeDeltaIndicator(close, 100);
		{
//			Indicator<Num> close = new ClosePriceIndicator(series);
//			Indicator<Num> closeMa = new MAIndicator(close, MovingAverage.SIMPLE, 30);
//			BollingerBandsMiddleIndicator bollMid = new BollingerBandsMiddleIndicator(closeMa);
//			Indicator<Num> deviation = new StandardDeviationIndicator(close, 30);
//			Num deviationScale = DecimalNum.valueOf(2);
//			Indicator<Num> bollUpper = new BollingerBandsUpperIndicator(bollMid, deviation, deviationScale);
//			Indicator<Num> bollLower = new BollingerBandsLowerIndicator(bollMid, deviation, deviationScale);
//			graph1.clearIndicators();
//			graph1.addIndicator(new PriceGraphIndicator(series));
//			graph1.addIndicator(new CurveGraphIndicator(bollMid, Color.magenta));
//			graph1.addIndicator(new CurveGraphIndicator(bollUpper, Color.orange));
//			graph1.addIndicator(new CurveGraphIndicator(bollLower, Color.cyan));

			graph1.clearIndicators();
			graph1.clearLines();
			graph1.addIndicator(new PriceGraphIndicator(series));
		}
		{
			graph2.clearIndicators();
			graph2.addIndicator(new CurveGraphIndicator(delta1, Color.orange));
			graph2.addIndicator(new CurveGraphIndicator(delta2, Color.cyan));
//			graph2.addIndicator(new CurveGraphIndicator(delta3, Color.magenta));
			graph2.clearLines();
			graph2.addLine(new GraphLine(0d, Color.gray));
		}

		updateScroll();
		super.paintComponent(g);
	}

	public void addGraph(Graph graph) {
		graph.addMouseListener(popupMenu);
		add(graph);
		doLayout();
	}

	public void removeGraph(Graph graph) {
		if (getComponentCount() > 1) {
			remove(graph);
			doLayout();
		}
	}

	@Override
	public void onRefresh() {
		if (graphView.getScroll() >= graphView.getMaxScroll())
			graphView.setScroll(graphView.getMaxScroll() + 1);
		repaint();
	}

	@Override
	public void onGraphUpdated() {
		updateScroll();
		graphView.setScroll(graphView.getMaxScroll());
		repaint();
	}

	private void updateScroll() {
		graphView.setMinScroll(
				ApplicationSettings.getSeries().getBeginIndex() - graphView.toWorldSize(0.5d * getWidth()));
		graphView.setMaxScroll(
				ApplicationSettings.getSeries().getEndIndex() + 1 - graphView.toWorldSize(0.5d * getWidth()));
		graphView.clampScroll();
	}

	public Point getCursorPoint() {
		return cursorPoint;
	}

	public void setCursorPoint(Point cursorPoint) {
		if (cursorPoint != null) {
			int index = (int) graphView.toWorld(cursorPoint.x);
			cursorPoint.x = graphView.toScreen(index + 0.5d);
		}
		this.cursorPoint = cursorPoint;
		repaint();
	}

}