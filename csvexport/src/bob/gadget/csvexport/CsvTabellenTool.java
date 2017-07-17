package bob.gadget.csvexport;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import bur.base.BobConstants;
import bur.base.BobException;
import bur.base.DateTimeExchange;
import bur.base.StartParams;
import bur.base.Utils;
import bur.commons.ConnectionService;
import bur.commons.ConnectionService.BRERP_ISO_8859_15;

/**
 * Liest die Daten vom SQL-Server und schreibt eine CSV-Datei.
 * <p>
 * Ein Pflichtparameter benennt die Tabelle oder Sicht, die Spaltenüberschriften
 * und Daten liefert.
 * <p>
 * Ein optionaler Parameter definiert einen Spaltennamen der als
 * Abfragebedingung gesetzt werden kann. Die Abfragebedingung ist immer eine
 * Ganzzahl und wird dauerhaft gespeichert. Die nächste Abfrage beachtet nur
 * Datensätze, deren Spaltenwert größer ist (als bei der letzten Ausführung).
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class CsvTabellenTool {

	/** der Logger */
	static final Logger LOG = Logger.getLogger(CsvTabellenWrite.class.getName());

	static {
		LOG.setUseParentHandlers(false);
		final ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new Formatter() {

			/**
			 * Ein einfacher Formatter, der nur die Nachricht ohne Metadaten
			 * ausgibt.
			 */
			@Override
			public String format(final LogRecord record) {
				return record.getMessage() + BobConstants.LINE_SEPARATOR_UNIX;
			}

		});
		LOG.addHandler(ch);
	}

	/** der Zeitpunkt */
	private static final DateTimeExchange _ZEITPUNKT = new DateTimeExchange(new Date());

	/** der Zwischenspeicher für die Dateien */
	private static final Map<String, LinkedList<File>> FILES = new LinkedHashMap<>();

	/**
	 * Das Hauptprogramm.
	 * 
	 * @param args
	 *            die Startparameter
	 * @throws SQLException
	 *             wenn Probleme mit SQL-Server
	 * @throws BobException
	 *             wenn allgemeine Probleme
	 * @throws IOException
	 *             wenn Probleme mit Dateisystem
	 */
	public static void main(String[] args) throws SQLException, BobException, IOException {
		System.out.println("===== START " + _ZEITPUNKT.getDateTime() + " =====");
		final StartParams params = new StartParams(args);

		final String before = params.value("-b");
		if (null != before && 0 < before.length()) {
			runProc(before);
		}

		final String cfgPath = params.value("-c");
		if (null == cfgPath) {
			singleStart(params.value("-t"), params.value("-o"), params.value("-f"));
		} else {
			final File cfgFile = new File(cfgPath);
			if (!cfgFile.canRead()) {
				throw new IllegalArgumentException("[cfg] not exists: " + cfgPath);
			}
			final Reader in = new FileReader(cfgFile);
			final Iterable<CSVRecord> records = CSVFormat.TDF.parse(in);
			for (final CSVRecord record : records) {
				final String t = Utils.trimNull(record.get(0));
				if (!t.startsWith("#")) {
					// Filter-Spaltenname (Ganzzahl zum inkrementellen Export)
					final String f = Utils.trimNull(record.get(1));
					// Zieldatei
					final String o = Utils.trimNull(record.get(2));
					// Gruppendatei (kombiniert mehrere Zieldateien)
					final String g = Utils.trimNull(record.get(3));
					System.out.println("cfg: " + t + " " + (Utils.isEmpty(f) ? "(leer)" : f) + " " + o + " "
							+ (Utils.isEmpty(g) ? "(leer)" : g));
					final Map<String, File> exportfiles = singleStart(t, o, f);
					if (null != g) {
						if (g.contains(CsvTabellenWrite.BUREXPTRN_COLUMNNAME)
								&& !o.contains(CsvTabellenWrite.BUREXPTRN_COLUMNNAME)) {
							throw new IllegalArgumentException("[burexptrn] not in [output] and [group]");
						}
						for (final Entry<String, File> e : exportfiles.entrySet()) {
							final String burexptrn = e.getKey();
							final String path = g.replaceFirst(CsvTabellenWrite.BUREXPTRN_COLUMNNAME, burexptrn);
							if (!FILES.containsKey(path)) {
								FILES.put(path, new LinkedList<File>());
							}
							FILES.get(path).add(e.getValue());
						}
					}
				}
			}

			if (0 < FILES.size()) {
				for (final Entry<String, LinkedList<File>> e : FILES.entrySet()) {
					final String grouppath = _ZEITPUNKT.replacePattern(e.getKey());
					final CsvTabellenMerge merge = new CsvTabellenMerge(grouppath, e.getValue());
					merge.mergeCsv();
				}
			}
		}

		System.out.println("===== ENDE " + _ZEITPUNKT.getDateTime() + " =====");
	}

	/**
	 * Startet die Prozedur.
	 * 
	 * @param name
	 *            der Name der Prozedur
	 * @throws SQLException
	 * @throws BobException
	 */
	private static void runProc(final String name) throws SQLException, BobException {
		Connection conn = null;
		CallableStatement stmt = null;

		try {
			final ConnectionService cs = ConnectionService.createDefaultService();
			conn = cs.createConnectionStrong(BRERP_ISO_8859_15.class);
			stmt = conn.prepareCall(String.format("{call %s(?)}", name));
			stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			stmt.execute();
			final String msg = stmt.getString(1);
			CsvTabellenTool.LOG.info("before[" + name + "]: " + msg);
		} finally {
			Utils.close(null, stmt, conn);
		}
	}

	/**
	 * Startet den Export für eine Tabelle oder Sicht.
	 * 
	 * @param t
	 *            der Tabellen- oder Sichtname
	 * @param o
	 *            der Pfad für die Ausgabedatei
	 * @param f
	 *            der optionale Zählerspaltenname
	 * @return die Ausgabedatei(en) getrennt nach "burexptrn"
	 * @throws BobException
	 * @throws SQLException
	 * @throws IOException
	 */
	private static Map<String, File> singleStart(final String t, final String o, final String f)
			throws BobException, SQLException, IOException {
		final String tablename = t;
		final String dateipfad = o;
		if (null == tablename || null == dateipfad) {
			throw new IllegalArgumentException("usage: -t=TABELLE [-f=SPALTE] -o=PFAD");
		}
		final String path = _ZEITPUNKT.replacePattern(dateipfad);
		final File file = new File(path);
		if (!file.getParentFile().exists()) {
			throw new IllegalArgumentException("[folder] not exists: " + file.getParent());
		}
		if (file.exists()) {
			throw new IllegalArgumentException("[file] exists: " + file.getAbsolutePath());
		}
		final String filtername = f;
		final CsvTabellenWrite x = new CsvTabellenWrite(tablename, filtername, file.getAbsolutePath(), null);
		return x.writeCsv();
	}

}
