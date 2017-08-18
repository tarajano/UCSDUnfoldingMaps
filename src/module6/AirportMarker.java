package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
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
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
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
		System.out.println("showTitle()");
		int textSize = 12;
		int positionShift = 5;
		String title = "name: " + this.getProperty("name").toString() + " " + 
				       "code: " + this.getProperty("code").toString() + " " +
				 	   "city: " + this.getProperty("city").toString() + " " +
				 	   "country: " + this.getProperty("country").toString()
				 	   ; 
		pg.pushStyle();
		pg.noStroke();
		pg.rect(x + positionShift, y - textSize, title.length() * (textSize/2), textSize + positionShift);
		pg.textSize(textSize);
		pg.fill(20,20,20);
		pg.text(title, x + positionShift, y);
		pg.popStyle();
		// show routes
	}
	
}
