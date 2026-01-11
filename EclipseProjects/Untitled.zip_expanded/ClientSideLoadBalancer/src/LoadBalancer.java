
public interface LoadBalancer {
	String registerNewService(String serviceId);
	String registerNewServiceInstance(String serviceId, String instanceId);
	Response getResolved(Request request);
}
