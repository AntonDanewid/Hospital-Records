package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class TestFileParser {

	public static void main(String[] args) {
		FileParser fp = new FileParser();
		String patient = "patient_1";
		String patientPath = "../database/records/" + patient + "/";
		String title = "patient";
		String doctor = "doctor1";
		String nurse = "nurse1";
		String division = "div1";
		String content = "title:\ndoctor!\nJag kommer i fred!\n";
		String recordPath = fp.createRecord(patientPath, patient, title, 
				doctor, nurse, division, content);
		System.out.println(recordPath + "\n");
		System.out.println("Fixed createFile\n");
		
		//fp.read()
		String title_content;
		try {
			title_content = fp.read(recordPath, "title") + "\n";
		
		title_content += fp.read(recordPath, "name")+ "\n";
		title_content += fp.read(recordPath, "doctor") + "\n";
		title_content += fp.read(recordPath, "nurse") + "\n";
		title_content += fp.read(recordPath, "div") + "\n";
		title_content += fp.read(recordPath, "content") + "\n";
		System.out.println(title_content);
		String doctor_content = fp.read(recordPath, "doctor");
		System.out.println("doctor: " + doctor_content + ".txt\n" + doctor_content.length());
		System.out.println("Read works!");
		
		//readPaths()
		System.out.println("records reading:");
		String doctorPath = "../database/accounts/doctor1.txt";
		HashMap<String, Integer> pathMap = 
				fp.readPaths(doctorPath, "records", Protocol.DOCTOR_ACCESS);
		for (String key : pathMap.keySet()) {
			System.out.println(key);
		}
		System.out.println("patient reading:");
		HashMap<String, Integer> patientsMap =
				fp.readPaths(doctorPath, "patients", Protocol.DOCTOR_ACCESS);
		for (String key : patientsMap.keySet()) {
			System.out.println(key);
		}
		
		//write()
		String new_content = "This is my new message:\n OBEY ME!!\n\n";
		fp.write(recordPath, "content", new_content);
		
		//appendPath()
		fp.appendPath(doctorPath, "records", recordPath);
		System.out.println("appendPath works!");
		
		//deleteFile()
		try {
			fp.delete(recordPath);
		} catch (FileNotFoundException e) {
			
		}
		
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
