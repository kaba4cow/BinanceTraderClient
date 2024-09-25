package kaba4cow.traderclient.ui.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.data.Balance;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.ui.panels.tables.BalanceTable;
import kaba4cow.traderclient.utils.FormatUtils;

public class BalancePanel extends JPanel implements RefreshListener {

	private static final long serialVersionUID = 1L;

	private final JLabel totalLabel;
	private final BalanceTable table;

	public BalancePanel() {
		super();
		setBorder(BorderFactory.createTitledBorder("Balance"));
		setLayout(new BorderLayout());

		add(totalLabel = new JLabel(), BorderLayout.NORTH);

		table = new BalanceTable();
		JScrollPane scrollPanel = new JScrollPane(table);
		scrollPanel.setViewportView(table);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPanel, BorderLayout.CENTER);

		ApplicationSettings.addRefreshListener(this);
	}

	@Override
	public void onRefresh() {
		Balance balance = table.getBalance();
		if (balance == null)
			totalLabel.setText("Total: N/A");
		else
			totalLabel.setText(String.format("Total: $%s", FormatUtils.number(balance.total(), 8)));
	}

}
