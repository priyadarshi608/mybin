package sss.dpvisitor_1.computer_part_visitor;

import sss.dpvisitor_1.computer_part.Computer;
import sss.dpvisitor_1.computer_part.Keyboard;
import sss.dpvisitor_1.computer_part.Monitor;
import sss.dpvisitor_1.computer_part.Mouse;

public interface ComputerPartVisitor {
	public void visit(Computer computer);
	public void visit(Mouse mouse);
	public void visit(Keyboard keyboard);
	public void visit(Monitor monitor);
}
