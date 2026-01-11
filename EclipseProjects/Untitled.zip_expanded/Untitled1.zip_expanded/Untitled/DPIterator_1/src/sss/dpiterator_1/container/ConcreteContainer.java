package sss.dpiterator_1.container;

public class ConcreteContainer implements Container {
	private String names[] = {"Robert" , "John" ,"Julie" , "Lora"};

	@Override
	public Iterator getIterator() {
		return new ContainerIterator();
	}

	private class ContainerIterator implements Iterator {

		int index;

		@Override
		public boolean hasNext() {

			if(index < names.length){
				return true;
			}
			return false;
		}

		@Override
		public Object next() {

			if(this.hasNext()){
				return names[index++];
			}
			return null;
		}		
	}
}
