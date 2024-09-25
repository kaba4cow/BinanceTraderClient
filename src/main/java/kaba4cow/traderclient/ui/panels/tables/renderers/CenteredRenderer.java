package kaba4cow.traderclient.ui.panels.tables.renderers;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CenteredRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public CenteredRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}

}