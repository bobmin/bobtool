package bob.tool;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import bob.api.IValidation;
import bob.core.BobConstants;
import bob.core.Utils;

/**
 * Erweitert die {@link JDialog} für das BurTool.
 * 
 * @author maik@btmx.net
 *
 */
public class BtDialog {

	private static final String OKAY_BUTTON_NAME = "jButtonOkay";
	
	public static void show(final BtMain main, final JPanel panel, 
			final String title, final int type, final String[] options,
			final ActionListener listener, final IValidation validation) {
		final JDialog dialog = new JDialog(main, title, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		String iconName = null;
		if (type == JOptionPane.INFORMATION_MESSAGE) {
			iconName = "OptionPane.informationIcon";
		} else if (type == JOptionPane.QUESTION_MESSAGE) {
			iconName = "OptionPane.questionIcon";
		} else if (type == JOptionPane.ERROR_MESSAGE) {
			iconName = "OptionPane.errorIcon";
		}
		if (null != iconName) {
			final JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			iconPanel.add(new JLabel(UIManager.getIcon(iconName)));
			centerPanel.add(iconPanel, BorderLayout.WEST);
		}
		
		centerPanel.add(panel, BorderLayout.CENTER);
		final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 6, 5));
		for (int idx=0; idx<options.length; idx++) {
			final String opt = options[idx];
			final JButton jButton = new JButton(opt);
			if (0 == idx) {
				jButton.setName(OKAY_BUTTON_NAME);
			} else if (idx < (options.length - 1)) {
				jButton.setName(OKAY_BUTTON_NAME + (idx+1));
			}
			jButton.addActionListener(
					new ActionListenerProxy(main, dialog, listener, validation));
			southPanel.add(jButton);
		}
		centerPanel.add(southPanel, BorderLayout.SOUTH);
		dialog.getContentPane().add(centerPanel);

		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doClickEnter");
		panel.getActionMap().put("doClickEnter", new AbstractAction() {
			
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Component[] comps = southPanel.getComponents();
				for (final Component c: comps) {
					if ("jButtonOkay".equals(c.getName())) {
						((JButton) c).doClick();
					}
				}
			}
		});
		dialog.pack();
		Utils.centerOnScreen(dialog, dialog.getWidth(), dialog.getHeight());
		dialog.setVisible(true);
	}
	
	/**
	 * Kapselt einen {@link ActionListener} und führt ihn nur aus, wenn
	 * {@link IValidation} gleich <code>null</code> oder 
	 * {@link IValidation#validate(ActionEvent)} gleich <code>true</code>. 
	 */
	public static class ActionListenerProxy implements ActionListener {
		
		/** der {@link JDialog} */
		private final JDialog dialog;
		
		/** ein optionaler {@link ActionListener} */
		private final ActionListener childActionListener;
		
		/** eine optionale Datenprüfung */
		private final IValidation validation;
		
		private final BtMain main;
		
		public ActionListenerProxy(final BtMain main, final JDialog dialog, 
				final ActionListener childActionListener, 
				final IValidation validation) {
			this.main = main;
			this.dialog = dialog;
			this.childActionListener = childActionListener;
			this.validation = validation;
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			boolean abort = true;
			final Object obj = evt.getSource();
			if (obj instanceof JButton) {
				final JButton b = (JButton) obj;
				if ((null != b.getName()) && b.getName().startsWith(OKAY_BUTTON_NAME)) {
					abort = false;
				}
			}
			if (abort || (null == validation) || validation.validate(evt)) {
				if (null != childActionListener) {
					childActionListener.actionPerformed(evt);
				}
				dialog.setVisible(false);
				dialog.dispose();
			} else {
				String msg = null;
				if (null != validation) {
					msg = validation.getMessage();
				}
				if (Utils.isEmpty(msg)) {
					msg = BobConstants.INPUT_NOT_USABLE;
				}
				main.showMessage(msg, JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
}
