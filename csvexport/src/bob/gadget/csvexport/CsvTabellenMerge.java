package bob.gadget.csvexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import bur.base.BobConstants;
import bur.base.Utils;

/**
 * Fügt mehrere Dateien zu einer goßen Datei zusammen.
 * 
 * @author maik.boettcher@bur-kg.de
 *
 */
public class CsvTabellenMerge {

	/** den Zielpfad */
	private final String path;

	/** die Dateien werden zusammengeführt */
	private final List<File> files;

	public CsvTabellenMerge(final String path, List<File> files) {
		this.path = path;
		this.files = files;
	}

	/**
	 * Führt die Dateien zusammen und erstellt eine neue Datei.
	 * 
	 * @throws IOException
	 */
	public void mergeCsv() throws IOException {
		final File groupFile = new File(path);

		if (groupFile.exists()) {
			throw new IllegalArgumentException("[file] exists: " + groupFile.getAbsolutePath());
		}

		CsvTabellenTool.LOG.info("group: " + groupFile.getAbsolutePath());

		BufferedWriter writer = null;
		CSVPrinter csvPrinter = null;

		InputStreamReader one = null;

		try {

			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(groupFile), BobConstants.CHARSET_WINDOWS));
			csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(';'));

			boolean firstHeader = true;
			long line = 0;

			for (final File f : files) {
				one = new InputStreamReader(new FileInputStream(f), BobConstants.CHARSET_WINDOWS);
				final Iterable<CSVRecord> oneRecords = CSVFormat.EXCEL.withDelimiter(';').parse(one);
				for (final CSVRecord r : oneRecords) {
					if (firstHeader || 0 < line) {
						csvPrinter.printRecord(r);
					}
					line++;
				}
				CsvTabellenTool.LOG
						.info("+file = " + f.getAbsolutePath() + ", header = " + firstHeader + ", lines = " + line);
				line = 0;
				firstHeader = false;
			}

		} finally {
			Utils.close(one);
			if (null != csvPrinter) {
				csvPrinter.close();
			}
			Utils.close(writer);
		}

	}

}
