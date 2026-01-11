import java.util.Optional;
import java.util.Random;

public class Java8Optional7 {

	public static void main(String[] args) {
		
		// non empty string map to its length -> we get the length as output (18)
		Optional<String> stringOptional = Optional.of( "loooooooong string" );
		Optional<Integer> sizeOptional = stringOptional.map( String::length ); //map from Optional to Optional
		System.out.println( "size of string " + sizeOptional.orElse( 0 ) );

		// empty string map to its length -> we get 0 as length
		Optional<String> stringOptionalNull = Optional.ofNullable( null );
		Optional<Integer> sizeOptionalNull = stringOptionalNull.map( x -> x.length()  ); // we can use Lambdas as we want
		System.out.println( "size of string " + sizeOptionalNull.orElse( 0 ) );
	}

}
