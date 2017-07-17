package bob.gadget.csvexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import bur.base.BobConstants;
import bur.base.BobException;
import bur.base.Utils;
import bur.commons.BurConstants;
import bur.commons.BurDatabaseVars;
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
 * <p>
 * Der restricted parameter listet Spaltennamen auf die in der CSV ignoriert
 * werden.
 *
 * @author maik.boettcher@bur-kg.de
 * @author joshua.kornfeld@bur-kg.de
 *
 */
public class CsvTabellenWrite {

	/** Konstante für Exporttrenner */
	public static final String BUREXPTRN_COLUMNNAME = "burexptrn";

	/** das Zahlenformat */
	private static final NumberFormat DECIFORMAT = NumberFormat.getNumberInstance(Locale.GERMAN);

	static {
		DECIFORMAT.setGroupingUsed(false);
	}

	/** der Name der Tabelle oder Sicht */
	private final String tablename;

	/** der Ausgabepfad */
	private final String dateipfad;

	/** die optionale Spalte, die als Filter dient */
	private BurvarFilter filter = null;

	/** die optionale Liste mit Spalten die in der CSV-Datei nicht erscheinen */
	private final Set<String> restricted = new LinkedHashSet<>();

	/** Trenner ausschalten */
	private final boolean burexptrnOff;

	/**
	 * Instanziiert das Objekt.
	 *
	 * @param tablename
	 *            der Name der Tabelle oder Sicht
	 * @param filtername
	 *            der Spaltenname zum optionalen Filter
	 * @param dateipfad
	 *            der Ausgabepfad
	 * @param restricted
	 *            die Spalten, die nicht in der CSV erscheinen dürfen
	 * @throws BobException
	 *             wenn Probleme mit BURVARS-Datenbank
	 */
	public CsvTabellenWrite(final String tablename, final String filtername, final String dateipfad,
			final Set<String> restricted) throws BobException {
		this.tablename = tablename;
		this.dateipfad = dateipfad;
		if (null != filtername) {
			this.filter = new BurvarFilter(filtername);
		}
		if (null != restricted) {
			this.restricted.addAll(restricted);
		}
		this.burexptrnOff = !dateipfad.contains(BUREXPTRN_COLUMNNAME);
	}

