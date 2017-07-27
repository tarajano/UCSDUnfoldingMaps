package module5;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
// TODO: Change SimplePointMarker to CommonMarker as the very first thing you do 
// in module 5 (i.e. CityMarker extends CommonMarker).  It will cause an error.
// That's what's expected.
public class CityMarker extends CommonMarker {
	
	public static int TRI_SIZE = 5;  // The size of the triangle marker
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}

	
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// IMPLEMENT: drawing triangle for each city
		pg.fill(150, 100, 30);
		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		// Restore previous drawing style
		pg.popStyle();
	}
	
	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		int textSize = 12;
		int positionShift = 5;
		String cityTitle =	"Population: " + Float.toString(this.getPopulation()) + " M. | " +	
							this.getCity() + ", " + 
							this.getCountry();
		pg.pushStyle();
		pg.noStroke();
		pg.rect(x - positionShift, y - textSize, cityTitle.length() * (textSize/2), textSize + positionShift);
		pg.textSize(textSize);
		pg.fill(0,0,0);
		pg.text(cityTitle, x + positionShift, y);
		pg.popStyle();
	}
	
	/*
	 * Returns the quakes within a threatCircle() distance from the cityMarker.
	 */
	public List<Marker> getThreateningQuakes(List<Marker> quakes) {
		List<Marker> threateningQuakes = new ArrayList<Marker>();
		for(Marker q: quakes) {
			EarthquakeMarker quake = (EarthquakeMarker) q;
			double quakeToCityDistance = this.getDistanceTo(quake.getLocation());
			if (quakeToCityDistance <= quake.threatCircle()) {
				threateningQuakes.add(quake);
			}
		}
		return threateningQuakes;
	}
	
	/* Local getters for some city properties.  
	 */
	public String getCity()
	{
		return getStringProperty("name");
	}
	
	public String getCountry()
	{
		return getStringProperty("country");
	}
	
	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}

}
