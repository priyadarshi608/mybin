import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import controller.CountryController;
import reqres.BaseResponse;
import reqres.country.CreateCountryRequest;
import reqres.country.DeleteCountryRequest;
import reqres.country.FindCountryRequest;
import reqres.country.FindCountryResponse;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CountryControllerTest {

	public CountryController countryController = new CountryController();
	
	@Test
	public void test_001_CreateCountry() {
		CreateCountryRequest request = new CreateCountryRequest();
		request.setCountryName("India");
		BaseResponse response = countryController.create(request);
		System.out.println("countryResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_002_FindCountry() {
		FindCountryRequest request = new FindCountryRequest();
		request.setId(1);
		FindCountryResponse response = countryController.find(request);
		System.out.println("findResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_003_DeleteCountry() {
		DeleteCountryRequest request = new DeleteCountryRequest();
		request.setId(1);
		BaseResponse response = countryController.delete(request);
		System.out.println("deleteResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_004_FindCountryAgain() {
		FindCountryRequest request = new FindCountryRequest();
		request.setId(1);
		FindCountryResponse response = countryController.find(request);
		System.out.println("findResponseAgain: " + response);
		assertTrue(!response.isStatus());
	}

}
