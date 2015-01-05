package bob.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Protokolliert unerwartete Programmabstürze.
 * 
 * @author maik@btmx.net
 *
 */
public class BobCrashHandler implements UncaughtExceptionHandler {

	/** das Protokoll */
    public static final File BOB_CRASH_LOG = 
    		new File(System.getProperty("user.home"), "bobcrash.log");
	
    /** der Programmbezug */
    private final String program;
    
    /**
     * Erstellt eine Instanz und übernimmt den Programmbezug für spätere 
     * Ausgaben.
     * @param program der Programmbezug sollte eindeutig sein
     */
    public BobCrashHandler(final String program) {
    	this.program = program;
	}
    
	@Override
	public void uncaughtException(final Thread th, final Throwable ex) {
		buildReport(th, ex);
	}

	private void buildReport(final Thread th, final Throwable ex) {
		final StringBuilder sb = new StringBuilder();
		// CRASH REPORT FOR BURTOOL Thread[AWT-EventQueue-0,6,main]
        sb.append("CRASH REPORT FOR BOBTOOL").append(BobConstants.SPACE);
        sb.append(th).append(BobConstants.CR);
        // CREATED ON 2014/02/18 11:05:13
        sb.append("CREATED ON").append(BobConstants.SPACE);
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sb.append(df.format(new Date())).append(BobConstants.CR);
        // BobTool Build 1234, JRE 1.7.0_51, OS Windows 7_6.1 amd64
        sb.append(program).append(BobConstants.COMMA).append(BobConstants.SPACE);
        final String jre = System.getProperty("java.version");
        sb.append("JRE").append(BobConstants.SPACE).append(jre);
        sb.append(BobConstants.COMMA).append(BobConstants.SPACE);
        final String os = System.getProperty("os.name");
        sb.append("OS").append(BobConstants.SPACE).append(os);
        sb.append(BobConstants.UNDERLINE).append(System.getProperty("os.version"));
        sb.append(BobConstants.UNDERLINE).append(System.getProperty("os.arch"));
        sb.append(BobConstants.CR).append(BobConstants.CR);
        // java.util.IllegalFormatConversionException: d != java.lang.String
    	// 		at java.util.Formatter$FormatSpecifier.failConversion...
    	//		at java.util.Formatter$FormatSpecifier.printInteger...
    	//		at java.util.Formatter$FormatSpecifier.print...
        final StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));        
        sb.append(writer.toString());
        sb.append(BobConstants.CR);
        // System.err
        System.err.println(sb.toString());
        // Protokollausgabe
        try {
        	Utils.writeTextFile(BOB_CRASH_LOG.getAbsolutePath(), 
        			sb.toString(), BobConstants.CHARSET_WINDOWS, null, true);
        } catch (final IOException ex2) {
            ex2.printStackTrace();
        }
	}

}
