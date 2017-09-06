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
	public void showTitle(PGraphics pg, float x, float y) { } // not needed for this marker. maybe a design issue.
	
}
