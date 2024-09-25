package kaba4cow.traderclient.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import kaba4cow.traderclient.ApplicationSettings;
import kaba4cow.traderclient.data.Order.OrderStatus;
import kaba4cow.traderclient.data.Order.OrderType;
import kaba4cow.traderclient.utils.UIUtils;
import kaba4cow.traderclient.utils.filters.OrderStatusFilter;
import kaba4cow.traderclient.utils.filters.OrderTypeFilter;

public class OrderFilterDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final Map<OrderStatus, JCheckBox> statusCheckBoxes;
	private final Map<OrderType, JCheckBox> typeCheckBoxes;

	private final JButton acceptButton;
	private final JButton cancelButton;

	public OrderFilterDialog() {
		super();
		setTitle("Select filters");
		setLayout(new BorderLayout());

		statusCheckBoxes = new HashMap<OrderStatus, JCheckBox>();
		JPanel statusFilterPanel = UIUtils.createTitledGridPanel("Status", OrderStatus.values().length, 1);
		for (OrderStatus status : OrderStatus.values()) {
			JCheckBox checkBox = new JCheckBox(status.getName());
			checkBox.setSelected(ApplicationSettings.getOrderStatusFilter().get(status));
			statusFilterPanel.add(checkBox);
			statusCheckBoxes.put(status, checkBox);
		}

		typeCheckBoxes = new HashMap<OrderType, JCheckBox>();
		JPanel typeFilterPanel = UIUtils.createTitledGridPanel("Type", OrderType.values().length, 1);
		for (OrderType type : OrderType.values()) {
			JCheckBox checkBox = new JCheckBox(type.getName());
			checkBox.setSelected(ApplicationSettings.getOrderTypeFilter().get(type));
			typeFilterPanel.add(checkBox);
			typeCheckBoxes.put(type, checkBox);
		}

		acceptButton = new JButton("Accept");
		acceptButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(1, 2));
		filterPanel.add(statusFilterPanel);
		filterPanel.add(typeFilterPanel);
		add(filterPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(acceptButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == acceptButton) {
			OrderStatusFilter statusFilter = ApplicationSettings.getOrderStatusFilter();
			for (OrderStatus status : statusCheckBoxes.keySet())
				statusFilter.set(status, statusCheckBoxes.get(status).isSelected());
			OrderTypeFilter typeFilter = ApplicationSettings.getOrderTypeFilter();
			for (OrderType type : typeCheckBoxes.keySet())
				typeFilter.set(type, typeCheckBoxes.get(type).isSelected());
			dispose();
			ApplicationSettings.updateOrderFilter();
		} else if (e.getSource() == cancelButton)
			dispose();
	}

}