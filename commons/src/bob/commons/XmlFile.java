package bob.commons;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Eine XML-Datei mit benannten Texten. Die Verwendung der Texte wird im
 * jeweligen Anwendungskontext bestimmt.
 * 
 * @author maik@btmx.net
 *
 */
public class XmlFile {

	/** der Logger */
	private static final Logger LOG = Logger.getLogger(XmlFile.class.getName());

	/**
	 * Erstellt ein Objekt aus den aktuellen Ressourcen.
	 * 
	 * @param path
	 *            der Pfad zum Objekt
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws FileNotFoundException
	 *             wenn Pfad unbekannt
	 */
	public static XmlFile createResource(final String path) throws FileNotFoundException {
		if (null == XmlFile.class.getResourceAsStream(path)) {
			throw new FileNotFoundException("[path] unknown: " + path);
		}
		return new XmlFile(path);
	}

	/** der Pfad zur XML-Datei */
	private final String path;

	/**
	 * Geschützter Konstruktor. Statische Methoden zur Instanziierung
	 * vorgesehen.
	 */
	private XmlFile(final String path) {
		this.path = path;
	}

	public static void parseXml(final InputStream in,
			final ContentHandler handler, final boolean loadExternalDtd)
			throws SAXException, IOException {
		Objects.requireNonNull(in);
		final XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", loadExternalDtd);
		final InputSource inputSource = new InputSource(in);
		reader.setContentHandler(handler);
		reader.parse(inputSource);
	}

	/**
	 * Liefert den Wert zum eindeutigen Schlüssel.
	 * 
	 * @param id
	 *            der Schlüssel
	 * @return ein Objekt oder <code>null</code>
	 */
	public XmlValue getValue(final String id) {
		Objects.requireNonNull(id);
		final InputStream in = XmlFile.class.getResourceAsStream(path);
		final Handler handler = new Handler(id);
		try {
			parseXml(in, handler, false);
		} catch (IOException | SAXException ex) {
			LOG.log(Level.SEVERE, "FILE IS CORRUPT", ex);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (final IOException ex) {
				}
			}
		}
		return handler.getXmlValue();
	}

	/**
	 * Verarbeitet die Einträge der XML-Datei und findet - falls vorhanden - den
	 * gesuchten Eintrag.
	 */
	private class Handler extends DefaultHandler {

		/** der Schlüssel wird gesucht */
		private final String search;

		/** die optionale Beschreibung */
		private String desc = null;

		/** der Wert zum Schlüssel */
		private CharArrayWriter contents = null;

		/** die Abbruchbedingung */
		private boolean finish = false;

		/**
		 * Instanziiert das Objekt mit dem Schlüssel, der gesucht werden soll.
		 * 
		 * @param id
		 *            der Schlüssel wird gesucht
		 */
		public Handler(final String id) {
			this.search = id;
		}

		/**
		 * Liefert einen Wert (ein Objekt), wenn der Schlüssel in der XML-Datei
		 * gefunden wurde.
		 * 
		 * @return ein Objekt oder <code>null</code>
		 */
		public XmlValue getXmlValue() {
			if (finish) {
				return new XmlValue(search, Objects.toString(contents), desc);
			}
			return null;
		}

		@Override
		public void startElement(final String uri, final String localName, final String qName,
				final Attributes atts) throws SAXException {
			if (!finish && localName.equals("xmlvalue")) {
				final String id = atts.getValue("id");
				if (search.equals(id)) {
					desc = atts.getValue("desc");
					contents = new CharArrayWriter();
				}
			}
		}

		@Override
		public void characters(final char[] ch, final int start, final int length)
				throws SAXException {
			try {
				if (null != contents && (!finish)) {
					String str = new String(ch, start, length);
					str = str.replaceAll("\n", " ");
					contents.write(str);
				}
			} catch (final IOException ex) {
				throw new SAXException(ex);
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName)
				throws SAXException {
			if (localName.equals("xmlvalue") && null != contents) {
				finish = true;
			}
		}

	}

	/**
	 * Beschreibt einen benannten Wert.
	 */
	public class XmlValue {

		/** der Schlüssel */
		private final String id;

		/** der Wert zum Schlüssel */
		private final String content;

		/** die optionale Beschreibung */
		private final String desc;

		/**
		 * Instanziiert das Objekt.
		 * 
		 * @param id
		 *            der Schlüssel
		 * @param content
		 *            der Wert zum Schlüssel
		 * @param desc
		 *            die optionale Beschreibung
		 */
		private XmlValue(
				final String id, final String content, final String desc) {
			Objects.requireNonNull(id);
			this.id = id;
			this.content = content;
			this.desc = desc;
		}

		/**
		 * Liefert den Schlüssel.
		 * 
		 * @return eine Zeichenkette, niemals <code>null</code>
		 */
		public String getId() {
			return id;
		}

		/**
		 * Liefert den Wert zum Schlüssel.
		 * 
		 * @return eine Zeichenkette oder <code>null</code>
		 */
		public String getContent() {
			return content;
		}

		/**
		 * Liefert die optionale Beschreibung.
		 * 
		 * @return eine Zeichenkette oder <code>null</code>
		 */
		public String getDesc() {
			return desc;
		}

	}

}
