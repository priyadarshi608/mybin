import sss.dpmemento.memento.DataSet;
import sss.dpmemento.memento.Memento;

public class DPMementoDemo {
	public static void main(String[] args) {
		Memento<DataSet> memento = new Memento<DataSet>();
		DataSet dataSet = new DataSet(1, "state_1");
		dataSet.setStr("state_2");
		memento.saveStateToMemento(dataSet.clone());
		dataSet.setStr("state_3");
		memento.saveStateToMemento(dataSet.clone());
		printStateOfDataSet(dataSet);
		memento.showAllStates();
		dataSet = memento.getStateFromMemento(1);
		printStateOfDataSet(dataSet);
		dataSet = memento.getStateFromMemento(2);
		printStateOfDataSet(dataSet);
	}
	
	private static void printStateOfDataSet(DataSet dataSet) {
		System.out.println("Now the state of dataSet " + dataSet.getId() + ": " + dataSet.getStr() + "identityHashCode: " + System.identityHashCode(dataSet));
	}
}
