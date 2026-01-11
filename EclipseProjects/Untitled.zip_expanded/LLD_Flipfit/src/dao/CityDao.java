package dao;

import model.*;

public interface CityDao {
    public int create(City city);
    public void update(City city);
    public City findById(int id);
    public void delete(int id);
}
