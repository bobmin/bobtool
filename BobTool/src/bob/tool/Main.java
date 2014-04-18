package bob.tool;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import bob.core.BobConstants;
import bob.core.Config;
import bob.core.Utils;

public class Main extends JFrame {
	
	/** die Build-Nummer aus <tt>/src/resources/burtool.build</tt> */
	public static String BUILD_NUMBER = null;
	
	/** Schl�ssel f�r Programmfensterbreite */
	public static final String CONFIG_WIDTH_KEY = "bobTool.width";

	/** Schl�ssel f�r Programmfensterh�he */
	public static final String CONFIG_HEIGHT_KEY = "bobTool.height";
	
	/** ein Icon f�rs Programm */
	private static final String PROGRAM_IMAGE = "/resources/gnome-control-center32.png";
	
	/** <code>true</code> wenn Demomodus eingeschaltet */
	private boolean demoActivated = false;
	
	private final LoginManager lm;
	
	private final PluginManager pm;
	
	private final String programName;
	
	public static void startApp(final String title) {
		// Ausnahemfehler protokollieren
		final BobCrashHandler handler = new BobCrashHandler(title);
		Thread.setDefaultUncaughtExceptionHandler(handler);
		// Oberfl�che starten
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Utils.setupNimbus();
				new Main(title);
			}
		});
	}
	
	public Main(final String programName) {
		this.programName = programName;
		// Icon f�r Anwendung
		final ImageIcon icon = 
				new ImageIcon(Main.class.getResource(PROGRAM_IMAGE));
		setIconImage(icon.getImage());
		// Manager starten
		lm = new LoginManager(this);
		pm = new PluginManager();
		// Oberfl�che anzeigen
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setupTitle();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				shutdown();
			}			
		});
		setJMenuBar(new BobMenuBar(this));
		setupSize();
		setVisible(true);
	}
	
	public LoginManager getLoginManager() {
		return lm;
	}
	
	public PluginManager getPluginManager() {
		return pm;
	}
	
	/**
	 * Liefert <code>true</code> wenn der Vorf�hrmodus aktiv ist.
	 * @return <code>true</code> wenn Vorf�hrmodus aktiv
	 */
	public boolean isDemoActivated() {
		return demoActivated;
	}
	
	public boolean isChangeMode() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setupTitle() {
		final StringBuffer sb = new StringBuffer(programName);
		if (demoActivated) {
			sb.append(BobConstants.SPACE).append("[DEMO]");
		}
		if (null != BUILD_NUMBER) {
			sb.append(BobConstants.SPACE).append(BobConstants.MINUS);
			sb.append(BobConstants.SPACE).append("Build");
			sb.append(BobConstants.SPACE).append(BUILD_NUMBER); 
		}
		setTitle(sb.toString());
	}
	
	public void setupSize() {
		final Config cfg = Config.getDefault();
		
		Integer w = cfg.getInteger(CONFIG_WIDTH_KEY);
		w = ((null == w) || (640 > w) ? 640 : w);
		Integer h = cfg.getInteger(CONFIG_HEIGHT_KEY);
		h = ((null == h) || (480 > h) ? 480 : h);
		
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		w = (screen.width > w ? w : (screen.width - 10));
		h = (screen.height > h ? h : (screen.height - 10));
		
		Utils.centerOnScreen(this, w, h);
	}

	/**
	 * Zeigt ein {@link JOptionPane}.
	 * @param msg der Nachrichtentext
	 * @param type der Nachrichtentyp
	 */
	public void showMessage(final String msg, final int type) {
		String title = null;
		switch (type) {
		case JOptionPane.WARNING_MESSAGE:
			title = "Warnung";
			break;
		case JOptionPane.ERROR_MESSAGE:
			title = "Fehler";
			break;
		default:
			title = "Hinweis";
		}
		JOptionPane.showMessageDialog(this, msg, title, type);
	}

	/**
	 * Kontrolliertes Herunterfahren der Anwendung.
	 */
	public void shutdown() {
		// Schwerer Ausnahmefehler?
//		if (!showExceptionBlocker()) {
//			// Bearbeitungsmodus?
//			if (!showChangeableBlocker()) {
//				// Plugin stoppen
//				pluginManager.stopCurrentPlugin();
//				// UI ausmachen
//				if (null != gui) {
//					gui.setVisible(false);
//				}
//				// Config speichern
//				Config.getDefault().save();
				// VM beenden
				System.exit(0);
//			}
//		}
	}

}
