package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class GovernmentAgency implements User {
	private FileParser parser;
	private Map<String, Integer> records;
	private String gaFile;
	private String classpath;
	
	public GovernmentAgency() throws FileNotFoundException, IOException {
		classpath = new File("").getAbsolutePath() + "/";
		gaFile = classpath + "../../database/government_agency/" + "all_records"+ ".txt";
	
		parser = new FileParser();
		records = parser.readPaths(gaFile, "records", Protocol.GOVERNMENT_ACCESS);
	}

	@Override
	public String read(String infile) throws IllegalAccessException, FileNotFoundException, IOException {
		infile = "../../database/records/" + infile + ".txt";
		// Check permissions to read
	
		if (records.containsKey(infile) && records.get(infile) <= Protocol.GOVERNMENT_ACCESS) {
			// Return content or error message
			return parser.read(infile, "content");
		}
		throw new IllegalAccessException("You do not have reader access to " + infile);
	}

	@Override
	public String create(String person, String doctor, String nurse, String division, String content) throws IllegalAccessException {
		throw new IllegalAccessException("You do not have create access to " + person);

	}

	@Override
	public String write(String infile, String content) throws IllegalAccessException {
		throw new IllegalAccessException("You do not have write access to " + infile);

	}

	@Override
	public String delete(String infile) throws IOException {
		infile = "../../database/records/" + infile + ".txt";
		parser.delete(classpath, infile);
		return "Deleted";
		
	}

	@Override
	public String getName() {
		return "Gov.gov";
	}

}