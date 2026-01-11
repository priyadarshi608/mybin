public class TestAnnotations {
   public static void main(String arg[]) {
      new TestAnnotations().doSomeTestRetention();
      new TestAnnotations().doSomeTestDocumented();
   }
   @Test_Retention (doTestRetention="Hello retention test")
   public void doSomeTestRetention() {
      System.out.printf("Testing annotation 'Retention'");
   }
   @Test_Documented(doTestDocument="Hello document")
   public void doSomeTestDocumented() {
      System.out.printf("Testing annotation 'Documented'");
   }
}
