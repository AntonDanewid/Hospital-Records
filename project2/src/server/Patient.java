package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Patient implements User{
    private HashMap<String, Integer> records;
    private String patientFolder;
    private String recordFolder;
    private String patientName;
    private FileParser parser;
    private String classpath;

    public Patient(String name) {
    	classpath = new File("").getAbsolutePath() + "/";
        parser = new FileParser();
        patientName = name;
        patientFolder = "../../database/records/" + name + "/";
        recordFolder = "../../database/records/";
        records = listFiles(classpath + patientFolder);
    }
	
	@Override
	public String read(String infile) throws FileNotFoundException, IOException, IllegalAccessException {
		infile = recordFolder + infile + ".txt";
		 // Check permissions to read
        if (records.containsKey(infile) && records.get(infile) == Protocol.PATIENT_ACCESS) {
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
	public String write(String infile, String content) throws IllegalAccessException {
		throw new IllegalAccessException("You do not have write access to " + infile);
	}

	@Override
	public String delete(String infile) throws IllegalAccessException {
		throw new IllegalAccessException("You do not have delete access to " + infile);
		
	}
	
	private HashMap<String, Integer> listFiles(String directoryName){
		HashMap<String, Integer> fileNameMap = new HashMap<String, Integer>();
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && file.getName() != "counter"){
                fileNameMap.put(patientFolder + file.getName(), Protocol.PATIENT_ACCESS);
            }
        }
        return fileNameMap;
    }



	@Override
	public String getName() {
		return patientName;
	}

}
