package kaba4cow.traderclient.ui.panels.graphs;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.ta4j.core.Bar;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.ui.UIColors;
import kaba4cow.traderclient.ui.panels.GraphPanel;
import kaba4cow.traderclient.ui.panels.graphs.indicators.GraphIndicator;
import kaba4cow.traderclient.ui.panels.graphs.indicators.GraphLine;
import kaba4cow.traderclient.utils.FormatUtils;
import kaba4cow.traderclient.utils.MathUtils;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private final GraphPanel graphPanel;
	protected final GraphView graphView;

	private final Set<GraphIndicator> indicators;
	private final Set<GraphLine> lines;

	private double minDataValue;
	private double maxDataValue;

	public Graph(GraphPanel graphPanel, GraphView graphView) {
		super();
		setBorder(BorderFactory.createLoweredBevelBorder());
		setLayout(null);
		setMinimumSize(new Dimension(0, 200));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		this.graphPanel = graphPanel;
		this.graphView = graphView;
		this.indicators = new LinkedHashSet<>();
		this.lines = new LinkedHashSet<>();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(UIColors.graphBackground);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		if (ApplicationSettings.getSeries().isEmpty())
			return;
		minDataValue = Double.POSITIVE_INFINITY;
		maxDataValue = Double.NEGATIVE_INFINITY;
		int minIndex = (int) Math.max(ApplicationSettings.getSeries().getBeginIndex(), graphView.getMinWorld());
		int maxIndex = (int) Math.min(ApplicationSettings.getSeries().getEndIndex(), graphView.getMaxWorld());
		for (int i = minIndex; i <= maxIndex; i++) {
			for (GraphIndicator indicator : indicators)
				if (i > indicator.getUnstableBars()) {
					double minValue = indicator.getMinValue(i);
					if (minValue < minDataValue)
						minDataValue = minValue;
					if (minValue > maxDataValue)
						maxDataValue = minValue;
					double maxValue = indicator.getMaxValue(i);
					if (maxValue < minDataValue)
						minDataValue = maxValue;
					if (maxValue > maxDataValue)
						maxDataValue = maxValue;
				}
			for (GraphLine line : lines) {
				double value = line.getValue();
				if (value < minDataValue)
					minDataValue = value;
				if (value > maxDataValue)
					maxDataValue = value;
			}
		}

		if (graphPanel.getCursorPoint() != null) {
			graphics.setColor(UIColors.graphLine);
			Point cursorPoint = graphPanel.getCursorPoint();
			graphics.drawLine(cursorPoint.x, 0, cursorPoint.x, getHeight());
		}

		for (GraphIndicator indicator : indicators) {
			int minIndicatorIndex = Math.max(minIndex, indicator.getUnstableBars());
			indicator.beginDraw(graphics, this);
			for (int i = minIndicatorIndex; i <= maxIndex; i++)
				indicator.drawIndex(graphics, this, i);
			indicator.endDraw(graphics, this);
		}
		for (GraphLine line : lines) {
			int y = toScreenY(line.getValue());
			graphics.setColor(line.getColor());
			graphics.drawLine(0, y, getWidth(), y);
		}

		graphics.setColor(UIColors.graphText);
		drawString(FormatUtils.number(maxDataValue, 8), graphics, 1d);
		drawString(FormatUtils.number(minDataValue, 8), graphics, 0d);
	}

	public void drawString(String string, Graphics2D graphics, double position) {
		FontMetrics metrics = graphics.getFontMetrics();
		int textWidth = metrics.stringWidth(string);
		int textHeight = metrics.getHeight();
		int x = getWidth() - textWidth;
		int y = (int) MathUtils.map(position, 1d, 0d, 0d, getHeight() - textHeight) + metrics.getAscent();
		graphics.drawString(string, x, y);
	}

	public int toScreenSize(double world) {
		return graphView.toScreenSize(world);
	}

	public int toScreenX(double world) {
		return graphView.toScreen(world);
	}

	public int toScreenY(double world) {
		return (int) MathUtils.map(world, minDataValue, maxDataValue, 0.99d * getHeight(), 0.01d * getHeight());
	}

	public double toWorldY(double screen) {
		return MathUtils.map(screen, getHeight(), 0d, minDataValue, maxDataValue);
	}

	public void addIndicator(GraphIndicator indicator) {
		indicators.add(indicator);
	}

	public void removeIndicator(GraphIndicator indicator) {
		indicators.remove(indicator);
	}

	public void clearIndicators() {
		indicators.clear();
	}

	public void addLine(GraphLine line) {
		lines.add(line);
	}

	public void removeLine(GraphLine line) {
		lines.remove(line);
	}

	public void clearLines() {
		lines.clear();
	}

	public Bar getBar(int index) {
		return ApplicationSettings.getSeries().getBar(index);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		graphView.mouseWheelMoved(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e))
			graphView.mouseDragged(e);
		graphPanel.setCursorPoint(e.getPoint());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e))
			graphView.mousePressed(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		graphPanel.setCursorPoint(e.getPoint());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		graphPanel.setCursorPoint(e.getPoint());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		graphPanel.setCursorPoint(null);
	}

}
