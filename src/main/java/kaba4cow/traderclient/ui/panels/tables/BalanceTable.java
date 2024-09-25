package kaba4cow.traderclient.ui.panels.tables;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.BinanceSpot;
import kaba4cow.traderclient.data.Balance;
import kaba4cow.traderclient.data.Balance.Asset;
import kaba4cow.traderclient.listeners.RefreshListener;
import kaba4cow.traderclient.ui.panels.tables.renderers.CenteredRenderer;

public class BalanceTable extends JTable implements RefreshListener {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAMES = { "Asset", "Total", "Free", "Locked" };
	private static final int[] COLUMN_WIDTHS = { 50, 50, 50, 50 };

	private Balance balance;
	private List<String> assets;

	private final BalanceTableModel tableModel;

	public BalanceTable() {
		super();
		this.balance = null;
		this.assets = new ArrayList<>();
		this.tableModel = new BalanceTableModel();
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
		ApplicationSettings.addRefreshListener(this);
	}

	@Override
	public void onRefresh() {
		balance = BinanceSpot.getBalance();
		assets.clear();
		for (String asset : balance.assets())
			assets.add(asset);
		tableModel.fireTableDataChanged();
	}

	public Balance getBalance() {
		return balance;
	}

	private class BalanceTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		public BalanceTableModel() {
			super();
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		@Override
		public int getRowCount() {
			return balance == null ? 0 : balance.assetCount();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Asset asset = balance.asset(assets.get(rowIndex));
			switch (columnIndex) {
			default:
			case 0: // Asset
				return assets.get(rowIndex);
			case 1: // Total
				return asset.total;
			case 2: // Free
				return asset.free;
			case 3: // Locked
				return asset.locked;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

	}

}
