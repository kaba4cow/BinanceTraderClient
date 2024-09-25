package kaba4cow.traderclient.utils;

import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;

public final class UIUtils {

	private UIUtils() {
	}

	public static void initialize() {
		Locale.setDefault(Locale.US);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
	}

	public static JPanel createGridPanel(int rows, int cols) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(rows, cols));
		return panel;
	}

	public static JPanel createTitledGridPanel(String title, int rows, int cols) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(rows, cols));
		panel.setBorder(BorderFactory.createTitledBorder(title));
		return panel;
	}

}
