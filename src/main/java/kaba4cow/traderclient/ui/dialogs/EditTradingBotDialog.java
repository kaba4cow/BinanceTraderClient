package kaba4cow.traderclient.ui.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;

import kaba4cow.traderclient.tradingbot.TradingBot;
import kaba4cow.traderclient.utils.FormatUtils;
import kaba4cow.traderclient.utils.TradingReport;
import kaba4cow.traderclient.utils.UIUtils;

public class EditTradingBotDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JButton pauseButton;
	private final JButton removeButton;

	public EditTradingBotDialog(TradingBot bot) {
		super();
		setTitle("Trading Bot Info");
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;

		{
			JPanel botPanel = UIUtils.createTitledGridPanel("Bot Parameters", 0, 2);
			botPanel.add(new JLabel("Pair:"));
			botPanel.add(new JLabel(bot.getBaseAsset() + "/" + bot.getQuoteAsset()));
			botPanel.add(new JLabel("Interval:"));
			botPanel.add(new JLabel(bot.getInterval().toString()));
			botPanel.add(new JLabel("Trade Quantity:"));
			botPanel.add(new JLabel(FormatUtils.number(bot.getTradeQuantity(), 8) + " " + bot.getBaseAsset()));
			botPanel.add(new JLabel("Strategy:"));
			botPanel.add(new JLabel(bot.getStrategy().getName()));
			add(botPanel, gbc);
		}

		{
			JPanel strategyPanel = UIUtils.createTitledGridPanel("Strategy Parameters", 0, 2);
			JSONArray json = bot.getStrategy().serialize().getJSONArray("fields");
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonField = json.getJSONObject(i);
				strategyPanel.add(new JLabel(jsonField.getString("name") + ":"));
				strategyPanel.add(new JLabel(jsonField.get("value").toString()));
			}
			add(strategyPanel, gbc);
		}

		{
			JPanel reportPanel = UIUtils.createTitledGridPanel("Trading Report", 0, 2);
			TradingReport report = bot.getTradingReport();
			reportPanel.add(new JLabel("Profit:"));
			reportPanel.add(new JLabel(FormatUtils.percent(report.profit)));
			reportPanel.add(new JLabel("Winrate:"));
			reportPanel.add(new JLabel(FormatUtils.percent(report.winrate)));
			reportPanel.add(new JLabel("Density:"));
			reportPanel.add(new JLabel(FormatUtils.percent(report.density)));
			reportPanel.add(new JLabel("Orders:"));
			reportPanel.add(new JLabel(Integer.toString(bot.getOrderCount())));
			add(reportPanel, gbc);
		}

		{
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 0));
			pauseButton = new JButton();
			pauseButton.setText(bot.isActive() ? "Stop" : "Start");
			pauseButton.addActionListener(e -> {
				if (bot.isActive())
					bot.stop();
				else
					bot.start();
				dispose();
			});
			buttonPanel.add(pauseButton);
			removeButton = new JButton("Remove");
			removeButton.addActionListener(e -> {
				bot.remove();
				dispose();
			});
			buttonPanel.add(removeButton);
			add(buttonPanel, gbc);
		}

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

}