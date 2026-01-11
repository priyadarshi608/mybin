package sss.dpbuilder_1.item;
import sss.dpbuilder_1.packing.Packing;

public interface Item {
	public String name();
	public Packing packing();
	public float price();	
}
