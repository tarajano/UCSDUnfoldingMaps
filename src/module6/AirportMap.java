package module6;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	
//	private CommonMarker lastSelected;
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
				sl.setProperty("midPointCoords", getRouteMidPoint(sl));
				routeList.add(sl);
			}
		}
		// Linking data
		routeList = setRoutesTraffic(routeList);
		//printRouteProperties();
		routeList = deleteDuplicateRoutes(routeList);
		routeList = setRoutesDistance(routeList);
		routeList = setRoutesMidPoint(routeList);
		assignRoutesToAirports();
//		printAirportMarkers();
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
			deselectMarkers(airportList);
			displayMarkers(routeList); 
		} else {
			// first hide all markers
			hideMarkers(airportList);
			hideMarkers(routeList);
			// then display markers related to lastClicked (airports and routes)
			lastClicked.setHidden(false);
			displayMarkers(getAirportsConnectedToLastClicked(lastClicked));
			displayMarkers(getRoutesConnectingAirport(lastClicked));
		}
	}
	
	// Helper methods
	private Location getRouteMidPoint(SimpleLinesMarker slm) {
		Location midPoint = null;
		List<Location> locs = slm.getLocations();
		float x1 = locs.get(0).getLat();
		float y1 = locs.get(0).getLon();
		float x2 = locs.get(1).getLat();
		float y2 = locs.get(1).getLon();
		float midX = (x1 + x2)/2;
		float midY = (y1 + y2)/2;
		midPoint = new Location(midX,midY);
		return midPoint;
	}
	
	private void printAirportMarkers() {
		for (Marker m : airportList ) {
			AirportMarker am = (AirportMarker) m;
			System.out.println( m.getProperties().toString() + " routes: " + am.getRoutes().size() ) ; 
		}
	}
	
	private void printRouteProperties() {
		for (Marker m : routeList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
			System.out.println( " route -- slm: " + slm.getProperties().toString()) ; 
		}
	}

	private List<Marker> deleteDuplicateRoutes(List<Marker> rList) {
		// This method should be executed ONLY after the route traffic 
		// has been computed. This method will remove route 'duplicates'.
		// Duplicates are defined here as repeated source/destination 
		// airport pairs, which happens due to 1) inbound/outbound flights
		// and 2) multiple airlines covering the same route. 
		List<Marker> returnRouteList = new ArrayList<Marker>();
		List<List<Location>> locsCheckList = new ArrayList<List<Location>>();
		
//		System.out.println("deleteDuplicateRoutes input List size: " + rList.size());
		
		for (Marker m : rList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
			List<Location> locs = slm.getLocations();
			List<Location> locsSwaped = Arrays.asList(locs.get(1), locs.get(0));
			
			// Avoid duplicate source/destination | destination/source pairs
			if (! locsCheckList.contains(locs) & ! locsCheckList.contains(locsSwaped)) {
				locsCheckList.add(locs);
				returnRouteList.add(slm);
			}
			
//			System.out.println("   route: " + slm.getProperties().toString() );
		}
		
//		System.out.println("deleteDuplicateRoutes return List size: " + returnRouteList.size());
		
		return returnRouteList;
	}
	
	private List<Marker> setRoutesMidPoint(List<Marker> rList) {
		List<Marker> returnRouteList = new ArrayList<Marker>();
		
		for (Marker m : rList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
			slm.setProperty("midPointCoords", getRouteMidPoint(slm) );
			returnRouteList.add(slm);
		}
		
		return returnRouteList;
	}
	
	private List<Marker> setRoutesDistance(List<Marker> rList) {
		List<Marker> returnRouteList = new ArrayList<Marker>();
		
		for (Marker m : rList) {
			SimpleLinesMarker slm = (SimpleLinesMarker) m;
			slm.setProperty("routeDistanceKm", getRouteDistance(slm) );
			returnRouteList.add(slm);
		}
		
		return returnRouteList;
	}
	
	private Double getRouteDistance(SimpleLinesMarker slm) {
		Double routeDistance = null;
		DecimalFormat df = new DecimalFormat("#.0");
		List<Location> locs = slm.getLocations();
		routeDistance = (Double) locs.get(0).getDistance(locs.get(1));
		routeDistance = Double.parseDouble( df.format(routeDistance) );
		return routeDistance;
	}
	
	private void assignRoutesToAirports() {
		for (Marker am : airportList) {
			AirportMarker airport = (AirportMarker) am;
			airport.setRoutes( (List<SimpleLinesMarker>)(List<?>) getRoutesConnectingAirport(airport) );
		}
	}
	
	private List<Marker> setRoutesTraffic(List<Marker> rList) {
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
		int alpha = (int) (100 - (100 - Math.pow(traffic, 1.5)));
		col = color(255,0,0,alpha);
		return col;
	}

	private List<Marker> getRoutesConnectingAirport( CommonMarker cmarker ) {
		Marker airport = (Marker) cmarker;
		List<Marker> connectingRoutes = new ArrayList<Marker>();
		Integer airportId = Integer.parseInt((String)airport.getId());
		
		for(Marker route : routeList) {
			SimpleLinesMarker slmRoute = (SimpleLinesMarker) route;
			Integer sourceId =  Integer.parseInt((String)slmRoute.getProperty("source"));
			Integer destId =  Integer.parseInt((String)slmRoute.getProperty("destination"));
			
			if ( airportId.equals(sourceId) | airportId.equals(destId)) {
				connectingRoutes.add(route);
			}			
		}
		System.out.println("airportId, connectingRoutes: " + airportId + " " + connectingRoutes.size());
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
		idList = new ArrayList<>(new HashSet<>(idList));
		for (Integer refereceId : idList) {
			for (Marker airport : airportList) {
				Integer targetId =  Integer.parseInt((String)airport.getId());
				if ( targetId.equals(refereceId) ) {
//					System.out.println(" connection: " + airport.getProperties().toString());
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
	
	private void deselectMarkers(List<Marker> markers) {
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