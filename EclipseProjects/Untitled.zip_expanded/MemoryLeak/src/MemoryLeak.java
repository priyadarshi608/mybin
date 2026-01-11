import java.util.Map;

class MemLeak {
    public final String key;
    
    public MemLeak(String key) {
        this.key =key;
    }
    
    public static void main(String args[]) {
        try {
            Map map = System.getProperties();
            
            /*
             * ************************* V V I *******************************
             * The memory leak is not due to the infinite loop. The infinite 
             * loop can lead to a resource exhaustion, but not a memory leak.
             * If we had properly implemented equals() and hashcode() methods, 
             * the code would run fine even with the infinite loop as we would 
             * only have one element inside the HashMap.
             */
            for(;;) {
                map.put(new MemLeak("key"), "value");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
