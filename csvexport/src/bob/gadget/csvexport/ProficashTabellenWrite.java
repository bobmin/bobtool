package bob.gadget.csvexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import bur.base.BobConstants;
import bur.base.BobException;
import bur.base.Utils;
import bur.commons.BurConstants;
import bur.commons.ConnectionService;
import bur.commons.ConnectionService.BRERP_ISO_8859_15;

/**
 * Schreibt die CSV zum Export in Proficash.
 * 
 * @author joshuakornfeld
 *
 */
public class ProficashTabellenWrite {

	/** das Zahlenformat */
	private static final NumberFormat DECIFORMAT = NumberFormat.getNumberInstance(Locale.GERMAN);
	private static final String LFDNR_COLUMNNAME = "erechlfdnr";

	static {
		DECIFORMAT.setGroupingUsed(false);
	}

	/** der Ausgabepfad */
	private final String dateipfad;

	public ProficashTabellenWrite(final String dateipfad) {
		this.dateipfad = dateipfad;
	}

	public Map<String, File> writeCsv() throws SQLException, BobException, IOException {
		final Map<String, File> exportfiles = new LinkedHashMap<>();

		final String sql = "select * from brerp..liefnr_op_man_zahlen";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		final Map<String, BufferedWriter> writerMap = new HashMap<>();
		final Map<String, CSVPrinter> csvPrinterMap = new HashMap<>();

		try {
			final ConnectionService cs = ConnectionService.createDefaultService();
			conn = cs.createConnectionStrong(BRERP_ISO_8859_15.class);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

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
					}
				}

				String burexptrn = null;

				final List<String> values = new LinkedList<>();
				for (final Entry<String, Integer> e : columns.entrySet()) {
					final String columnLabel = e.getKey();
					if (!LFDNR_COLUMNNAME.equals(columnLabel)) {
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

						values.add(Utils.trim(value));
					}
				}

				final String burexptrnKey = (null == burexptrn ? "" : burexptrn);

				if (!csvPrinterMap.containsKey(burexptrnKey)) {
					final String path = dateipfad;
					final File file = new File(path);
					exportfiles.put(burexptrnKey, file);
					final BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(file), BobConstants.CHARSET_WINDOWS));
					writerMap.put(burexptrnKey, writer);
					final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(';'));
					csvPrinterMap.put(burexptrnKey, csvPrinter);
					// 1x Spaltenüberschriften
					final Set<String> columnlabels = new LinkedHashSet<>();
					for (final String k : columns.keySet()) {
						if (!LFDNR_COLUMNNAME.equals(k)) {
							columnlabels.add(k);
						}
					}
					csvPrinter.printRecord(columnlabels);
				}

				csvPrinterMap.get(burexptrnKey).printRecord(values);

				count++;
			}
		} finally {
			Utils.close(rs, stmt, conn);
			final Set<String> keys = csvPrinterMap.keySet();
			for (final String k : keys) {
				if (!LFDNR_COLUMNNAME.equals(k)) {
					// Printer
					final CSVPrinter csvPrinter = csvPrinterMap.get(k);
					csvPrinter.flush();
					csvPrinter.close();
					// Writer
					final BufferedWriter writer = writerMap.get(k);
					writer.close();
				}
			}

		}

		return exportfiles;
	}

}
