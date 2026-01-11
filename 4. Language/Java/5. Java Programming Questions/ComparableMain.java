package testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Country1 implements Comparable<Country1>{
    int Country1Id;
    String Country1Name;
    public Country1(int Country1Id, String Country1Name) {
        super();
        this.Country1Id = Country1Id;
        this.Country1Name = Country1Name;
    }
    @Override
    public int compareTo(Country1 Country1) {
       return (this.Country1Id < Country1.Country1Id ) ? -1: (this.Country1Id > Country1.Country1Id ) ? 1:0 ;
    }
    public int getCountry1Id() {
        return Country1Id;
    }
    public void setCountry1Id(int Country1Id) {
        this.Country1Id = Country1Id;
    }
    public String getCountry1Name() {
        return Country1Name;
    }
    public void setCountry1Name(String Country1Name) {
        this.Country1Name = Country1Name;
    }
}
public class ComparableMain {
	 public static void main(String[] args) {
	   Country1 indiaCountry1=new Country1(1, "India");
	   Country1 chinaCountry1=new Country1(4, "China");
	   Country1 nepalCountry1=new Country1(3, "Nepal");
	   Country1 bhutanCountry1=new Country1(2, "Bhutan");
	         List<Country1> listOfCountries = new ArrayList<Country1>();
	         listOfCountries.add(indiaCountry1);
	         listOfCountries.add(chinaCountry1);
	         listOfCountries.add(nepalCountry1);
	         listOfCountries.add(bhutanCountry1);
	         System.out.println("Before Sort  : ");
	         for (int i = 0; i < listOfCountries.size(); i++) {
	    Country1 Country1=(Country1) listOfCountries.get(i);
	    System.out.println("Country1 Id: "+Country1.getCountry1Id()+"||"+"Country1 name: "+Country1.getCountry1Name());
	   }
	         Collections.sort(listOfCountries);
	         System.out.println("After Sort  : ");
	         for (int i = 0; i < listOfCountries.size(); i++) {
	    Country1 Country1=(Country1) listOfCountries.get(i);
	    System.out.println("Country1 Id: "+Country1.getCountry1Id()+"|| "+"Country1 name: "+Country1.getCountry1Name());
	   }
	 }
}
