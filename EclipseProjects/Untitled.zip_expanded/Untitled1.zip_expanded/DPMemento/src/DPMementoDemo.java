import sss.dpmemento.memento.DataSet;
import sss.dpmemento.memento.Memento;

public class DPMementoDemo {
	public static void main(String[] args) {
		DataSet dataSet = new DataSet(1, "sate_1");
		dataSet.setStr("state_2");
		Memento<DataSet> memento = new Memento<DataSet>();
		memento.saveStateToMemento(dataSet);
		dataSet.setStr("state_3");
		memento.saveStateToMemento(dataSet);
		printStateOfDataSet(dataSet);
		memento.showAllStates();
		dataSet = memento.getStateFromMemento(1);
		printStateOfDataSet(dataSet);
	}
	
	private static void printStateOfDataSet(DataSet dataSet) {
		System.out.println("Now the state of dataSet " + dataSet.getId() + ": ");
		System.out.println(dataSet.getStr());
		System.out.println();
	}
}
