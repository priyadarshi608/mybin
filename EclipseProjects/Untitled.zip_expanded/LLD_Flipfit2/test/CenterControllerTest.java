import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;

import controller.CenterController;
import model.Location;
import reqres.BaseResponse;
import reqres.center.CreateCenterRequest;
import reqres.center.DeleteCenterRequest;
import reqres.center.FindCenterRequest;
import reqres.center.FindCenterResponse;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CenterControllerTest {
	
	public CenterController centerController = new CenterController();
	
	@Test
	public void test_001_CreateCenter() {
		CreateCenterRequest request = new CreateCenterRequest();
		request.setCenterName("abc");
		request.setCenterEmail("abc@gmail.com");
		request.setCityId(1);
		Location location = new Location();
		location.setLatitude(10.0);
		location.setLongitude(10.0);
		request.setLocation(location);
		BaseResponse response = centerController.create(request);
		System.out.println("createResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_002_FindCenter() {
		FindCenterRequest request = new FindCenterRequest();
		request.setId(1);
		FindCenterResponse response = centerController.find(request);
		System.out.println("findResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_003_DeleteCenter() {
		DeleteCenterRequest request = new DeleteCenterRequest();
		request.setId(1);
		BaseResponse response = centerController.delete(request);
		System.out.println("deleteResponse: " + response);
		assertTrue(response.isStatus());
	}
	
	@Test
	public void test_004_FindCenterAgain() {
		FindCenterRequest request = new FindCenterRequest();
		request.setId(1);
		FindCenterResponse response = centerController.find(request);
		System.out.println("findResponseAgain: " + response);
		assertTrue(!response.isStatus());
	}

}
