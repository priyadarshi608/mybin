import sss.dpvisitor_1.computer_part.Computer;
import sss.dpvisitor_1.computer_part.ComputerPart;
import sss.dpvisitor_1.computer_part_visitor.ComputerPartVisitorImpl;

public class VisitorPatternDemo {
   public static void main(String[] args) {

      ComputerPart computer = new Computer();
      computer.accept(new ComputerPartVisitorImpl());
   }
}
