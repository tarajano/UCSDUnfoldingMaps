package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
//import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class Airport{

	private String id;
	private Float latitude;
	private Float longitude;
	private String name;
	private String city;
	private String country;
	private String code3;
	private Float altitude;
	
	public Airport(String id, Location loc, HashMap properties){
		this.id = id;
		this.latitude = loc.getLat();
		this.longitude  = loc.getLon();
		this.name = (String) properties.get("name");
		this.city = ((String) properties.get("city")).replace("\"", "");
		this.country = (String) properties.get("country");
		this.code3 = (String) properties.get("code");
		this.altitude =  Float.parseFloat( (String) properties.get("altitude") ) ;
	}

	//toString
	public String toString(){
		String objectString =	"id: " + id + ", " + 
								"lat: " + latitude + ", "  + 
								"lon: " + longitude + ", " + 
								"name: " + name + ", " + 
								"city: " + city + ", " + 
								"country: " + country + ", " + 
								"code3: " + code3 + ", " + 
								"altitude: " + altitude;
		return objectString;
	}
	
	//getters
	public String getId() {
		return this.id;
	}

	public Float getLatitude() {
		return this.latitude;
	}

	public Float getLongitude() {
		return this.longitude;
	}

	public String getName() {
		return this.name;
	}

	public String getCity() {
		return this.city;
	}

	public String getCountry() {
		return this.country;
	}

	public String getCode3() {
		return this.code3;
	}

	public Float getAltitude() {
		return this.altitude;
	}
	
}
