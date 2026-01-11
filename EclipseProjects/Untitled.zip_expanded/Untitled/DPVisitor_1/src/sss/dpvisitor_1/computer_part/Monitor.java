package sss.dpvisitor_1.computer_part;

import sss.dpvisitor_1.computer_part_visitor.ComputerPartVisitor;

public class Monitor implements ComputerPart {

	@Override
	public void accept(ComputerPartVisitor computerPartVisitor) {
		computerPartVisitor.visit(this);
	}
}
