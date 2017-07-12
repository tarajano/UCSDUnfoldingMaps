package module3;

//Java utilities libraries
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;


//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	// Marker radious
	private float markerRadious = 10;
	
    // Using Processing's color method to generate an int that represents the color yellow.  
    private int yellow = color(240, 234, 39);
    private int orange = color(231, 124, 31);
    private int red = color(231, 46, 31);
    
    // Earthquake magnitude thresholds
    private double lowMagnitude = 4.0;
    private double mediumMagnitude = 4.9;
    private double largeMagnitude = 5.0;

    // Earthquake magnitude Radious
    private int lowMagnitudeRadious = 3;
    private int mediumMagnitudeRadious = 6;
    private int largeMagnitudeRadious = 9;
    
	// Markers List
	private List<Marker> markers;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "../data/2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
//			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Microsoft.RoadProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			earthquakesURL = "../data/2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    fillMarkersList(earthquakes);
	    
	    //TODO: Add code here as appropriate
	    map.addMarkers(markers);
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}
	

	// TODO. Complete method.
	// 
	private void addKey() {
		//// Coordinates
		// Icons' xCoord
		float iconsX = 30;
		float itemY = 100;
		float itemYShift = 10;

		// Radious Multiply
		float radiousMult = 2;
		
		// Legend rectangle
		fill(200);
		rect(20, 50, 160, 250);
		
		// Legend header
		textSize(20);
		fill(0, 102, 153);		
		text("Legend", 30, 60);
		
		// Legend icons
		fill(red);
		stroke(red);
		ellipse(iconsX, itemY, largeMagnitudeRadious * radiousMult, largeMagnitudeRadious * radiousMult);
		fill(orange);
		stroke(orange);
		ellipse(iconsX, itemY + itemYShift, mediumMagnitudeRadious * radiousMult, mediumMagnitudeRadious * radiousMult);
		fill(yellow);
		stroke(yellow);
		ellipse(iconsX, itemY + itemYShift * 2, lowMagnitudeRadious * radiousMult, lowMagnitudeRadious * radiousMult);

		// Leged Text
		textSize(12);
		fill(0, 0, 50);		
		text("Large", iconsX, itemY);
		text("Medium", iconsX, itemY + itemYShift );
		text("Low", iconsX, itemY + itemYShift * 2);
	}
	
	private void fillMarkersList(List<PointFeature> earthquakes){
	    for(PointFeature pointFeat : earthquakes){
	    	SimplePointMarker marker = createMarker(pointFeat);
	    	markers.add(createMarker(pointFeat));
    	}
	}
	
	private SimplePointMarker createMarker(PointFeature feature){
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		HashMap props = new HashMap<String, String>();
		props.put("magnitude", feature.getProperty("magnitude"));
		props.put("depth", feature.getProperty("depth"));
		marker.setProperties(props);
		int markerRadious = pickMarkerRadiousByMagnitude(feature);
		marker.setRadius(markerRadious);
		int markerColor = pickMarkerColorByMagnitude(feature);
		marker.setColor(markerColor);
		marker.setStrokeColor(markerColor);
		return marker;
	}
	
	private int pickMarkerColorByMagnitude(PointFeature feature){
		Float magnitude = Float.parseFloat(feature.getProperty("magnitude").toString());
		if(magnitude < lowMagnitude){
			return yellow;
		}else if(magnitude >= lowMagnitude & magnitude < mediumMagnitude){
			return orange;
		}else{
			return red;	
		}
	}
	
	private int pickMarkerRadiousByMagnitude(PointFeature feature){
		Float magnitude = Float.parseFloat(feature.getProperty("magnitude").toString());
		if(magnitude < lowMagnitude){
			return lowMagnitudeRadious;
		}else if(magnitude >= lowMagnitude & magnitude < mediumMagnitude){
			return mediumMagnitudeRadious;
		}else{
			return largeMagnitudeRadious;	
		}
	}
	
	private void printPointsFeatures(List<PointFeature> earthquakes){
	    for(PointFeature pointFeat : earthquakes){
	    	// Properties: depth magnitude title age
	    	System.out.println(pointFeat.getProperties());
	    }
	}

}
