package jp.ie.cs.pirika.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Configuration {
	private static Properties props = new Properties();
	private static Configuration config = new Configuration();
	
	public Configuration(){
		this.initialize();
	}
	
	private void initialize() {
		try {
			File configFile = new File("config.properties");
			if (!configFile.exists()) {
				System.err.println("Not Found ConfigFile");
				props.setProperty("semantics_connection_address", "127.0.0.1");
				props.setProperty("semantics_connection_port", "51985");
				props.setProperty("console_message","true");
				props.setProperty("port", "51984");
				FileOutputStream fos = new FileOutputStream(configFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				Date d = new Date();
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				props.store(bos, df.format(d));
				fos.close();
				bos.close();
			}
			FileInputStream fin = new FileInputStream(configFile);
			BufferedInputStream bis = new BufferedInputStream(fin);
			props.load(bis);
			fin.close();
			bis.close();
		} catch (IOException ex) {}
	}
	
	public static void reload(){
		config.initialize();
	}
	
	public static String getString(String key) throws IllegalArgumentException{
		String value = props.getProperty(key);
		if (value == null) 
			throw new IllegalArgumentException("Illegal String key : "+key);
		return value;
	}
	
	public static int getInt(String key) throws IllegalArgumentException{
		String value = props.getProperty(key);
		if (value == null) 
			throw new IllegalArgumentException("Illegal String key : "+key);
		return Integer.valueOf(value);
	}
	
	public static Boolean getBoolean(String key){
		String value = props.getProperty(key);
		if (value == null) 
			return false;
		return Boolean.valueOf(value);
	}
}
