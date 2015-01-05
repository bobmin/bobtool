package bob.finance;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import bob.api.IToolContext;
import bob.core.BobIcon;
import bob.tool.AbstractPlugin;

/**
 * Finanzwerkzeug zum organisieren von Verträgen und zum erstellen markanter 
 * Übersichten.
 *  
 * @author maik.boettcher@bur-kg.de
 *
 */
public class FinancePlugin extends AbstractPlugin {

	/** die Benutzeroberfläache */
	private FinancePanel topComponent = null;
	
	/**
	 * Instanziiert ein Werkzeug.
	 */
	public FinancePlugin() {
		super("Finanzen verwalten", BobIcon.APPLICATION, 
				"Verträge organisieren und Übersichten generieren.");
	}
	
	@Override
	public JPanel start(final IToolContext context, final int modifiers) {
		topComponent = new FinancePanel();
		return topComponent;
	}

	/**
	 * Benutzeroberfläche vom Finanzwerkzeug.
	 */
	private class FinancePanel extends JPanel {
		
		private final JLabel label;
		
		private final JTable table;
		
		public FinancePanel() {
			super(new BorderLayout(0, 0));
			label = new JLabel("Übersicht Verträge");
			add(label, BorderLayout.NORTH);
			table = new JTable();
			add(new JScrollPane(table), BorderLayout.CENTER);
		}
		
		public void showData() {
			
		}
		
	}

}
