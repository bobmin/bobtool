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
 * Finanzwerkzeug zum organisieren von Vertr�gen und zum erstellen markanter 
 * �bersichten.
 *  
 * @author maik.boettcher@bur-kg.de
 *
 */
public class FinancePlugin extends AbstractPlugin {

	/** die Benutzeroberfl�ache */
	private FinancePanel topComponent = null;
	
	/**
	 * Instanziiert ein Werkzeug.
	 */
	public FinancePlugin() {
		super("Finanzen verwalten", BobIcon.APPLICATION, 
				"Vertr�ge organisieren und �bersichten generieren.");
	}
	
	@Override
	public JPanel start(final IToolContext context, final int modifiers) {
		topComponent = new FinancePanel();
		return topComponent;
	}

	/**
	 * Benutzeroberfl�che vom Finanzwerkzeug.
	 */
	private class FinancePanel extends JPanel {
		
		private final JLabel label;
		
		private final JTable table;
		
		public FinancePanel() {
			super(new BorderLayout(0, 0));
			label = new JLabel("�bersicht Vertr�ge");
			add(label, BorderLayout.NORTH);
			table = new JTable();
			add(new JScrollPane(table), BorderLayout.CENTER);
		}
		
		public void showData() {
			
		}
		
	}

}
