import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadBalancerImpl implements LoadBalancer {
	
	private final Map<String, Map<String, Service>> serviceInstanceMap = new HashMap<>();

	@Override
	public String registerNewService(String serviceId) {
		if (serviceId == null || serviceId.equals("")) {
			throw new InvalidServiceException("serviceId not valid.");
		}
		if (serviceInstanceMap.get(serviceId) != null) {
			throw new InvalidServiceException("service already registered.");
		} else {
			Map<String, Service> instanceIdAndInstanceMap = new HashMap<>();
			serviceInstanceMap.put(serviceId, instanceIdAndInstanceMap);
		}
		return serviceId;
	}

	@Override
	public String registerNewServiceInstance(String serviceId, Service service) {
		Map<String, Service> instanceIdAndInstanceMap = serviceInstanceMap.get(serviceId);
		if (instanceIdAndInstanceMap == null) {
			throw new InvalidServiceException("service not registered.");
		} else {
			instanceIdAndInstanceMap.get(service.getServiceName());
		}
		return instanceId;
	}

	@Override
	public Response getResolved(Request request) {
		// TODO Auto-generated method stub
		return null;
	}
}
