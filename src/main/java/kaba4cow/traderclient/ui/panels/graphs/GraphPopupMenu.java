package kaba4cow.traderclient.ui.panels.graphs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import kaba4cow.traderclient.ui.panels.GraphPanel;

public class GraphPopupMenu extends JPopupMenu implements MouseListener {

	private static final long serialVersionUID = 1L;

	private Graph graph;

	private final JMenuItem addItem;
	private final JMenuItem editItem;
	private final JMenuItem removeItem;

	public GraphPopupMenu(GraphPanel panel) {
		super();
		this.graph = null;
		addItem = new JMenuItem("Add");
		addItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Add Graph");
			}
		});
		add(addItem);
		addSeparator();
		editItem = new JMenuItem("Edit");
		editItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Edit Graph");
			}
		});
		add(editItem);
		removeItem = new JMenuItem("Remove");
		removeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (graph != null)
					panel.removeGraph(graph);
			}
		});
		add(removeItem);
	}

	public void show(int x, int y, Graph graph) {
		this.graph = graph;
		editItem.setEnabled(graph != null);
		removeItem.setEnabled(graph != null);
		show(graph, x, y);
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event))
			show(event.getX(), event.getY(), (Graph) event.getSource());
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}