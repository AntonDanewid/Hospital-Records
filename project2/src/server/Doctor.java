package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
public class Doctor implements User {
	private Map<String, Integer> patients;
	private Map<String, Integer> records;
	private String division;
	private String divisionFile;
	private String doctorFile;
	private String doctorName;
	private FileParser parser;
	private String abspath;
	private String classpath;

	public Doctor(String name) throws FileNotFoundException, IOException {
		parser = new FileParser();
		
		classpath = new File("").getAbsolutePath() + "/";
		
		doctorFile = "../../database/accounts/" + name + ".txt";
		doctorName = name;
		// Save doctor division
		division = parser.read(classpath + doctorFile, "div");
		divisionFile = "../../database/divisions/" + division + ".txt";
		// Save patients
		patients = parser.readPaths(classpath + doctorFile, "patients", Protocol.DOCTOR_ACCESS);
		// Save division records
		records = parser.readPaths(classpath + divisionFile, "records", Protocol.DIVISION_ACCESS);

		// Save doctor records
		HashMap<String, Integer> docRecords = parser.readPaths(classpath + doctorFile, "records", Protocol.DOCTOR_ACCESS);
		
		records.putAll(docRecords);
		
	}

	public String read(String infile) throws FileNotFoundException, IOException, IllegalAccessException {
		infile = "../../database/records/" + infile + ".txt"; //      patient1/r0
		// Check permissions to read
		if (records.containsKey(infile) && records.get(infile) <= Protocol.DIVISION_ACCESS) {
			// Return content or error message
			return parser.read(classpath + infile, "content");
		}
		throw new IllegalAccessException("You do not have read access to " + infile);
	}

	public String write(String infile, String content) throws FileNotFoundException, IOException, IllegalAccessException {
		String filepath = "../../database/records/" + infile + ".txt";
		// Write content to infile, or block
		if (records.containsKey(filepath) && records.get(filepath) == Protocol.DOCTOR_ACCESS) {
			parser.write(filepath, "content", content);
			return "Write complete!";
		}
		throw new IllegalAccessException("You do not have write access to " + infile);
	}

	public String create(String patient, String doctor, String nurse, String division, String content) throws IllegalAccessException {
		String patientPath = "../../database/records/" + patient + "/";
		if (patients.containsKey(patientPath)) {
			// Create file
			String title = "patient";
			String recordPath = parser.createRecord(classpath, patientPath, patient, title, doctorName, nurse, division, content);
			addPath(recordPath);
			// Write permissions
			// Write content to file
			return "Created record for " + patient;
		}
		throw new IllegalAccessException("You do not have create access to " + patient);
	}

	private void addPath(String recordPath) {
		String section = "records";

		try {
			// This doctor
			parser.appendPath(doctorFile, section, recordPath);
			records.put(recordPath, Protocol.DOCTOR_ACCESS);
			// Assigned nurse
			String nurse = parser.read(recordPath, "nurse");
			parser.appendPath(classpath + "../../database/accounts/" + nurse + ".txt", section, recordPath);
			// Div
			String division = parser.read(recordPath, "div");
			parser.appendPath(classpath + "../../database/divisions/" + division + ".txt", section, recordPath);
			// Government Agency
			parser.appendPath(classpath + "../../database/government_agency/all_records.txt", section, recordPath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	public String delete(String infile) throws FileNotFoundException, IOException, IllegalAccessException{
		throw new IllegalAccessException("You do not have delete access to " + infile);
	}

	@Override
	public String getName() {
		return doctorName;
	}

}