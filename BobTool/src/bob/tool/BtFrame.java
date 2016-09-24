package bob.tool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import bob.api.IAction;
import bob.api.IPlugin;
import bob.core.BobConstants;
import bob.core.Config;
import bob.core.Utils;
import bob.tool.actions.AppExit;

/**
 * Die Ein-Fenster-Benutzeroberfl�che mit Werkzeug- und Aufgabenmenu sowie den
 * Steuerelementen f�r die Programmhistorie.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class BtFrame extends JFrame {

	/** das Programm */
	private final AbstractApplication app;

	/** Schl�ssel f�r Programmfensterbreite */
	public static final String CONFIG_WIDTH_KEY = "bobTool.width";

	/** Schl�ssel f�r Programmfensterh�he */
	public static final String CONFIG_HEIGHT_KEY = "bobTool.height";

	/** Signalfarbe f�r Bearbeitungsmodus und schwere Ausnahmefehler */
	private static final Color WARNING_COLOR = new Color(255, 138, 0);

	/** ein Panel f�r die TopComponent */
	private final JPanel contentPane = new JPanel(new BorderLayout(0, 0));

	/** aktuelle Werkzeugoberfl�che */
	private JPanel topComponent = null;

	/** ein {@link KeyStroke} f�r <tt>ALT+S</tt> */
	private final KeyStroke keyStrgS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);

	/** Konstante f�r {@link InputMap} und {@link ActionMap} */
	private static final String BEARBEITUNG_SPEICHERN = "bearbeitung.speichern";

	/** Tastenk�rzel <tt>ESC</tt> zum Schlie�en */
	private static final KeyStroke KEY_ESC = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

	/** Konstante f�r {@link InputMap} und {@link ActionMap} */
	private static final String BEARBEITUNG_ABBRECHEN = "bearbeitung.abbrechen";

	/** die aktuell sichtbaren Aufgaben */
	private final Set<IAction> currentActions = new LinkedHashSet<IAction>();

	private JPanel infoPanel = null;

	private final GlassPane glassPane;

	/** <code>true</code> wenn schwerer Ausnahemfehler angezeigt wird */
	private boolean exceptionMode = false;

	public BtFrame(final AbstractApplication app) {
		this.app = app;
		// Icon f�r Anwendung
		final String iconPath = app.getSettings().getProgramIcon();
		final ImageIcon icon = new ImageIcon(BtFrame.class.getResource(iconPath));
		setIconImage(icon.getImage());
		// Oberfl�che konfigurieren
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setupTitle();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				app.shutdown();
			}
		});
		setupMenubar();
		setupSize();
		glassPane = new GlassPane();
	}

	private void setupMenubar() {

		final JMenuBar x = new JMenuBar();

		final Action aaa = app.getLoginManager().getActionWithoutText();
		final JButton jButtonLock = new JButton(aaa);
		jButtonLock.setContentAreaFilled(false);
		jButtonLock.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		x.add(jButtonLock);

		x.add(Box.createHorizontalStrut(1));

		final JMenu jMenuProgram = new JMenu("Werkzeugmenü");
		// Werkzeuge auflisten
		final BtPluginManager bpm = app.getPluginManager();
		final Iterator<IPlugin> plugins = bpm.pluginList.iterator();
		while (plugins.hasNext()) {
			final IPlugin p = plugins.next();
			final Action a = bpm.getPluginAction(p);
			final JMenuItem item = new JMenuItem(a);
			jMenuProgram.add(a);
		}
		// Programm beenden
		jMenuProgram.add(new JSeparator());
		jMenuProgram.add(new JMenuItem(new AppExit(app)));
		x.add(jMenuProgram);

		setJMenuBar(x);

		// setJMenuBar(new BtMenuBar(app));
	}

	public void setupSize() {
		final Config cfg = Config.getDefault();
		// Breite und H�he holen
		Integer w = cfg.getInteger(CONFIG_WIDTH_KEY);
		w = ((null == w) || (640 > w) ? 640 : w);
		Integer h = cfg.getInteger(CONFIG_HEIGHT_KEY);
		h = ((null == h) || (480 > h) ? 480 : h);
		// Fenster einstellen
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		w = (screen.width > w ? w : (screen.width - 10));
		h = (screen.height > h ? h : (screen.height - 10));
		// Fenster positionieren
		Utils.centerOnScreen(this, w, h);
	}

	public void setupTitle() {
		final String title = app.getSettings().getToolTitle();
		final StringBuffer sb = new StringBuffer(title);
		if (app.isDemoActivated()) {
			sb.append(BobConstants.SPACE).append("[DEMO]");
		}
		if (null != AbstractApplication.BUILD_NUMBER) {
			sb.append(BobConstants.SPACE).append(BobConstants.MINUS);
			sb.append(BobConstants.SPACE).append("Build");
			sb.append(BobConstants.SPACE).append(AbstractApplication.BUILD_NUMBER);
		}
		setTitle(sb.toString());
	}

	public void setTopComponent(final JPanel tc) {
		if (null != topComponent) {
			contentPane.remove(topComponent);
		}
		if (null != tc) {
			// Gr��e
			tc.setMaximumSize(new Dimension(950, Integer.MAX_VALUE));
			tc.setPreferredSize(new Dimension(800, Integer.MAX_VALUE));
			// Bearbeitungsmodus
			final InputMap inputMap = tc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			final ActionMap actionMap = tc.getActionMap();
			if (null == inputMap.get(keyStrgS)) {
				inputMap.put(keyStrgS, BEARBEITUNG_SPEICHERN);
			}
			if (null == actionMap.get(BEARBEITUNG_SPEICHERN)) {
				actionMap.put(BEARBEITUNG_SPEICHERN, new ChangeModeAction(BEARBEITUNG_SPEICHERN));
			}
			if (null == inputMap.get(KEY_ESC)) {
				inputMap.put(KEY_ESC, BEARBEITUNG_ABBRECHEN);
			}
			if (null == actionMap.get(BEARBEITUNG_ABBRECHEN)) {
				actionMap.put(BEARBEITUNG_ABBRECHEN, new ChangeModeAction(BEARBEITUNG_ABBRECHEN));
			}
			// Anzeige
			contentPane.add(tc, BorderLayout.CENTER);
			tc.revalidate();
			topComponent = tc;
		}
	}

	private class ChangeModeAction extends AbstractAction {

		private final String cmd;

		public ChangeModeAction(final String cmd) {
			this.cmd = cmd;
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			if (app.isChangeMode()) {
				switchChangeMode(cmd);
			}
		}

	}

	public void switchChangeMode(final String cmd) {
		boolean success = false;
		if (BEARBEITUNG_SPEICHERN.equals(cmd)) {
			success = app.dataSave();
		} else if (BEARBEITUNG_ABBRECHEN.equals(cmd)) {
			final int a = JOptionPane.showConfirmDialog(this,
					"Die �nderungen werden r�ckg�ngig gemacht.\n"
							+ "Der letzte gespeicherte Stand wird wieder hergestellt.\n" + "Wirklich fortfahren?",
					app.getSettings().getToolTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (a == JOptionPane.YES_OPTION) {
				success = app.dataReset();
			}
		} else {
			throw new IllegalStateException("command unknown: " + cmd);
		}
		if (success) {
			setChangeMode(false);
		}
	}

	/**
	 * Schaltet die Anzeige "Bearbeitungsmodus" ein und aus.
	 * 
	 * @param b
	 *            <code>true</code> schaltet Bearbeitungsmodus ein
	 */
	public void setChangeMode(final boolean b) {
		if (b) {
			infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
			infoPanel.add(createInfoLabel("Bearbeitungsmodus"));
			infoPanel.add(createInfoButton("abbrechen"));
			infoPanel.add(createInfoLabel("oder �nderungen"));
			infoPanel.add(createInfoButton("speichern"));
			infoPanel.add(createInfoLabel("!"));
			infoPanel.setBackground(WARNING_COLOR);
			contentPane.add(infoPanel, BorderLayout.NORTH);
			final Border border = BorderFactory.createLineBorder(WARNING_COLOR, 1);
			contentPane.setBorder(border);
		} else {
			contentPane.remove(infoPanel);
			contentPane.setBorder(null);
			infoPanel = null;
		}
		// Historie an/aus
		// TODO Historie implementieren
		// getTracker().setEnabled(!b);
		// Aktions an/aus
		actionsOnOff(b);
		// Keybinding an/aus
		keybindingOnOff(b);
	}

	private JLabel createInfoLabel(final String text) {
		final JLabel jLabel = new JLabel(text);
		jLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		jLabel.setForeground(Color.WHITE);
		return jLabel;
	}

	private JLabel createInfoButton(final String text) {
		final JLabel jLabel = new JLabel(text);
		jLabel.setName(text);
		jLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		jLabel.setForeground(Color.BLUE);
		jLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		jLabel.addMouseListener(new ChangeModeMouseAdapter());
		return jLabel;
	}

	/**
	 * Schaltet die Aktion abh�ngig vom Bearbeitungsmodus an oder aus.
	 * 
	 * @param changeMode
	 *            <code>true</code> wenn der Bearbeitungsmodus an ist
	 */
	private void actionsOnOff(final boolean changeMode) {
		for (final IAction a : currentActions) {
			final int visibility = a.getVisibility();
			final boolean enabled;
			if (0 < (visibility & IAction.EDIT_MODE_BOTH)) {
				enabled = true;
			} else if (changeMode && (0 < (visibility & IAction.EDIT_MODE_ON))) {
				enabled = true;
			} else if (!changeMode && (0 < (visibility & IAction.EDIT_MODE_OFF))) {
				enabled = true;
			} else {
				enabled = false;
			}
			a.getAction().setEnabled(enabled);
		}
	}

	private void keybindingOnOff(final boolean changeMode) {
	}

	/**
	 * Verbindet �ber {@link MouseAdapter#mouseClicked(MouseEvent)} eine
	 * {@link MouseEvent} sendende Komponente der Benutzeroberfl�che mit der
	 * M�glichkeit den Bearbeitungsmodus zu beenden.
	 */
	private class ChangeModeMouseAdapter extends MouseAdapter {

		@Override
		public void mouseClicked(final MouseEvent e) {
			final String cmd = e.getComponent().getName();
			assert cmd != null;
			switchChangeMode(cmd);
		}

	}

	public void setActions(final Set<IAction> actions) {
		currentActions.clear();
		currentActions.addAll(actions);
	}

	/**
	 * Benennt eine Hilfeseite zum aktuellen Werkzeug. Wird <code>null</code>
	 * gesetzt, ist keine Hilfeseite zum Werkzeug bekannt.
	 * 
	 * @param url
	 *            eine Adresse
	 */
	public void setHelpUrl(final String url) {
		if (Utils.isEmpty(url)) {

		} else {

		}
	}

	public void startException(final String text) {
		glassPane.jLabelInfo.setText(text);
		glassPane.switchExceptionMode(true);
		glassPane.setVisible(true);
	}

	public void stopException() {
		final int a = JOptionPane.showOptionDialog(this,
				"Die Anwendung ist instabil und sollte nur " + "zur Fehleranlyse weiter betrieben werden.",
				"Schwerer Ausnahmefehler", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
				new String[] { "Programm beenden", "Fortsetzen" }, JOptionPane.YES_OPTION);
		if (JOptionPane.YES_OPTION == a) {
			System.exit(0);
		} else {
			glassPane.jLabelInfo.setText("");
			glassPane.switchExceptionMode(false);
			glassPane.setVisible(false);
		}
	}

	/**
	 * Eine "Scheibe" vor dem Anwendungsfenster, um dieses komplett zu sperren.
	 * 
	 * @see <a href=
	 *      "http://docs.oracle.com/javase/tutorial/uiswing/components/rootpane.html">
	 *      How to Use Root Panes</a>
	 */
	public class GlassPane extends JPanel {

		private final JLabel jLabel;
		private final JLabel jLabelInfo;
		private final JButton jButton;

		public GlassPane() {
			super(new GridBagLayout());
			setOpaque(false);
			setBackground(new Color(75, 75, 75, 100));
			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					e.consume();
				}
			});
			setFocusTraversalKeysEnabled(false);

			final GridBagConstraints gbc = new GridBagConstraints();
			// Label mit Ups
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(5, 5, 30, 5);
			jLabel = new JLabel("Ups, das h�tte nicht passieren d�rfen...");
			jLabel.setFont(jLabel.getFont().deriveFont(Font.BOLD, 24.0f));
			jLabel.setForeground(WARNING_COLOR);
			add(jLabel, gbc);
			// Label mit Info
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			jLabelInfo = new JLabel();
			add(jLabelInfo, gbc);
			// Button
			jButton = new JButton("Fehlermeldung schlie�en");
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.insets = new Insets(30, 5, 5, 5);
			jButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					stopException();
				}
			});
			add(jButton, gbc);

			switchExceptionMode(false);

		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			g.setColor(getBackground());
			g.fillRect(0, 0, getSize().width, getSize().height);
		}

		private void switchExceptionMode(final boolean b) {
			exceptionMode = b;
			jLabel.setVisible(b);
			final boolean infoOnOff = (0 < jLabelInfo.getText().trim().length());
			jLabelInfo.setVisible(infoOnOff);
			jButton.setVisible(b);
			revalidate();
		}

	}

}
