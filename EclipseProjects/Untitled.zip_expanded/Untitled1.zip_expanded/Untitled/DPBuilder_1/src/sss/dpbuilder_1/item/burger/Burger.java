package sss.dpbuilder_1.item.burger;

import sss.dpbuilder_1.item.Item;
import sss.dpbuilder_1.packing.Packing;
import sss.dpbuilder_1.packing.Wrapper;

public abstract class Burger implements Item {

	@Override
	public Packing packing() {
		return new Wrapper();
	}

	@Override
	public abstract float price();
}
