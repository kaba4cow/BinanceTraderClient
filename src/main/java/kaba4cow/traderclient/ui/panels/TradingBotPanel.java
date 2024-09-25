package kaba4cow.traderclient.ui.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kaba4cow.traderclient.ui.dialogs.CreateTradingBotDialog;
import kaba4cow.traderclient.ui.panels.tables.TradingBotTable;

public class TradingBotPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public TradingBotPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Trading Bots"));
		setLayout(new BorderLayout());

		JButton filterButton = new JButton("Create Trading Bot");
		filterButton.addActionListener(e -> new CreateTradingBotDialog());
		add(filterButton, BorderLayout.NORTH);

		TradingBotTable table = new TradingBotTable();
		JScrollPane scrollPanel = new JScrollPane(table);
		scrollPanel.setViewportView(table);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPanel, BorderLayout.CENTER);
	}

}
