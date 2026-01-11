package sss.dpvisitor_1.computer_part;

import sss.dpvisitor_1.computer_part_visitor.ComputerPartVisitor;

public interface ComputerPart {
	public void accept(ComputerPartVisitor computerPartVisitor);
}
