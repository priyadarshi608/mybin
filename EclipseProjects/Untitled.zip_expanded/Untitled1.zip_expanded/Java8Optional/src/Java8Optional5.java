import java.util.Optional;

public class Java8Optional5 {

	public static void main(String[] args) {
		Optional<Person2> person2Optional = Optional.of(new Person2("000000000000"));
		doSome(person2Optional);
	}

	static void doSome(Optional<Person2> person){
		/*and here you want to retrieve some property phone out of person
		    you may write something like this:
		 */
		Optional<String> phone = person.flatMap((p)->p.getPhone());
		phone.ifPresent((ph)->dial(ph));
	}

	static void dial(String phone) {
		System.out.println(phone);
	}
}

class Person2 {
	private Optional<String> phone;

	public Person2(String phone) {
		this.phone = Optional.of(phone);
	}
	
	public Optional<String> getPhone() {
		return phone;
	}
}