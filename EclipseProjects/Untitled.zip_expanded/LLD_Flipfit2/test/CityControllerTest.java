import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import controller.CityController;
import reqres.BaseResponse;
import reqres.city.CreateCityRequest;
import reqres.city.DeleteCityRequest;
import reqres.city.FindCityRequest;
import reqres.city.FindCityResponse;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CityControllerTest {

	public CityController cityController = new CityController();
	
	@Test
	public void test_001_CreateCity() {
		CreateCityRequest request = new CreateCityRequest();
		request.setCityName("Noida");
		request.setCountryId(1);
		BaseResponse response = cityController.create(request);
		System.out.println("cityResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_002_FindCity() {
		FindCityRequest request = new FindCityRequest();
		request.setId(1);
		FindCityResponse response = cityController.find(request);
		System.out.println("findResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_003_DeleteCity() {
		DeleteCityRequest request = new DeleteCityRequest();
		request.setId(1);
		BaseResponse response = cityController.delete(request);
		System.out.println("deleteResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_004_FindCityAgain() {
		FindCityRequest request = new FindCityRequest();
		request.setId(1);
		FindCityResponse response = cityController.find(request);
		System.out.println("findResponseAgain: " + response);
		assertTrue(!response.isStatus());
	}

}
