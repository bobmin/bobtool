package bob.core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Bibliothek mit oft verwendeten Funktionen.
 * 
 * @author maik@btmx.net
 *
 */
public class Utils {
	
	/** ein Logger */
	private static final Logger LOG = Logger.getLogger(Utils.class.getName());

	/**
     * Öffnet eine bestehende oder erstellt eine neue Textdatei und schreibt den 
     * Text hinein. Mit {@code append} wird festgelegt, ob eine vorhandene Datei 
     * ergänzt oder überschrieben wird.
     * <p>
     * Mit {@code charset} und {@code lineseparator} wird die Formatierung der
     * Ausgabe bestimmt. Wird nichts übergeben, greifen die Einstellungen vom 
     * Betriebssystem.
     * @param path der Dateipfad zur Textdatei
     * @param text der Inhalt für die Textdatei
     * @param charset Zeichensatz für die Textdatei
     * @param lineseparator Zeilenende für die Textdatei
     * @param append <code>true</code> erweitert vorhandene Dateien,
     * 					<code>false</code> überschreibt bestehende Dateien
     * @throws IOException wenn Probleme mit Dateisystem
     */
    public static void writeTextFile(final String path, final String text, 
    		final String charset, final String lineseparator, 
    		final boolean append) throws IOException {
    	
    	if ((null != path) && (null != text)) {
    		FileOutputStream out = null;
    		OutputStreamWriter os = null;
    		
    		try {
	    		out = new FileOutputStream(path, append);
				if (null == charset) {
					os = new OutputStreamWriter(out);
				} else {
					os = new OutputStreamWriter(out, charset);
				}
				
				final BufferedReader in = 
						new BufferedReader(new StringReader(text));
				
				final String ls;
				if (null == lineseparator) {
					ls = System.getProperty("line.separator");
				} else {
					ls = lineseparator;
				}
				
				String line;
				while((line = in.readLine()) != null) {
					os.write(line);
					os.write(ls);
				}
				os.flush();

				LOG.fine("file written, path = " + path 
						+ ", charset = " + charset
						+ ", lineseparator = " + maskLFCR(ls)
						+ ", append = " + Boolean.toString(append)
						+ ", content = " + maskLFCR(text));
    		} finally {
    			close(out);
    			close(os);
    		}
			
    	}
    }
    
    /**
     * Maskiert Carriage-Return und Line-Feed. Das Ergebnis entspricht einer
     * einzeiligen Zeichenkette. Wird NULL übergeben, wird auch NULL geliefert.
     * @param str eine mehrzeilige Zeichenkette
     * @return eine einzeilige Zeichenkette oder NULL
     */
    public static String maskLFCR(final String str) {
		String line = null;
		if (null != str) {
			line = str.replaceAll("\\r", "\\\\r").replaceAll("\\n", "\\\\n");
		}
		return line;
	}
    
    public static void close(final Writer writer) {
		if (null != writer) {
			try {
				writer.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    /**
	 * Schließt <tt>out</tt> ohne Fehlermeldung, wenn es nichts zu schließen 
	 * gibt oder dabei ein Ausnahmefehler geworfen wird.
	 * @param out
	 */
	public static void close(final OutputStream out) {
		if (null != out) {
			try {
				out.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sucht und aktiviert das Look-And-Feel mit dem Namen "Nimbus". 
	 * Kann das Look-And-Feel nicht gesetzt werden, wird <code>false</code> 
	 * geliefert.  
	 * @return <code>true</code> wenn das Look-And-Feel gesetzt wurde
	 */
	public static boolean setupNimbus() {
		try {
			for (final LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					return true;
				}
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Liefert den linken oberen Eckpunkt vom Rechteck, welches mittig auf 
	 * dem Bildschirm platziert werden soll und durch <tt>width</tt> und 
	 * <tt>height</tt> definiert ist. 
	 * @param width
	 * @param height
	 * @return ein Punkt, niemals NULL
	 */
	public static Point centerOnScreen(final int width, final int height) {
		final Point point = new Point();
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		point.x = ((screen.width / 2) - (width / 2));
		point.y = ((screen.height / 2) - (height / 2));		
		return point;
	}
	
	/**
	 * Platziert <tt>frame</tt> mit der durch <tt>width</tt> und <tt>height</tt> 
	 * vorgegebenen Größe mittig auf dem Bildschirm.
	 * @param frame
	 * @param width
	 * @param height
	 */
	public static void centerOnScreen(
			final JFrame frame, final int width, final int height) {
		final Point p = centerOnScreen(width, height);
		frame.setBounds(p.x, p.y, width, height);
	}

	/**
	 * Platziert <tt>dialog</tt> mit der durch <tt>width</tt> und <tt>height</tt> 
	 * vorgegebenen Größe mittig auf dem Bildschirm.
	 * @param dialog
	 * @param width
	 * @param height
	 */
	public static void centerOnScreen(final JDialog dialog, final int width, final int height) {
		final Point p = centerOnScreen(width, height);
		dialog.setBounds(p.x, p.y, width, height);
	}

}
