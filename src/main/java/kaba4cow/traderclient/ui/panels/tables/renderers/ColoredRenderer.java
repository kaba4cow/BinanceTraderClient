package kaba4cow.traderclient.ui.panels.tables.renderers;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColoredRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public ColoredRenderer() {
		super();
	}

	@Override
	public JLabel getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setText("");
		label.setBorder(BorderFactory.createLoweredBevelBorder());
		label.setOpaque(true);
		label.setBackground((Color) table.getValueAt(row, 0));
		return label;
	}

}