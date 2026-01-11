package dao;

import model.*;

public interface CountryDao {
    public int create(Country country);
    public void update(Country country);
    public Country findById(int id);
    public void delete(int id);
}
