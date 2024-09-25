package kaba4cow.traderclient.ui.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kaba4cow.traderclient.ui.dialogs.OrderFilterDialog;
import kaba4cow.traderclient.ui.panels.tables.OrderTable;

public class OrderHistoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public OrderHistoryPanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Order History"));
		setLayout(new BorderLayout());

		JButton filterButton = new JButton("Filter");
		filterButton.addActionListener(e -> new OrderFilterDialog());
		add(filterButton, BorderLayout.NORTH);

		OrderTable table = new OrderTable();
		JScrollPane scrollPanel = new JScrollPane(table);
		scrollPanel.setViewportView(table);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPanel, BorderLayout.CENTER);
	}

}
