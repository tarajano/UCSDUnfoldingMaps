package parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileIOParser {
	
	private String pathToFile;
	
	public FileIOParser(String path){
		this.pathToFile = path;
	}
	
	// Reads by line and add each line to an ArrayList.
	public ArrayList<String> fileToArrayList() throws IOException {
		ArrayList fileContent = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	fileContent.add(line);
		    }
		}
		return fileContent;
	}

}
