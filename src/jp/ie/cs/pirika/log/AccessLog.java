package jp.ie.cs.pirika.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AccessLog extends Formatter{
	protected static final String LOGGING_PROPERTIES = "accessLog.properties";
	
	static {
		File f = new File("."+File.separator+"Log");
		if(!f.exists()){f.mkdirs();}
		
		Logger logger = Logger.getLogger("log");
        logger.fine("Log configuration : " + LOGGING_PROPERTIES);
        InputStream inStream = null;
		try {
			inStream = new FileInputStream(LOGGING_PROPERTIES);
			 try {
	                LogManager.getLogManager().readConfiguration(inStream);
	                logger.config("Log configuration : LogManager was configurated.");
	            } catch (IOException e) {
	                logger.warning("Log configuration : Exception was generated the case of LogManager configuration.:"+ e.toString());
	            } finally {
	                try {
	                    if (inStream != null) inStream.close(); 
	                } catch (IOException e) {
	                    logger.warning("Log configuration : Exception was generated the case of LogManager configuration.:"+ e.toString());
	                }
	            }
		} catch (FileNotFoundException e1) {
			logger.info("Log configuration : " + LOGGING_PROPERTIES + " is not found.");
		}finally{
			try {
				if(inStream!=null){
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	public String format(final LogRecord argLogRecord) {
        final StringBuffer buf = new StringBuffer();
        final SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        buf.append(sdFormat.format(new Date(argLogRecord.getMillis())));
        buf.append(" ");

        if (argLogRecord.getLevel() == Level.FINEST) {
            buf.append("FINEST");
        } else if (argLogRecord.getLevel() == Level.FINER) {
            buf.append("FINER");
        } else if (argLogRecord.getLevel() == Level.FINE) {
            buf.append("FINE");
        } else if (argLogRecord.getLevel() == Level.CONFIG) {
            buf.append("CONFIG");
        } else if (argLogRecord.getLevel() == Level.INFO) {
            buf.append("INFO");
        } else if (argLogRecord.getLevel() == Level.WARNING) {
            buf.append("WARN");
        } else if (argLogRecord.getLevel() == Level.SEVERE) {
            buf.append("SEVERE");
        } else {
            buf.append(Integer.toString(argLogRecord.getLevel().intValue()));
            buf.append(" ");
        }
        buf.append(" ");
        buf.append(argLogRecord.getLoggerName());
        buf.append(": ");
        buf.append(argLogRecord.getMessage());
        buf.append("\n");

        return buf.toString();
    }
	
	public static Logger getLogger(){
		return Logger.getLogger("log");
	}
}
