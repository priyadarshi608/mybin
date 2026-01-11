package dao.impl;

import java.util.logging.Logger;

import dao.CenterDao;
import database.Database;
import model.*;

public class CenterDaoImpl implements CenterDao {

    private static final Logger logger = Logger.getLogger(CenterDaoImpl.class.getName());

    public int create(Center center) {
    	int id = Database.getNewCenterId();
    	center.setId(id);
    	Database.centerTable.put(id, center);
    	return id;
    }

    public void update(Center center) {
    	Database.centerTable.put(center.getId(), center);
    }

    public Center findById(int id) {
    	return Database.centerTable.get(id);
    }

    public void delete(int id) {
    	Database.centerTable.remove(id);
    }

}
