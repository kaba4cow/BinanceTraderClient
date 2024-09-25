package kaba4cow.traderclient.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JProgressBar progressBar;

	public ProgressDialog(int min, int max) {
		super();
		setTitle("Please wait...");

		progressBar = new JProgressBar(min, max);
		progressBar.setStringPainted(true);
		add(progressBar);

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	public void updateProgress(int progress) {
		if (progressBar.getValue() != progress)
			progressBar.setValue(progress);
	}

}
