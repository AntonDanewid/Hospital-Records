package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestDoctor {

	private static String recordName() {
		String counterPath = "../database/records/patient_1/counter.txt";
		try (BufferedReader br = new BufferedReader(new FileReader(counterPath))) {
			String counterNum = br.readLine();
			return "../database/records/patient_1/r" + counterNum + ".txt";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "../database/records/patient_1/r1.txt";
	}

	public static void main(String[] args) {
		FileParser fp = new FileParser();
		String doctorPath = "../database/accounts/Doctor1.txt";
		String nursePath = "../database/accounts/nurse1.txt";
		String divPath = "../database/divisions/div1.txt";
		String govPath = "../database/government_agency/all_records.txt";
		Doctor doctor;

		try {
			doctor = new Doctor("Doctor1");

				String infile = "patient_1/r0";
				String infile2 = "patient_1/r49";
				String content = "Testing doctor class at time: " + System.currentTimeMillis();
				String patient = "patient_1";
			String doctorName = "Doctor1";
				String nurse = "nurse1";
				String division = "div1";
				String recordPath = recordName();

				// Read
				System.out.println("Testing read()");
				try {
					System.out.println(doctor.read(infile));

					System.out.println("Read successful");

					// Write
					System.out.println("Testing write()");
					System.out.println(doctor.write(infile, content));
					System.out.println("Content in file: " + infile);
					System.out.println(doctor.read(infile));
					System.out.println("Write successful");

					// Create
					System.out.println("Testing create");
					System.out.println(doctor.create(patient, doctorName, nurse, division, content));
					System.out.println("Content in file: " + recordPath);
					System.out.println(doctor.read(recordPath));
					System.out.println("record paths in file: " + doctorPath);
					System.out.println(fp.read(doctorPath, "records"));
					System.out.println("record paths in file: " + nursePath);
					System.out.println(fp.read(nursePath, "records"));
					System.out.println("record paths in file: " + divPath);
					System.out.println(fp.read(divPath, "records"));
					System.out.println("record paths in file: " + govPath);
					System.out.println(fp.read(govPath, "records"));
					System.out.println("Create successful!");

					// Delete
					System.out.println("Testing delete");
					System.out.println(doctor.delete(recordPath));

					System.out.println("Testing Government Agency");
					GovernmentAgency ga = new GovernmentAgency();

					// Read
					System.out.println("Testing read()");
					System.out.println(ga.read(infile2));
					System.out.println("Read successful");

					// Create
					System.out.println("Testing create");
					System.out.println(ga.create(patient, doctorName, nurse, division, content));

					// Write
					System.out.println("Testing write()");
					System.out.println(ga.write(infile2, content));

					// Delete
					System.out.println("Testing delete");
					System.out.println(ga.delete("../database/records/patient_1/r49.txt"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
