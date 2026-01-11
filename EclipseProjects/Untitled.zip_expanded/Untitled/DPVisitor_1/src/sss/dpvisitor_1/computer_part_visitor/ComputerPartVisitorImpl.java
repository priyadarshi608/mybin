package sss.dpvisitor_1.computer_part_visitor;

import sss.dpvisitor_1.computer_part.Computer;
import sss.dpvisitor_1.computer_part.Keyboard;
import sss.dpvisitor_1.computer_part.Monitor;
import sss.dpvisitor_1.computer_part.Mouse;

public class ComputerPartVisitorImpl implements ComputerPartVisitor {

	@Override
	public void visit(Computer computer) {
		System.out.println("Displaying Computer.");
	}

	@Override
	public void visit(Mouse mouse) {
		System.out.println("Displaying Mouse.");
	}

	@Override
	public void visit(Keyboard keyboard) {
		System.out.println("Displaying Keyboard.");
	}

	@Override
	public void visit(Monitor monitor) {
		System.out.println("Displaying Monitor.");
	}
}
