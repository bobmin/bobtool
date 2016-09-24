package bob;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import bob.forms.editor.FormsEditorPlugin;

/**
 * Die Anwendung startet das Werkzeug ohne Framework in einer minimalen Fassung.
 * 
 * @author maik@btmx.net
 *
 */
public class Main extends JFrame {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new Main().setVisible(true);
			}
		});
	}

	public Main() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		final FormsEditorPlugin plugin = new FormsEditorPlugin();

		final JMenu jMenu = new JMenu("Aufgaben");
		final Set<Action> actions = plugin.getActions();
		for (Action a : actions) {
			jMenu.add(new JMenuItem(a));
		}
		final JMenuBar jMenuBar = new JMenuBar();
		jMenuBar.add(jMenu);
		setJMenuBar(jMenuBar);

		final JPanel topComponent = plugin.start();
		getContentPane().add(topComponent);

		final int width = 780, height = 590;

		final Point point = new Point();
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		point.x = (int) ((screen.width - width) * 0.5f);
		point.y = (int) ((screen.height - height) * 0.5f);

		setBounds(point.x, point.y, width, height);

	}

}
