import java.util.Optional;

public class Java8Optional3 {
	
	public static void main(String[] args) {
		Optional<String> s = Optional.of("input");
		System.out.println(s.map(Java8Optional3::getOutput));
		System.out.println(s.flatMap(Java8Optional3::getOutputOpt));
	}

	static Optional<String> getOutputOpt(String input) {
		return input == null ? Optional.empty() : Optional.of("output for " + input);
	}

	static String getOutput(String input) {
		return input == null ? null : "output for " + input;
	}
}
