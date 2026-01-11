package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Country{
    int countryId;
    String countryName;
    public Country(int countryId, String countryName) {
        super();
        this.countryId = countryId;
        this.countryName = countryName;
    }
    public int getCountryId() {
        return countryId;
    }
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
class CountrySortByIdComparator implements Comparator<Country>{
    @Override
    public int compare(Country country1, Country country2) {
        return (country1.getCountryId() < country2.getCountryId() ) ? -1: (country1.getCountryId() > country2.getCountryId() ) ? 1:0 ;
    }
}
public class ComparatorMain {
	 /**
	  * @author Arpit Mandliya
	  */
	 public static void main(String[] args) {
	   Country indiaCountry=new Country(1, "India");
	   Country chinaCountry=new Country(4, "China");
	   Country nepalCountry=new Country(3, "Nepal");
	   Country bhutanCountry=new Country(2, "Bhutan");
	         List<Country> listOfCountries = new ArrayList<Country>();
	         listOfCountries.add(indiaCountry);
	         listOfCountries.add(chinaCountry);
	         listOfCountries.add(nepalCountry);
	         listOfCountries.add(bhutanCountry);
	         System.out.println("Before Sort by id : ");
	         for (int i = 0; i < listOfCountries.size(); i++) {
	    Country country=(Country) listOfCountries.get(i);
	    System.out.println("Country Id: "+country.getCountryId()+"||"+"Country name: "+country.getCountryName());
	   }
	         Collections.sort(listOfCountries,new CountrySortByIdComparator());
	         System.out.println("After Sort by id: ");
	         for (int i = 0; i < listOfCountries.size(); i++) {
	    Country country=(Country) listOfCountries.get(i);
	    System.out.println("Country Id: "+country.getCountryId()+"|| "+"Country name: "+country.getCountryName());
	   }
	     //Sort by countryName
	         Collections.sort(listOfCountries,new Comparator<Country>() {
	    @Override
	    public int compare(Country o1, Country o2) {
	     return o1.getCountryName().compareTo(o2.getCountryName());
	    }
	   });
	   System.out.println("After Sort by name: ");
	         for (int i = 0; i < listOfCountries.size(); i++) {
	    Country country=(Country) listOfCountries.get(i);
	    System.out.println("Country Id: "+country.getCountryId()+"|| "+"Country name: "+country.getCountryName());
	   }
     }
}
