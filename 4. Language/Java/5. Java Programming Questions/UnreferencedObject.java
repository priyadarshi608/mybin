package testing;

class UnreferencedObject { 
static UnreferencedObject a=null; 
public static void main(String[] args) 
{ UnreferencedObject b=new UnreferencedObject();
 System.out.println( b.hashCode() );
 b=new UnreferencedObject();
 System.gc(); //System.out.println( a.hashCode() ); 
}
 public void finalize() {
 a=this; 
System.out.println( a.hashCode() ); 
System.out.println("finalize method");
 }
 }
