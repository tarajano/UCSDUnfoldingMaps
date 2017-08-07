package module6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import parsing.FileIOParser;
import processing.core.PApplet;

public class TesterClass{

//	List<PointFeature> airportsPointFeatures = ParseFeed.parseAirports(this, "../data/airports.dat");

	public static void main(String[] args) throws IOException {
		
		String path = "/Users/tarajano/eclipse-workspace/UCSDUnfoldingMaps/data/airports.dat";
		
		// Creating Airport objects from airports.dat file
		AirportListMaker lm = new AirportListMaker(path);
		List<PointFeature> airportsLF = lm.getAirportsAsPointFeatureList();
		List<Airport> airportsList = new ArrayList<Airport>();
		for (PointFeature pf : airportsLF) {
			//System.out.println(pf.getId() + " -- " + pf.getLocation() + " -- " + pf.getProperties().toString());
			airportsList.add(new Airport(pf.getId(), pf.getLocation(), pf.getProperties()) );
		}
		
		for (Airport airport : airportsList) {
			System.out.println(airport.toString());
		}
		
		// Testing FileIOParser
		//FileIOParser fiop = new FileIOParser(path);
		//ArrayList<String> fileContent = fiop.fileToArrayList();
		//System.out.println(fileContent.toString());
		
		// TODO COMMIT
		
	}
	
	

}
