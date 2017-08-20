package module6;

import java.util.ArrayList;
import java.util.Arrays;
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
		size(1600,1200, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 1550, 1150);
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
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "../data/routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				Location locSource = airports.get(source);
				Location locDestination = airports.get(dest);
				route.addLocation(locSource);
				route.addLocation(locDestination);
//				System.out.println( " locSource: " + locSource +
//									" locDestination: " + locDestination +
//									" route.getLocations(): " + route.getLocations() + 
//									" checkLocationsPresenceInRoutesList(): " + 
//									checkLocationsPresenceInRoutesList(locSource, locDestination) 
//									);
//				System.out.println("route.getLocations(): " + route.add  );
				SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
				//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
//				System.out.println("  sl: " + sl.getLocations() + " | " + sl.getProperties() );
				routeList.add(sl);
				
			}
		}
		
		routeList = setRouteTraffic(routeList);
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	private List<Marker> setRouteTraffic(List<Marker> rList) {
		HashMap<List<Location>,Integer> routesTrafficHashMap = new HashMap<List<Location>,Integer>(); 
		List<Marker> returnRouteList = new ArrayList<Marker>();
		Integer traffic = 1;
		
		// Filling routesTrafficHashMap
		for (Marker m : rList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
//			System.out.println("  slm: " + slm.getLocations() + " | " + slm.getProperties() );
			List<Location> locs = slm.getLocations();
			
			List<Location> locsSwaped = Arrays.asList(locs.get(1), locs.get(0));
			
			if ( routesTrafficHashMap.containsKey(locs) ) {
				traffic = routesTrafficHashMap.get(locs);
				routesTrafficHashMap.replace(locs, traffic + 1);
			} else if (routesTrafficHashMap.containsKey(locsSwaped) ) {
				traffic = routesTrafficHashMap.get(locsSwaped);
				routesTrafficHashMap.replace(locsSwaped, traffic + 1);
			} else {
				routesTrafficHashMap.put(locs, 1);
			}
		}
		
		// Setting route traffic property 
		for (Marker m : rList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
			List<Location> locs = slm.getLocations();
			List<Location> locsSwaped = Arrays.asList(locs.get(1), locs.get(0));
			
			if ( routesTrafficHashMap.containsKey(locs) ) {
				traffic = routesTrafficHashMap.get(locs);
			} else if ( routesTrafficHashMap.containsKey(locsSwaped) ) {
				traffic = routesTrafficHashMap.get(locsSwaped);
			}
			slm.setProperty("routeTraffic", traffic);
			slm.setStrokeWeight(5);
		    slm.setColor( getColor(traffic) );
			System.out.println(slm.getLocations() + " " + slm.getProperties() );
			returnRouteList.add(slm);
		}
		
		return returnRouteList;
	}
	
	private int getColor(int traffic) {
		int col = 0;
		colorMode(RGB, 100);
		int alpha = 100 - (100 - traffic);
		col = color(200,0,0,alpha);
		return col;
	}
	
	private boolean checkLocationsPresenceInRoutesList(Location source, Location destination) {
		for (Marker m: routeList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
			List<Location> locs = slm.getLocations();
			if ( locs.contains(source) & locs.contains(destination) ) {
				return true;
			}
		}
		return false;
	}
	
	
	
	// Defining Interactive Behavior
	
	@Override
	public void mouseClicked()
	{
		lastClicked = selectMarkerIfClicked(airportList);
		
		if (lastClicked == null) {
			unHideMarkers(airportList);
			unSelectMarkers(airportList);
			// TODO
			// unHideRoutes()
		}
		else {
			System.out.println("lastClicked.isSelected(): " + lastClicked.isSelected() + " " + lastClicked.getProperties().toString());
			hideMarkers(airportList);
			lastClicked.setHidden(false);
			// TODO 
			// unHideMarkers() for airports connected to lastClicked
			// hideRoutes()  NOT connected to lastClicked
		}
	}
	
	private CommonMarker selectMarkerIfClicked(List<Marker> markers)
	{
		CommonMarker marker = null;
		
		for(Marker m: markers){
			CommonMarker cm = (CommonMarker) m;
			if( cm.isInside(map, mouseX, mouseY) ){
				System.out.println("marker: " + m.getProperties().toString() + " clicked");
				marker = cm;
				marker.setClicked(true);
				marker.setSelected(true); 
				return marker;
			}
		}
		System.out.println("no marker clicked");
		return marker;
	}
	
	// loop over and unhide all markers
	private void unHideMarkers(List<Marker> markers) {
		System.out.println("unhiding markers");
		for(Marker marker : markers) {
			marker.setHidden(false);
		}
	}
	
	private void unSelectMarkers(List<Marker> markers) {
		System.out.println("unhiding markers");
		for(Marker marker : markers) {
			marker.setSelected(false); 
		}
	}
	
	private void hideMarkers(List<Marker> markers) {
		for(Marker marker : markers) {
			marker.setHidden(true);
		}
	}
	

}