package dao.impl;

import java.util.logging.Logger;
import dao.CityDao;
import database.Database;
import model.*;

public class CityDaoImpl implements CityDao {

    private static final Logger logger = Logger.getLogger(CityDaoImpl.class.getName());

    public int create(City city) {
    	int id = Database.getNewCityId();
    	city.setId(id);
    	Database.cityTable.put(id, city);
    	return id;
    }

    public void update(City city) {
    	Database.cityTable.put(city.getId(), city);
    }

    public City findById(int id) {
    	return Database.cityTable.get(id);
    }

    public void delete(int id) {
    	Database.cityTable.remove(id);
    }

}
