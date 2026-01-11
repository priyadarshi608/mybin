package database;

import java.util.concurrent.ConcurrentHashMap;

import model.Center;
import model.City;
import model.Country;
import model.Member;

public class Database {

	public static ConcurrentHashMap<Integer, Country> countryTable = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Integer, City> cityTable = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<Integer, Center> centerTable = new ConcurrentHashMap<>();

	private static int countryIdCounter = 0;
	
	public static synchronized int getNewCountryId() {
		countryIdCounter++;
		return countryIdCounter;
	}
	
	private static int cityIdCounter = 0;
	
	public static synchronized int getNewCityId() {
		cityIdCounter++;
		return cityIdCounter;
	}
	
	private static int centerIdCounter = 0;
	
	public static synchronized int getNewCenterId() {
		centerIdCounter++;
		return centerIdCounter;
	}
	
}
