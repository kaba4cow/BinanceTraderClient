package kaba4cow.traderclient.ui.panels.tables;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.ta4j.core.Trade.TradeType;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.BinanceSpot;
import kaba4cow.traderclient.data.Order;
import kaba4cow.traderclient.listeners.OrderFilterListener;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.ui.UIColors;
import kaba4cow.traderclient.ui.panels.tables.renderers.CenteredRenderer;
import kaba4cow.traderclient.ui.panels.tables.renderers.ColoredRenderer;
import kaba4cow.traderclient.utils.FormatUtils;

public class OrderTable extends JTable implements RefreshListener, OrderFilterListener {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = { "", "Type", "Status", "Price", "Quantity", "Total", "Time" };
	private static final int[] COLUMN_SUFFIXES = { 0, 0, 0, 2, 1, 2, 0 };
	private static final int[] COLUMN_WIDTHS = { 10, 50, 50, 75, 75, 75, 100 };

	private List<Order> orderList;

	private final OrderTableModel tableModel;

	public OrderTable() {
		super();
		this.orderList = new ArrayList<>();
		this.tableModel = new OrderTableModel();
		setModel(tableModel);
		setCellSelectionEnabled(false);
		getTableHeader().setReorderingAllowed(false);
		getTableHeader().setResizingAllowed(false);
		CenteredRenderer centeredRenderer = new CenteredRenderer();
		ColoredRenderer coloredRenderer = new ColoredRenderer();
		int totalWidth = 0;
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			if (i == 0)
				getColumnModel().getColumn(i).setCellRenderer(coloredRenderer);
			else
				getColumnModel().getColumn(i).setCellRenderer(centeredRenderer);
			getColumnModel().getColumn(i).setPreferredWidth(COLUMN_WIDTHS[i]);
			totalWidth += COLUMN_WIDTHS[i];
		}
		setPreferredScrollableViewportSize(new Dimension(totalWidth, 0));
		ApplicationSettings.addRefreshListener(this);
		ApplicationSettings.addOrderFilterListener(this);
	}

	@Override
	public void onRefresh() {
		orderList = BinanceSpot.getOrders(ApplicationSettings.getSymbol(), ApplicationSettings.getOrderTypeFilter(),
				ApplicationSettings.getOrderStatusFilter());
		Collections.reverse(orderList);
		tableModel.fireTableDataChanged();
		for (int i = 0; i < tableModel.getColumnCount(); i++)
			getColumnModel().getColumn(i).setHeaderValue(tableModel.getColumnName(i));
		getTableHeader().repaint();
	}

	@Override
	public void onOrderFilterUpdated() {
		onRefresh();
	}

	private class OrderTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		public OrderTableModel() {
			super();
		}

		@Override
		public String getColumnName(int column) {
			if (COLUMN_SUFFIXES[column] == 0)
				return COLUMN_NAMES[column];
			return String.format("%s (%s)", COLUMN_NAMES[column],
					COLUMN_SUFFIXES[column] == 1 ? ApplicationSettings.getBaseAsset()
							: ApplicationSettings.getQuoteAsset());
		}

		@Override
		public int getRowCount() {
			return orderList.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Order order = orderList.get(rowIndex);
			switch (columnIndex) {
			default:
			case 0: // Side
				return order.side == TradeType.BUY ? UIColors.buy : UIColors.sell;
			case 1: // Type
				return order.type.getName();
			case 2: // Status
				return order.status.getName();
			case 3: // Price
				return FormatUtils.number(order.price, 8);
			case 4: // Quantity
				return FormatUtils.number(order.executedQuantity, 8);
			case 5: // Total
				return FormatUtils.number(order.executedQuantity * order.price, 8);
			case 6: // Time
				return FormatUtils.dateTime(order.time);
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

	}

}
