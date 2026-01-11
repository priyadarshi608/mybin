import java.util.Optional;

public class Java8Optional4 {

	public static void main(String[] args) {
		Optional<Person1> person1Optional = Optional.of(new Person1("000000000000"));
		doSome(person1Optional);
	}

	static void doSome(Optional<Person1> person){
		/*and here you want to retrieve some property phone out of person
		    you may write something like this:
		 */
		Optional<String> phone = person.map((p)->p.getPhone());
		phone.ifPresent((ph)->dial(ph));
	}

	static void dial(String phone) {
		System.out.println(phone);
	}
}

class Person1 {
	private String phone;

	public Person1(String phone) {
		this.phone = phone;
	}
	
	public String getPhone() {
		return phone;
	}
}