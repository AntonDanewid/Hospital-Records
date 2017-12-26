package server;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface User {

	/** 
	*	Reads specified file's content
	**/
	public String read(String infile) throws FileNotFoundException, IOException, IllegalAccessException;
	
	public String create(String patient, String doctor, String nurse, String division, String content) throws IllegalAccessException;
	
	public String write(String infile, String content) throws FileNotFoundException, IOException, IllegalAccessException;
	
	public String delete(String infile) throws FileNotFoundException, IOException, IllegalAccessException;
	
	public String getName();
}