package module6;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public List<SimpleLinesMarker> routes;
	private int textSize = 12;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	public void setRoutes(List<SimpleLinesMarker> r) {
		this.routes = r;
	}
	
	public List<SimpleLinesMarker> getRoutes() {
		return this.routes;
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.fill(11);
		pg.ellipse(x, y, 5, 5);
		pg.popStyle();
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		int positionShift = 5;
		
		String title = this.getProperty("name").toString() + ", " + 
				       this.getProperty("code").toString() + ", " +
				 	   this.getProperty("city").toString() + ", " +
				 	   this.getProperty("country").toString()
				 	   ;
		title = title.replace("\"", "");
		
		pg.pushStyle();
		pg.noStroke(); 
		pg.rect(x + positionShift, y - textSize, title.length() * (textSize/2), textSize + positionShift);
		pg.textSize(textSize);
		pg.fill(20,20,20);
		pg.text(title, x + positionShift, y);
		pg.popStyle();
	}
	
}
