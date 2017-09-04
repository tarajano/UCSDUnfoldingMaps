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
 * A class to represent Route Mid Point Marker on a world map.
 *
 */
public class RouteMidPointMarker extends CommonMarker {

	private int textSize = 12;
	private boolean hidden = true;
	
	public RouteMidPointMarker(Location location) {
		super(location);
		this.setHidden(hidden);
	}
	
	public RouteMidPointMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
		this.setHidden(hidden);
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		float radius = 3;
		int positionShift = 5;
		int textSize = 11;
		
		pg.pushStyle();
		pg.fill(200);
		pg.noStroke();
		pg.ellipse(x, y, radius, radius);
		
		String routeLen = this.getProperty("routeDistanceKm").toString() ;
		String title = routeLen + " Kms";
		pg.fill(200,20,20);
		pg.textSize(textSize);
		pg.text(title, x , y);
		pg.popStyle();
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
//		int positionShift = 5;
//		String title = "name: " + this.getProperty("name").toString() + " " + 
//				       "code: " + this.getProperty("code").toString() + " " +
//				 	   "city: " + this.getProperty("city").toString() + " " +
//				 	   "country: " + this.getProperty("country").toString()
//				 	   ; 
//		pg.pushStyle();
//		pg.noStroke(); 
//		pg.rect(x + positionShift, y - textSize, title.length() * (textSize/2), textSize + positionShift);
//		pg.textSize(textSize);
//		pg.fill(20,20,20);
//		pg.text(title, x + positionShift, y);
//		pg.popStyle();
		// show routes
	}
	
	@Override
	public void showRoutesDistances(PGraphics pg, float x, float y) {
//		int radius = 10;
//
//		for(SimpleLinesMarker slm : this.routes) {
//			pg.pushStyle();
//			pg.fill(250,20,20);
//			Location loc = (Location) slm.getProperty("midPointCoords");
//			float xCoord = loc.getLat();
//			float yCoord = loc.getLon();
//			pg.ellipse(xCoord, yCoord, radius, radius);
//			pg.popStyle();
//			System.out.printf(	"Airport(%.2f,%.2f), MidPoint(%.2f,%.2f), MouseClicked(%.2f,%.2f)\n",
//								this.getLocation().getLat(),this.getLocation().getLon(),
//								xCoord, yCoord,
//								x, y
//							);
//		}
//		
	}
	
}
