package module6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;


/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private boolean online = false;
	private List<Marker> airportList;
	List<Marker> routeList;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		if(online) {
			map = new UnfoldingMap(this, 10, 10, 790, 590);
		} else {
			map = new UnfoldingMap(this, 50, 50, 1550, 1150, new MBTilesMapProvider("../data/blankLight-1-3.mbtiles"));
		}
		
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
			m.setId(feature.getId());
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
				SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
				routeList.add(sl);
				
			}
		}
		
		routeList = setRouteTraffic(routeList);
		map.addMarkers(routeList);
		map.addMarkers(airportList);
	}
	
	
	// Drawing
	public void draw() {
		background(0);
		map.draw();	
	}
	
	// Interactive behavior
	@Override
	public void mouseClicked()
	{
		lastClicked = selectMarkerIfClicked(airportList);
		
		if (lastClicked == null) {
			displayMarkers(airportList);
			unSelectMarkers(airportList);
			displayMarkers(routeList); 
		} else {
			// hide all markers
			hideMarkers(airportList);
			hideMarkers(routeList);
			// display markers related to lastClicked
			lastClicked.setHidden(false);
			displayMarkers(getAirportsConnectedToLastClicked(lastClicked));
			displayMarkers(getRoutesConnectingLastClicked(lastClicked));
		}
	}
	
	// Helper methods
	private List<Marker> setRouteTraffic(List<Marker> rList) {
		HashMap<List<Location>,Integer> routesTrafficHashMap = new HashMap<List<Location>,Integer>(); 
		List<Marker> returnRouteList = new ArrayList<Marker>();
		Integer traffic = 1;
		
		// Filling routesTrafficHashMap
		for (Marker m : rList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
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

	private List<Marker> getRoutesConnectingLastClicked( CommonMarker last ) {
		Marker lastClicked = (Marker) last;
		List<Marker> connectingRoutes = new ArrayList<Marker>();
		Integer lastClickedId = Integer.parseInt((String)lastClicked.getId());
		
		for(Marker route : routeList) {
			SimpleLinesMarker slmRoute = (SimpleLinesMarker) route;
			Integer sourceId =  Integer.parseInt((String)slmRoute.getProperty("source"));
			Integer destId =  Integer.parseInt((String)slmRoute.getProperty("destination"));
			
			if ( lastClickedId.equals(sourceId) | lastClickedId.equals(destId)) {
				connectingRoutes.add(route);
			}			
		}
		
		return connectingRoutes;
	}
	
	private List<Marker> getAirportsConnectedToLastClicked( CommonMarker last ) {
		Marker lastClicked = (Marker) last;
		List<Marker> connectedAirports = new ArrayList<Marker>();
		List<Integer> connectedAirportsIds = new ArrayList<Integer>();
		Integer lastClickedId = Integer.parseInt((String)lastClicked.getId());
		
		for(Marker route : routeList) {			
			SimpleLinesMarker slmRoute = (SimpleLinesMarker) route;
			Integer sourceId =  Integer.parseInt((String)slmRoute.getProperty("source"));
			Integer destId =  Integer.parseInt((String)slmRoute.getProperty("destination"));
			
			if ( lastClickedId.equals(sourceId) ) {
				connectedAirportsIds.add(destId);
			} else if (lastClickedId.equals(destId)) {
				connectedAirportsIds.add(sourceId);
			}
		}
		
		connectedAirports = getAirportsById(connectedAirportsIds);		
		return connectedAirports;
	}
	
	private List<Marker> getAirportsById(List<Integer> idList) {
		List<Marker> airportsReturn = new ArrayList<Marker>();
		// TODO make arg idList unique
		for (Integer refereceId : idList) {
			for (Marker airport : airportList) {
				Integer targetId =  Integer.parseInt((String)airport.getId());
				if ( targetId.equals(refereceId) ) {
					System.out.println(" connection: " + airport.getProperties().toString());
					airportsReturn.add(airport);
				}
			}
		}
		return airportsReturn;
	}
	
	private CommonMarker selectMarkerIfClicked(List<Marker> markers){
		CommonMarker marker = null;
		
		for(Marker m: markers){
			CommonMarker cm = (CommonMarker) m;
			if( cm.isInside(map, mouseX, mouseY) ){
				marker = cm;
				marker.setClicked(true);
				marker.setSelected(true); 
				return marker;
			}
		}
		return marker;
	}
	
	private void displayMarkers(List<Marker> markers) {
		for(Marker marker : markers) {
			marker.setHidden(false);
		} 
	}
	
	private void unSelectMarkers(List<Marker> markers) {
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