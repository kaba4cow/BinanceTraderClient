package kaba4cow.traderclient.ui.panels.graphs.indicators;

import java.awt.Color;

public class GraphLine {

	private double value;

	private Color color;

	public GraphLine(double value, Color color) {
		this.value = value;
		this.color = color;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
