import sss.dpiterator_1.container.ConcreteContainer;
import sss.dpiterator_1.container.Iterator;

public class IteratorPatternDemo {

	public static void main(String[] args) {
		ConcreteContainer namesRepository = new ConcreteContainer();

		for(Iterator iter = namesRepository.getIterator(); iter.hasNext();){
			String name = (String)iter.next();
			System.out.println("Name : " + name);
		}
	}
}