	/**
	 * Erstellt und liefert die CSV-Dateien.
	 * 
	 * @return ein Objekt, niemals <code>null</code>
	 * @throws SQLException
	 *             wenn Probleme mit Datenbank
	 * @throws IOException
	 *             wenn Probleme mit Dateisystem
	 * @throws BobException
	 *             wenn Probleme beim Objektaufbau
	 */
	public Map<String, File> writeCsv() throws SQLException, BobException, IOException {
		final Map<String, File> exportfiles = new LinkedHashMap<>();

		CsvTabellenTool.LOG.info("tablename: " + tablename + " dateipfad: " + dateipfad);
		CsvTabellenTool.LOG.info("filter: " + filter);
		CsvTabellenTool.LOG.info("restricted: " + restricted);

		final String sql;
		if (null == filter) {
			sql = String.format("select * from %s", tablename);
		} else {
			sql = String.format("select * from %s where %2$s > ? order by %2$s", tablename, filter.columnname);
		}
		CsvTabellenTool.LOG.info("sql: " + sql);

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		final Map<String, BufferedWriter> writerMap = new HashMap<>();
		final Map<String, CSVPrinter> csvPrinterMap = new HashMap<>();
		final Map<String, AtomicLong> csvCounterMap = new HashMap<>();

		try {
			final ConnectionService cs = ConnectionService.createDefaultService();
			conn = cs.createConnectionStrong(BRERP_ISO_8859_15.class);

			if (null == filter) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
			} else {
				stmt = conn.prepareStatement(sql);
				((PreparedStatement) stmt).setLong(1, filter.letzterWert);
				rs = ((PreparedStatement) stmt).executeQuery();
			}

			Map<String, Integer> columns = null;
			int count = 0;
			final Set<String> warnings = new LinkedHashSet<>();
			while (rs.next()) {
				if (null == columns) {
					final ResultSetMetaData md = rs.getMetaData();
					final int columnCount = md.getColumnCount();
					columns = new LinkedHashMap<>();
					for (int idx = 0; idx < columnCount; idx++) {
						final String columnLabel = md.getColumnLabel(idx + 1);
						final Integer columnType = Integer.valueOf(md.getColumnType(idx + 1));
						columns.put(columnLabel, columnType);
						CsvTabellenTool.LOG
								.info("column[" + idx + "]: " + columnLabel + " (" + columnType.intValue() + " = "
								+ md.getColumnTypeName(idx + 1) + ")");
					}
				}

				String burexptrn = null;

				final List<String> values = new LinkedList<>();
				for (final Entry<String, Integer> e : columns.entrySet()) {
					final String columnLabel = e.getKey();
					final int columnType = e.getValue().intValue();
					final String value;
					if (java.sql.Types.INTEGER == columnType || java.sql.Types.BIGINT == columnType) {
						value = String.valueOf(rs.getLong(columnLabel));
					} else if (java.sql.Types.FLOAT == columnType || java.sql.Types.DECIMAL == columnType) {
						String x = null;
						try {
							x = DECIFORMAT.format(rs.getBigDecimal(columnLabel));
						} catch (final IllegalArgumentException ex) {
							warnings.add(columnLabel + " [" + count + "] = " + rs.getString(columnLabel) + " >> "
									+ ex.getMessage());
						}
						value = x;
					} else if (java.sql.Types.DATE == columnType) {
						String x = null;
						try {
							x = Utils.dateFormat(BurConstants.DEFAULT_DATE_FORMAT, rs.getDate(columnLabel));
						} catch (final IllegalArgumentException ex) {
							warnings.add(columnLabel + " [" + count + "] = " + rs.getString(columnLabel) + " >> "
									+ ex.getMessage());
						}
						value = x;
					} else {
						value = rs.getString(columnLabel);
					}
					if (BUREXPTRN_COLUMNNAME.equalsIgnoreCase(columnLabel)) {
						burexptrn = Utils.trim(value);
					} else {
						if (!restricted.contains(columnLabel)) {
							values.add(Utils.trim(value));
						}
						if (null != filter && columnLabel.equals(filter.columnname)) {
							filter.letzterWert = Long.parseLong(value);
						}
					}

				}

				final String burexptrnKey = (null == burexptrn || burexptrnOff ? "" : burexptrn);

				// wenn BUREXPTRNKEY neu, dann Ausgabe konfigurieren
				if (!csvPrinterMap.containsKey(burexptrnKey)) {
					final String path;
					if (Utils.isNotEmpty(burexptrn)) {
						path = dateipfad.replaceFirst(BUREXPTRN_COLUMNNAME, burexptrn);
					} else {
						path = dateipfad;
					}
					// Dateipfad
					final File file = new File(path);
					exportfiles.put(burexptrnKey, file);
					// Writer
					final BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(file), BobConstants.CHARSET_WINDOWS));
					writerMap.put(burexptrnKey, writer);
					// Printer
					final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(';'));
					csvPrinterMap.put(burexptrnKey, csvPrinter);
					// Überschriften
					final Set<String> columnlabels = new LinkedHashSet<>();
					for (final String k : columns.keySet()) {
						if (!BUREXPTRN_COLUMNNAME.equals(k) && !restricted.contains(k)) {
							columnlabels.add(k);
						}
					}
					csvPrinter.printRecord(columnlabels);
					// Zähler
					csvCounterMap.put(burexptrnKey, new AtomicLong(0l));
				}

				csvPrinterMap.get(burexptrnKey).printRecord(values);
				csvCounterMap.get(burexptrnKey).incrementAndGet();

				if (count % 1000 == 0) {
					printLinesCounter(count + " lines", csvCounterMap, null);
				}
				count++;
			}

			if (null != filter) {
				filter.commit();
			}

			printLinesCounter(count + " rows", csvCounterMap, exportfiles);

			if (0 < warnings.size()) {
				final StringBuffer warn = new StringBuffer("WARNUNGEN:").append(BobConstants.LINE_SEPARATOR_UNIX);
				for (final String x : warnings) {
					warn.append(x).append(BobConstants.LINE_SEPARATOR_UNIX);
				}
				CsvTabellenTool.LOG.warning(warn.toString());
			}

		} finally {
			Utils.close(rs, stmt, conn);
			closeFiles(csvPrinterMap, writerMap);
		}


		return exportfiles;
	}

	private void printLinesCounter(final String start, final Map<String, AtomicLong> csvCounterMap,
			final Map<String, File> exportfiles) {
		StringBuffer linesLoggerText = new StringBuffer(start);
		for (final Entry<String, AtomicLong> e : csvCounterMap.entrySet()) {
			if (null != exportfiles) {
				linesLoggerText.append("\n\t");
			} else {
				linesLoggerText.append(", ");
			}
			final String x = e.getKey();
			if (null != exportfiles) {
				if (exportfiles.containsKey(x)) {
					linesLoggerText.append(exportfiles.get(x).getName());
				} else {
					linesLoggerText.append("+++ keine Datei +++");
				}
			} else {
				linesLoggerText.append(Utils.isEmpty(x) ? "(leer)" : x);
			}
			linesLoggerText.append(" = ");
			linesLoggerText.append(e.getValue().longValue());
		}
		CsvTabellenTool.LOG.info(linesLoggerText.toString());
	}

	private void closeFiles(Map<String, CSVPrinter> csvPrinterMap, Map<String, BufferedWriter> writerMap)
			throws IOException {
		final Set<String> keys = csvPrinterMap.keySet();
		for (final String k : keys) {
			// Printer
			final CSVPrinter csvPrinter = csvPrinterMap.get(k);
			csvPrinter.flush();
			csvPrinter.close();
			// Writer
			final BufferedWriter writer = writerMap.get(k);
			writer.close();
		}
	}

	private class BurvarFilter {

		/** der Spaltenname */
		private final String columnname;

		/** der Schlüssel */
		private final String dbkey;

		/** der Kommentar */
		private final String comment;

		/** der letzte Wert oder {@link Long#MIN_VALUE} */
		private long letzterWert;

		/**
		 * Instanziiert den Filter und erstellt den Datenbankeintrag, wenn er
		 * noch nie verwendet wurde.
		 *
		 * @param filtername
		 *            der Spaltenname
		 * @throws BobException
		 *             wenn Probleme mit Datenbank
		 */
		private BurvarFilter(final String filtername) throws BobException {
			this.columnname = filtername;
			this.dbkey = Utils.maskFileName(String.format("%s_%s", tablename, filtername)).toLowerCase();
			this.comment = String.format("letzter Filterwert für \"%s\" in Spalte \"%s\"", tablename, filtername);
			final BurDatabaseVars vars = BurDatabaseVars.getRefreshed();
			final Long x = vars.searchNumber(dbkey);
			if (null == x) {
				letzterWert = Long.MIN_VALUE;
				vars.createNumber(BurDatabaseVars.BRERP_BURVARS_DB, dbkey, letzterWert, comment);
			} else {
				letzterWert = x.longValue();
			}
		}

		private void commit() throws BobException {
			final BurDatabaseVars vars = BurDatabaseVars.getCached();
			vars.setNumber(dbkey, Long.valueOf(letzterWert), comment);
		}

		@Override
		public String toString() {
			final String x = letzterWert == Long.MIN_VALUE ? "INIT" : String.valueOf(letzterWert);
			return String.format("[%s] = %s", dbkey, x);
		}

	}

}
