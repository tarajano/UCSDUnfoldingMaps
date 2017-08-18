package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "../data/airportsHead.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(10);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			//routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	
	// Defining Interactive Behavior
	
	// TODO
	// git commit.
	// fix crash after clicking out of markers.
	
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}
//		selectMarkerIfClicked(airportList);
	}
	
	private CommonMarker selectMarkerIfClicked(List<Marker> markers)
	{
		CommonMarker markerNull = null;
		
		for(Marker m: markers){
			CommonMarker cm = (CommonMarker) m;
			if( cm.isInside(map, mouseX, mouseY) ){
				System.out.println("marker: " + m.getProperties().toString() + " clicked");
				cm.setClicked(true);
				lastClicked = cm;
				return lastClicked;
			}
		}
		return markerNull;
	}
	
	@Override
	public void mouseClicked()
	{
		lastClicked = selectMarkerIfClicked(airportList);
		
		if (lastClicked == null) {
			unhideMarkers(airportList);
			lastClicked.setClicked(false);
			lastClicked.setSelected(false);
		}
		else {
			hideMarkers(airportList);
			lastClicked.setHidden(false);
			lastClicked.setClicked(true);
			lastClicked.setSelected(true); 
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers(List<Marker> markers) {
		System.out.println("unhiding markers");
		for(Marker marker : markers) {
			marker.setHidden(false);
		}
	}
	
	private void hideMarkers(List<Marker> markers) {
		for(Marker marker : markers) {
			marker.setHidden(true);
		}
	}
	

}
