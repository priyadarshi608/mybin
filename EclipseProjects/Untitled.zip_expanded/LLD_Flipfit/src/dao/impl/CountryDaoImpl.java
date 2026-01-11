package dao.impl;

import java.util.logging.Logger;

import dao.CenterDao;
import dao.CountryDao;
import database.Database;
import model.*;

public class CountryDaoImpl implements CountryDao {

    private static final Logger logger = Logger.getLogger(CountryDaoImpl.class.getName());

    public int create(Country country) {
    	int id = Database.getNewCountryId();
    	country.setId(id);
    	Database.countryTable.put(id, country);
    	return id;
    }

    public void update(Country country) {
    	Database.countryTable.put(country.getId(), country);
    }

    public Country findById(int id) {
    	return Database.countryTable.get(id);
    }

    public void delete(int id) {
    	Database.countryTable.remove(id);
    }

}
