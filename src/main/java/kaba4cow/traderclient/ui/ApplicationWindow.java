package kaba4cow.traderclient.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import kaba4cow.traderclient.ui.panels.BalancePanel;
import kaba4cow.traderclient.ui.panels.GraphPanel;
import kaba4cow.traderclient.ui.panels.OrderHistoryPanel;
import kaba4cow.traderclient.ui.panels.TickerPanel;
import kaba4cow.traderclient.ui.panels.TradingBotPanel;

public class ApplicationWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public ApplicationWindow() {
		super();
		setTitle("Trader Client");
		setLayout(new BorderLayout());

		add(new TickerPanel(), BorderLayout.NORTH);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 1));
		add(topPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(new BalancePanel(), BorderLayout.WEST);
		{
			JPanel rightPanel = new JPanel();
			rightPanel.setLayout(new GridLayout(2, 1));
			rightPanel.add(new TradingBotPanel());
			rightPanel.add(new OrderHistoryPanel());
			bottomPanel.add(rightPanel, BorderLayout.CENTER);
		}
		topPanel.add(new GraphPanel());
		topPanel.add(bottomPanel);

		setMinimumSize(new Dimension(720, 480));
		pack();
		setFocusable(true);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
