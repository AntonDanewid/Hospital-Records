package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Nurse implements User {
	FileParser parser;
	private Map<String, Integer> patients;
    private Map<String, Integer> records;
	private String division;
	private String divisionFile;
    private String nurseFile;
    private String nurseName;
    private String classpath;
	
	public Nurse(String name) throws FileNotFoundException, IOException{
		classpath = new File("").getAbsolutePath() + "/";
		parser = new FileParser();
		nurseFile = "../../database/accounts/" + name + ".txt";
		this.nurseName = name;
		division = parser.read(classpath + nurseFile, "div");
		divisionFile = "../../database/divisions/" + division + ".txt";
        // Save division records
		records = parser.readPaths(classpath + divisionFile, "records", Protocol.DIVISION_ACCESS);
        // Save nurse records
		HashMap<String, Integer> nurseRecords = parser.readPaths(classpath + nurseFile, "records", Protocol.NURSE_ACCESS);
        records.putAll(nurseRecords);
    }

	@Override
	public String read(String infile) throws FileNotFoundException, IOException, IllegalAccessException {
		infile = "../../database/records/" + infile + ".txt";
		  // Check permissions to read
        if (records.containsKey(infile) && records.get(infile) <= Protocol.DIVISION_ACCESS) {
            // Return content or error message
            return parser.read(classpath + infile, "content");
        }
        throw new IllegalAccessException("You do not have read access to " + infile);
    }

	@Override
	public String create(String patient, String doctor, String nurse, String division, String content) throws IllegalAccessException {
		throw new IllegalAccessException("You do not have create access to " + patient);
	}

	@Override
	public String write(String infile, String content) throws FileNotFoundException, IOException, IllegalAccessException {
		throw new IllegalAccessException("You do not have write access to " + infile);
    }

	@Override
	public String delete(String infile) throws IllegalAccessException {
		throw new IllegalAccessException("You do not have delete access to " + infile);
		
	}

	@Override
	public String getName() {
		return nurseName;
	}
	
	
	
}