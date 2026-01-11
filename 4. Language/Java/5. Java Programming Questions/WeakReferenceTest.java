package testing;

import java.lang.ref.WeakReference; 
import java.util.HashMap; 
import java.util.Map; 
   
public class WeakReferenceTest { 
   
 private WeakReference<Map<Integer, String>> myMap; 
   
 public static void main(String[] args) { 
  new WeakReferenceTest().doFunction(); 
 } 
   
 private void doFunction() { 
   
  Map<Integer, String> map = new HashMap<Integer, String>(); 
  myMap = new WeakReference<Map<Integer, String>>(map); 
   
  map = null; 
  int i = 0; 
  while (true) { 
   if (myMap != null && myMap.get() != null) { 
    myMap.get().put(i++, "test" + i); 
   
    System.out.println("im still working!!!!"); 
   } else { 
   
    System.out.println("*******im free*******"); 
   
   } 
   
  } 
 } 
}