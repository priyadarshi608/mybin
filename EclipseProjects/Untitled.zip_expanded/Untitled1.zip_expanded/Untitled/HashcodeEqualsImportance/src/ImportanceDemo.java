import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import sss.importance.employees.*;

public class ImportanceDemo {
	public static void main(String args[]) {
        Employee1 e1Obj1 = new Employee1(100, "e1Obj");
        Employee1 e1Obj2 = new Employee1(100, "e1Obj");
 
        //Prints 'false' which is business logic wise incorrect.
        System.out.println("Employee1 both objects equals: " + e1Obj1.equals(e1Obj2));
        
        Set<Employee1> employees1 = new HashSet<Employee1>();
        employees1.add(e1Obj1);
        employees1.add(e1Obj2);
         
        // Prints two objects as equals method treats both objects different.
        System.out.println("All elements of Employees1: ");
        System.out.println(employees1);
        System.out.println();
 
        
        
        Employee2 e2Obj1 = new Employee2(100, "e2Obj");
        Employee2 e2Obj2 = new Employee2(100, "e1Obj");
 
        //Prints 'true' which is logically correct.
        System.out.println("Employee2 both objects equals: " + e2Obj1.equals(e2Obj2));
 
        Set<Employee2> employees2 = new HashSet<Employee2>();
        employees2.add(e2Obj1);
        employees2.add(e2Obj2);
         
        // Prints two objects. If both employee objects have been equal, in a 
        // Set which stores only unique objects, there must be only one instance
        // inside HashSet, after all both objects refer to same employee.
        System.out.println("All elements of Employees2: ");
        System.out.println(employees2);
        System.out.println();
        
        
        
        Employee3 e3Obj1 = new Employee3(100, "e3Obj");
        Employee3 e3Obj2 = new Employee3(100, "e3Obj");
 
        //Prints 'true' which is logically correct.
        System.out.println("Employee3 both objects equals: " + e3Obj1.equals(e3Obj2));
 
        Set<Employee3> employees3 = new HashSet<Employee3>();
        employees3.add(e3Obj1);
        employees3.add(e3Obj2);
         
        // Prints only one object.
        System.out.println("All elements of Employees3: ");
        System.out.println(employees3);
	}
}
