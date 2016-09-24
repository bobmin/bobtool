package bob.forms.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

/**
 * Die Benutzeroberfläche zeigt links eine Auswahlliste und rechts Eigenschaften
 * zur jeweiligen Auswahl.
 * 
 * @author maik@btmx.net
 *
 */
public class FormsEditorPanel extends JPanel {

	public FormsEditorPanel() {
		super(new BorderLayout());
		// die Auswahlliste
		final JTable jTable = new JTable();
		// die Eigenschaften
		final JPanel jPanel = new JPanel();
		// alles zusammen
		final JSplitPane jSplitPane = new JSplitPane();
		jSplitPane.setLeftComponent(new JScrollPane(jTable));
		jSplitPane.setRightComponent(jPanel);
		add(jSplitPane, BorderLayout.CENTER);
	}

}
