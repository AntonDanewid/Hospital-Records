package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class FileParser {

	public FileParser() {

	}

	public String read(String infile, String section) throws FileNotFoundException, IOException {
		LinkedList<String> textList = fileToList(infile);
		Iterator<String> itr = textList.iterator();
		StringBuilder sb = new StringBuilder();
		String line;

		while (itr.hasNext()) {
			line = itr.next();
			if (line.startsWith(section)) {
				while (itr.hasNext() && (line = itr.next()).startsWith("&")) {
					sb.append(line.substring(1));
				}
				break;
			}
		}
		String ans = sb.toString();
		while (ans.endsWith("\n")) {
			ans = ans.substring(0, ans.length() - 1);
		}
		return ans;
	}

	public HashMap<String, Integer> readPaths(String infile, String section, Integer accessRights) throws FileNotFoundException, IOException {
		LinkedList<String> textList = fileToList(infile);
		Iterator<String> itr = textList.iterator();
		HashMap<String, Integer> pathMap = new HashMap<String, Integer>();
		String line;

		while (itr.hasNext()) {
			line = itr.next();
			if (line.startsWith(section)) {
				while (itr.hasNext() && (line = itr.next()).startsWith("&")) {
					pathMap.put(line.substring(1).trim(), accessRights);
				}
			}
		}
		return pathMap;
	}

	public void write(String infile, String section, String content) throws FileNotFoundException, IOException {
		LinkedList<String> fileContent = fileToList(infile);
		Iterator<String> itr = fileContent.iterator();
		StringBuilder sb = new StringBuilder();
		String line;

		while (itr.hasNext()) {
			line = itr.next();
			sb.append(line);
			if (line.startsWith(section)) {
				while ((line = itr.next()).startsWith("&"));
				sb.append(format(content));
				sb.append(line);
			}
		}
		String modifiedContent = sb.toString();
		writeToFile(infile, modifiedContent);
	}

	private String format(String content) {
		BufferedReader br = new BufferedReader(new StringReader(content));
		StringBuilder sb = new StringBuilder();
		String line2;
		try {
			while ((line2 = br.readLine()) != null) {
				sb.append("&" + line2);
				sb.append(System.lineSeparator());
			}
		} catch (IOException e) {
		}
		return sb.toString();
	}

	public String createRecord(String classpath, String patientPath, String name, String title, String doctor, String nurse,
			String division, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("title: \n&" + title + "\n");
		sb.append("name: \n&" + name + "\n");
		sb.append("doctor: \n&" + doctor + "\n");
		sb.append("nurse: \n&" + nurse + "\n");
		sb.append("div: \n&" + division + "\n");
		sb.append("content: \n" + format(content) + "\n");

		if (!(new File(classpath + patientPath + "counter.txt").isFile())) {
			writeToFile(classpath + patientPath + "counter.txt", "0");
		}

		try (BufferedReader br = new BufferedReader(new FileReader(classpath + patientPath + "counter.txt"))) {
			String counterNumber;
			counterNumber = br.readLine();
			String outfile = patientPath + "r" + counterNumber + ".txt";
			String fileContent = sb.toString();
			writeToFile(classpath + outfile, fileContent);

			int counter = Integer.parseInt(counterNumber) + 1;
			writeToFile(classpath + patientPath + "counter.txt", Integer.toString(counter));
			return outfile;
		} catch (IOException e) {

		}
		return "Error. Could not create file!";
	}

	private LinkedList<String> fileToList(String infile) throws FileNotFoundException, IOException {
		LinkedList<String> textList = new LinkedList<String>();
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = br.readLine();

		while (line != null) {
			textList.add(line + "\n");
			line = br.readLine();
		}
		return textList;
	}

	private void writeToFile(String infile, String content) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(infile), "utf-8"))) {
			writer.write(content);
		} catch (IOException e) {
		}
	}

	public void delete(String classpath, String infile) throws IOException {
		String doctor = classpath + "../../database/accounts/" + read(classpath + infile, "doctor") + ".txt";
		String nurse = classpath + "../../database/accounts/" + read(classpath + infile, "nurse") + ".txt";
		String div = classpath + "../../database/divisions/" + read(classpath + infile, "div") + ".txt";
		String gov = classpath + "../../database/government_agency/all_records.txt";
		deletePath(doctor, "records", infile);
		deletePath(nurse, "records", infile);
		deletePath(div, "records", infile);
		deletePath(gov, "records", infile);
		File file = new File(classpath + infile);
		file.delete();
	}

	public void appendPath(String infile, String section, String filepath) throws FileNotFoundException, IOException {
		HashMap<String, Integer> pathMap = readPaths(infile, section, 1);

		pathMap.put(filepath, 1);
		putMapToFile(infile, section, pathMap);
	}

	private void putMapToFile(String infile, String section, HashMap<String, Integer> pathMap) throws FileNotFoundException, IOException {
		LinkedList<String> textList = fileToList(infile);
		Iterator<String> itr = textList.iterator();
		StringBuilder sb = new StringBuilder();
		String line;

		while (itr.hasNext()) {
			line = itr.next();
			sb.append(line);
			if (line.startsWith(section)) {
				while (itr.hasNext() && (line = itr.next()).startsWith("&"));
				sb.append(format(mapToString(pathMap)));
			}
		}
		writeToFile(infile, sb.toString());
	}

	private String mapToString(HashMap<String, Integer> pathMap) {
		StringBuilder sb = new StringBuilder();
		for (String path : pathMap.keySet()) {
			sb.append(path);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	public void deletePath(String infile, String section, String filepath) throws FileNotFoundException, IOException {
		HashMap<String, Integer> pathMap = readPaths(infile, section, 1);
		pathMap.remove(filepath);
		putMapToFile(infile, section, pathMap);
	}
}
