package bob.forms.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;

/**
 * Ein Werkzeug für die FORMS-Bearbeitung.
 * 
 * @author maik@btmx.net
 *
 */
public class FormsEditorPlugin {

	/** die Aufgaben vom Werkzeug */
	private Set<Action> actions = null;

	/** die Benutzeroberfläche */
	private PluginPanel topComponent = null;

	/** der aktuelle Baustein wird bearbeitet */
	private IBlockDao blockDao = null;

	public Set<Action> getActions() {
		if (null == actions) {
			actions = new LinkedHashSet<>();
			actions.add(new ChangeBlock());
		}
		return actions;
	}

	public JPanel start() {
		topComponent = new PluginPanel();
		return topComponent;
	}

	public JPanel getTopComponent() {
		return topComponent;
	}

	public class PluginPanel extends JPanel {

		private final FormsEditorPanel contentPane;

		private PluginPanel() {
			super(new BorderLayout());
			contentPane = new FormsEditorPanel();
			add(contentPane, BorderLayout.CENTER);
		}

	}

	public class ChangeBlock extends AbstractAction {

		public ChangeBlock() {
			putValue(NAME, "Baustein wechseln");
			putValue(SHORT_DESCRIPTION, "Wechselt den Baustein zur Bearbeitung.");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("go...");
		}

	}

}
