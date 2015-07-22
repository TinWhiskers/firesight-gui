package io.tinwhiskers.firesight.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class OpSelectionDialog<T> extends JDialog {
	private Op selectedOp;
	private JList<OpListItem> list;

	public OpSelectionDialog(Frame parent, String title, String description, Ops ops) {
		super(parent, title, true);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(8, 8, 4, 8));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panelActions = new JPanel();
		panel.add(panelActions, BorderLayout.SOUTH);
		panelActions.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnCancel = new JButton(cancelAction);
		panelActions.add(btnCancel);

		JButton btnSelect = new JButton(selectAction);
		panelActions.add(btnSelect);

		JLabel lblDescription = new JLabel(
				"Please select an implemention class from the given list. Or whatever.");
		lblDescription.setBorder(new EmptyBorder(4, 4, 8, 4));
		panel.add(lblDescription, BorderLayout.NORTH);
		lblDescription.setHorizontalAlignment(SwingConstants.LEFT);

		lblDescription.setText(description);
		list = new JList<OpListItem>();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectAction.actionPerformed(null);
				}
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(list, BorderLayout.CENTER);
		// setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(parent);

		DefaultListModel<OpListItem> listModel = new DefaultListModel<OpListItem>();
		list.setModel(listModel);
		for (Op op : ops) {
			listModel.addElement(new OpListItem(op));
		}

		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				selectAction.setEnabled(OpSelectionDialog.this.list
						.getSelectedValue() != null);
			}
		});

		JRootPane rootPane = getRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		InputMap inputMap = rootPane
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", cancelAction);

		selectAction.setEnabled(false);
	}

	private final Action selectAction = new AbstractAction("Accept") {
		public void actionPerformed(ActionEvent arg0) {
			selectedOp = ((OpListItem) list.getSelectedValue())
					.getOp();
			setVisible(false);
		}
	};

	private final Action cancelAction = new AbstractAction("Cancel") {
		public void actionPerformed(ActionEvent arg0) {
			setVisible(false);
		}
	};

	public Op getSelectedOp() {
		return selectedOp;
	}

	private class OpListItem {
		private Op op;

		public OpListItem(Op op) {
			this.op = op;
		}

		@Override
		public String toString() {
			return op.getName();
		}

		public Op getOp() {
			return op;
		}
	}
}
