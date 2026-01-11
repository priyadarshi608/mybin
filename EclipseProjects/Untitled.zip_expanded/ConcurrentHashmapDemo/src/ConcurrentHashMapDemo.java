import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {
	public static void main(String args[]) {
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("abcd", "Sony");
		map.put("ABCD", "Samsung");
		map.put("123", "Micromax");
		map.put("!@#", "Karbon");
		
		Iterator iterator = map.keySet().iterator();
		
		while (iterator.hasNext()) {
			System.out.println(map.get(iterator.next()));
			
			// BUG BUG BUG
			
			// If the entry with key of size <= 4 will be added during iteration,
			// those entries will not be shown during this same iteration.
			
			// These won't be printed during this iteration..
			map.put("Son", "Jiss");
			map.put("Sony", "Jio");
			
			// These will be printed during this iteration..
			map.put("Sony1", "Jio1");
			map.put("Sony2", "Jio2");
		}
	}
}
