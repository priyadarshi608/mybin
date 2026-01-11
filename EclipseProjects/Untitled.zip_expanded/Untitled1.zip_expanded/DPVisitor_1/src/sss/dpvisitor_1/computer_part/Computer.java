package sss.dpvisitor_1.computer_part;

import sss.dpvisitor_1.computer_part_visitor.ComputerPartVisitor;

public class Computer implements ComputerPart {

	ComputerPart[] parts;

	public Computer(){
		parts = new ComputerPart[] {new Mouse(), new Keyboard(), new Monitor()};		
	} 


	@Override
	public void accept(ComputerPartVisitor computerPartVisitor) {
		for (int i = 0; i < parts.length; i++) {
			parts[i].accept(computerPartVisitor);
		}
		computerPartVisitor.visit(this);
	}
}
