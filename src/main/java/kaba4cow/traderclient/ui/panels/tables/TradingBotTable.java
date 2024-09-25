package kaba4cow.traderclient.ui.panels.tables;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.tradingbot.TradingBot;
import kaba4cow.traderclient.ui.dialogs.EditTradingBotDialog;
import kaba4cow.traderclient.ui.panels.tables.renderers.CenteredRenderer;
import kaba4cow.traderclient.utils.FormatUtils;

public class TradingBotTable extends JTable implements RefreshListener {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = { "Status", "Pair", "Interval", "Strategy", "Trade Quantity", "Profit",
			"Orders", "Created" };
	private static final int[] COLUMN_WIDTHS = { 50, 50, 50, 100, 50, 25, 25, 100 };

	private List<TradingBot> botList;

	private final TradingBotTableModel tableModel;

	public TradingBotTable() {
		super();
		this.botList = new ArrayList<>();
		this.tableModel = new TradingBotTableModel();
		setModel(tableModel);
		setCellSelectionEnabled(false);
		getTableHeader().setReorderingAllowed(false);
		getTableHeader().setResizingAllowed(false);
		CenteredRenderer centeredRenderer = new CenteredRenderer();
		int totalWidth = 0;
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			getColumnModel().getColumn(i).setCellRenderer(centeredRenderer);
			getColumnModel().getColumn(i).setPreferredWidth(COLUMN_WIDTHS[i]);
			totalWidth += COLUMN_WIDTHS[i];
		}
		setPreferredScrollableViewportSize(new Dimension(totalWidth, 0));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() % 2 == 0 && getSelectedRow() != -1) {
					TradingBot bot = botList.get(getSelectedRow());
					if (!ApplicationSettings.getBaseAsset().equals(bot.getBaseAsset())
							|| !ApplicationSettings.getQuoteAsset().equals(bot.getQuoteAsset())
							|| ApplicationSettings.getInterval() != bot.getInterval())
						ApplicationSettings.setSeries(bot.getBaseAsset(), bot.getQuoteAsset(), bot.getInterval());
					new EditTradingBotDialog(bot);
				}
			}
		});
		ApplicationSettings.addRefreshListener(this);
	}

	@Override
	public void onRefresh() {
		botList = TradingBot.getAllBots();
		tableModel.fireTableDataChanged();
	}

	private class TradingBotTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		public TradingBotTableModel() {
			super();
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		@Override
		public int getRowCount() {
			return botList.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TradingBot bot = botList.get(rowIndex);
			switch (columnIndex) {
			default:
			case 0: // Status
				return bot.isActive() ? "Active" : "Paused";
			case 1: // Pair
				return bot.getBaseAsset() + "/" + bot.getQuoteAsset();
			case 2: // Interval
				return bot.getInterval().toString();
			case 3: // Strategy
				return bot.getStrategy().getName();
			case 4: // Trade Quantity
				return FormatUtils.number(bot.getTradeQuantity(), 8) + " " + bot.getBaseAsset();
			case 5: // Profit
				return FormatUtils.percent(bot.getTradingReport().profit);
			case 6: // Orders
				return bot.getOrderCount();
			case 7: // Created
				return FormatUtils.dateTime(bot.getCreateTime());
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

	}

}
