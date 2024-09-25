package kaba4cow.traderclient.ui.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.BinanceSpot;
import kaba4cow.traderclient.data.OrderBook;
import kaba4cow.traderclient.data.Ticker;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.ta.bars.BarInterval;
import kaba4cow.traderclient.ui.dialogs.SymbolDialog;
import kaba4cow.traderclient.utils.FormatUtils;
import kaba4cow.traderclient.utils.UIUtils;

public class TickerPanel extends JPanel implements RefreshListener {

	private static final long serialVersionUID = 1L;

	private final JLabel lastPriceLabel;
	private final JLabel bidVolumeLabel;
	private final JLabel askVolumeLabel;
	private final JLabel priceChangeLabel;
	private final JLabel lowPriceLabel;
	private final JLabel highPriceLabel;
	private final JLabel baseVolumeLabel;
	private final JLabel quoteVolumeLabel;
	private final JLabel tradesLabel;

	private final JButton symbolButton;
	private final JComboBox<String> intervalComboBox;

	private boolean listening;

	public TickerPanel() {
		super();
		setLayout(new GridLayout(1, 0));
		ApplicationSettings.addRefreshListener(this);

		{
			JPanel panel = UIUtils.createTitledGridPanel("Settings", 2, 2);
			panel.add(new JLabel("Pair:"));
			panel.add(symbolButton = new JButton());
			symbolButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SymbolDialog dialog = new SymbolDialog(ApplicationSettings.getBaseAsset(),
							ApplicationSettings.getQuoteAsset());
					if (dialog.getBaseAsset() != null)
						ApplicationSettings.setSeries(dialog.getBaseAsset(), dialog.getQuoteAsset(),
								ApplicationSettings.getInterval());
				}
			});

			panel.add(new JLabel("Interval:"));
			panel.add(intervalComboBox = new JComboBox<>());
			for (BarInterval interval : BarInterval.values())
				intervalComboBox.addItem(interval.toString());
			intervalComboBox.setSelectedItem(ApplicationSettings.getInterval().toString());
			intervalComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (listening)
						ApplicationSettings.setSeries(ApplicationSettings.getBaseAsset(),
								ApplicationSettings.getQuoteAsset(),
								BarInterval.get((String) intervalComboBox.getSelectedItem()));
				}
			});

			add(panel);
		}

		{
			JPanel panel = UIUtils.createTitledGridPanel("Current Price", 3, 1);
			panel.add(lastPriceLabel = new JLabel());
			panel.add(bidVolumeLabel = new JLabel());
			panel.add(askVolumeLabel = new JLabel());
			add(panel);
		}

		{
			JPanel panel = UIUtils.createTitledGridPanel("24h Price", 3, 1);
			panel.add(priceChangeLabel = new JLabel());
			panel.add(lowPriceLabel = new JLabel());
			panel.add(highPriceLabel = new JLabel());
			add(panel);
		}

		{
			JPanel panel = UIUtils.createTitledGridPanel("24h Volume", 3, 1);
			panel.add(baseVolumeLabel = new JLabel());
			panel.add(quoteVolumeLabel = new JLabel());
			panel.add(tradesLabel = new JLabel());
			add(panel);
		}

		listening = true;
	}

	public void updateSettings() {
		listening = false;
		updateSymbolButton();
		intervalComboBox.setSelectedItem(ApplicationSettings.getInterval());
		listening = true;
	}

	private void updateSymbolButton() {
		symbolButton.setText(ApplicationSettings.getBaseAsset() + "/" + ApplicationSettings.getQuoteAsset());
	}

	private void updateInfo() {
		String baseAsset = ApplicationSettings.getBaseAsset();
		String quoteAsset = ApplicationSettings.getQuoteAsset();
		Ticker ticker = BinanceSpot.getTicker(baseAsset + quoteAsset);
		OrderBook orderBook = BinanceSpot.getOrderBook(baseAsset + quoteAsset, 15);

		lastPriceLabel.setText(//
				String.format("Price: %s %s", FormatUtils.number(ticker.lastPrice, 8), quoteAsset));
		bidVolumeLabel.setText(//
				String.format("Bids: %s", FormatUtils.percent(orderBook.bidVolumePercent)));
		askVolumeLabel.setText(//
				String.format("Asks: %s", FormatUtils.percent(orderBook.askVolumePercent)));

		priceChangeLabel.setText(//
				String.format("Change: %s %s (%s)", FormatUtils.number(ticker.priceChange, 8), quoteAsset,
						FormatUtils.percent(ticker.priceChangePercent / 100d)));
		lowPriceLabel.setText(//
				String.format("Low: %s %s", FormatUtils.number(ticker.lowPrice, 8), quoteAsset));
		highPriceLabel.setText(//
				String.format("High: %s %s", FormatUtils.number(ticker.highPrice, 8), quoteAsset));

		baseVolumeLabel.setText(//
				String.format("Base: %s %s", FormatUtils.number(ticker.baseVolume, 3), baseAsset));
		quoteVolumeLabel.setText(//
				String.format("Quote: %s %s", FormatUtils.number(ticker.quoteVolume, 3), quoteAsset));
		tradesLabel.setText(//
				String.format("Trades: %d", ticker.trades));
	}

	@Override
	public void onRefresh() {
		updateSymbolButton();
		updateSettings();
		updateInfo();
	}

}
