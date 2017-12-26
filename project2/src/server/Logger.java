package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private File log; 
	private String classpath;
	
	
	public Logger() {
		log = new File("log.txt");
		System.out.println(log.getAbsolutePath());
		try {
			log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void printToLog(String line) {
		try (FileWriter fw = new FileWriter(log, true);){
			fw.write(line);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void login(String user, String success) {
		printToLog("\nUser:" + user + " tried to login. Status: " + success);
	}
	
	public void log(String actor, String operation, String subject, String success) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		dateFormat.format(date);
		printToLog(actor + " " + operation + " " + subject + " " + success + " " + dateFormat.format(date));
	}
	
	
}
