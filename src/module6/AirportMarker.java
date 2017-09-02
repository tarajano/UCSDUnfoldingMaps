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
	
	@Override
	public void showRoutesDistances(PGraphics pg, float x, float y) {
		int radius = 10;

		for(SimpleLinesMarker slm : this.routes) {
			pg.pushStyle();
			pg.fill(250,20,20);
			Location loc = (Location) slm.getProperty("midPointCoords");
			float xCoord = loc.getLat();
			float yCoord = loc.getLon();
			pg.ellipse(xCoord, yCoord, radius, radius);
			pg.popStyle();
			System.out.printf(	"Airport(%.2f,%.2f), MidPoint(%.2f,%.2f), MouseClicked(%.2f,%.2f)\n",
								this.getLocation().getLat(),this.getLocation().getLon(),
								xCoord, yCoord,
								x, y
							);
		}
		
	}
	
}
