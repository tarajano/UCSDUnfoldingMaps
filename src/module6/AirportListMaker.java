package module6;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.PointFeature;
import parsing.ParseFeed;
import processing.core.PApplet;

public class AirportListMaker extends PApplet{

	private String path;
	
	public AirportListMaker(String pathToFile) {
		// TODO Auto-generated constructor stub
		this.path = pathToFile;
	}
	
	public List<PointFeature> getAirportsAsPointFeatureList() {
		List<PointFeature> features = ParseFeed.parseAirports(this, path);
		return features;
	}

}
