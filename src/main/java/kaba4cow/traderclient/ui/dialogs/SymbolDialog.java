package kaba4cow.traderclient.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import kaba4cow.traderclient.BinanceSpot;

public class SymbolDialog extends JDialog implements DocumentListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private final DefaultListModel<String> baseAssetListModel;
	private final JList<String> baseAssetList;
	private final JComboBox<String> quoteAssetComboBox;
	private final JTextField filterTextField;

	private final JButton selectButton;
	private final JButton cancelButton;

	private boolean option;

	public SymbolDialog(String baseAsset, String quoteAsset) {
		super();
		setTitle("Select symbol");
		setLayout(new BorderLayout());

		baseAssetListModel = new DefaultListModel<>();
		baseAssetList = new JList<>(baseAssetListModel);
		baseAssetList.addListSelectionListener(this);

		quoteAssetComboBox = new JComboBox<>();

		filterTextField = new JTextField();
		filterTextField.getDocument().addDocumentListener(this);

		selectButton = new JButton("Select");
		selectButton.addActionListener(e -> {
			option = true;
			dispose();
		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			option = false;
			dispose();
		});

		JPanel assetPanel = new JPanel();
		assetPanel.setLayout(new BorderLayout());
		assetPanel.add(filterTextField, BorderLayout.NORTH);
		assetPanel.add(new JScrollPane(baseAssetList), BorderLayout.CENTER);
		assetPanel.add(quoteAssetComboBox, BorderLayout.SOUTH);
		add(assetPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(selectButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

		updateBaseAssets("");
		updateQuoteAssets(baseAsset);
		baseAssetList.setSelectedValue(baseAsset, true);
		quoteAssetComboBox.setSelectedItem(quoteAsset);
		option = false;

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private void updateBaseAssets(String filter) {
		filter = filter.toUpperCase();
		baseAssetListModel.clear();
		Set<String> baseAssets = BinanceSpot.getBaseAssets();
		for (String asset : baseAssets)
			if (asset.toUpperCase().contains(filter))
				baseAssetListModel.addElement(asset);
	}

	private void updateQuoteAssets(String baseAsset) {
		quoteAssetComboBox.removeAllItems();
		List<String> quoteAssets = BinanceSpot.getQuoteAssets(baseAsset);
		for (String quoteAsset : quoteAssets)
			quoteAssetComboBox.addItem(quoteAsset);
		quoteAssetComboBox.setSelectedItem("USDT");
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		updateQuoteAssets(baseAssetList.getSelectedValue());
		selectButton.setEnabled(baseAssetList.getSelectedValue() != null);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateBaseAssets(filterTextField.getText());
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateBaseAssets(filterTextField.getText());
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateBaseAssets(filterTextField.getText());
	}

	public String getBaseAsset() {
		return option ? baseAssetList.getSelectedValue() : null;
	}

	public String getQuoteAsset() {
		return option ? (String) quoteAssetComboBox.getSelectedItem() : null;
	}
}