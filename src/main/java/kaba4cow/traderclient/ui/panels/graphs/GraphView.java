package kaba4cow.traderclient.ui.panels.graphs;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import kaba4cow.traderclient.ui.panels.GraphPanel;
import kaba4cow.traderclient.utils.MathUtils;

public class GraphView {

	private double scrollLast;
	private double scroll;
	private double minScroll;
	private double maxScroll;

	private double zoom;
	private double minZoom;
	private double maxZoom;

	private final GraphPanel panel;

	public GraphView(GraphPanel panel) {
		this.panel = panel;
		this.scrollLast = 0d;
		this.scroll = 0d;
		this.zoom = 1d;
	}

	public double getMinWorld() {
		return toWorld(-toScreenSize(1d));
	}

	public double getMaxWorld() {
		return toWorld(panel.getWidth());
	}

	public int toScreenSize(double world) {
		return (int) Math.max(1, zoom * world);
	}

	public double toWorldSize(double screen) {
		return screen / zoom;
	}

	public int toScreen(double world) {
		return (int) (zoom * (world - scroll));
	}

	public double toWorld(double screen) {
		return screen / zoom + scroll;
	}

	public void clampScroll() {
		scroll = MathUtils.clamp(scroll, minScroll, maxScroll);
	}

	public void clampZoom() {
		zoom = MathUtils.clamp(zoom, minZoom, maxZoom);
	}

	public double getScroll() {
		return scroll;
	}

	public void setScroll(double scroll) {
		this.scroll = scroll;
	}

	public double getMinScroll() {
		return minScroll;
	}

	public void setMinScroll(double minScroll) {
		this.minScroll = minScroll;
	}

	public double getMaxScroll() {
		return maxScroll;
	}

	public void setMaxScroll(double maxScroll) {
		this.maxScroll = maxScroll;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public double getMinZoom() {
		return minZoom;
	}

	public void setMinZoom(double minZoom) {
		this.minZoom = minZoom;
	}

	public double getMaxZoom() {
		return maxZoom;
	}

	public void setMaxZoom(double maxZoom) {
		this.maxZoom = maxZoom;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		Point cursorPoint = e.getPoint();
		double before = toWorld(cursorPoint.x);
		if (e.getWheelRotation() < 0)
			zoom *= 1.05d;
		else
			zoom /= 1.05d;
		clampZoom();
		double after = toWorld(cursorPoint.x);
		scroll += (before - after);
		panel.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		Point cursorPoint = e.getPoint();
		scroll -= (cursorPoint.x - scrollLast) / zoom;
		scrollLast = cursorPoint.x;
		clampScroll();
		panel.repaint();
	}

	public void mousePressed(MouseEvent e) {
		scrollLast = e.getPoint().x;
	}

}
