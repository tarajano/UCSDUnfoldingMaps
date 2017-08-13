package module6;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import parsing.FileIOParser;
import processing.core.PApplet;

public class TesterClass{

//	List<PointFeature> airportsPointFeatures = ParseFeed.parseAirports(this, "../data/airports.dat");

	public static void main(String[] args) throws IOException {
		
		String path = "/Users/tarajano/eclipse-workspace/UCSDUnfoldingMaps/data/airports.dat";
		
		// Testing FileIOParser
		//FileIOParser fiop = new FileIOParser(path);
		//ArrayList<String> fileContent = fiop.fileToArrayList();
		//System.out.println(fileContent.toString());
		
		// Creating Airport objects from airports.dat file
		List<Airport> airportsList = generateAirportsAsPointFeatureList(path);
		
		// Linear search for 'city' in the list of airports.
		String targetCity = "Havana";
		String airportCode = getCodeForTargetCity(targetCity, airportsList);
		System.out.println("Code for airport in " + targetCity + ": " + airportCode);
		

		// Binary search for 'city' in the list of airports.
		// Sorting airports alphabetically by city name
//		String targetCity = "Havana";
		Collections.sort(airportsList);
		airportCode = getCodeForTargetCityBinarySearch(targetCity, airportsList);
		System.out.println("Code for airport in " + targetCity + ": " + airportCode);		
		
	}
	
	
	// Binary search for 'city'.
	// Must provide a list ordered alphabetically and ascending on the relevant property (city).
	static private String getCodeForTargetCityBinarySearch(String targetString, List<Airport> list) {
		int bottom = 0;
		int top = list.size();
		
		while (bottom <= top) {
			int middle = (bottom + top)/2;
			
			String currentString = list.get(middle).getCity(); 
			int stringCompareResult = currentString.compareTo(targetString);
			
			if( stringCompareResult == 0 ) {
				return list.get(middle).getCode3();
			} else if ( stringCompareResult < 0 ) {
				bottom = middle + 1;
			} else {
				top  = middle - 1;
			}
		}

		return null;
	}
	

	// Linear search for 'city'.
	static private String getCodeForTargetCity (String targetCity, List<Airport> airportsList) {
		int rounds = 0;
		for (Airport airport: airportsList) {
			if ( airport.getCity().equals(targetCity)) {
				return airport.getCode3();
			}
		}
		return null;
	}
	
	
	// Creating a list of Airport objects.
	static private List<Airport> generateAirportsAsPointFeatureList (String path) {
		
		AirportListMaker lm = new AirportListMaker(path);
		List<PointFeature> airportsLF = lm.getAirportsAsPointFeatureList();
		List<Airport> airportsList = new ArrayList<Airport>();
		
		for (PointFeature pf : airportsLF) {
			airportsList.add(new Airport(pf.getId(), pf.getLocation(), pf.getProperties()) );
		}
		
		return airportsList;
	}
	
	

}
