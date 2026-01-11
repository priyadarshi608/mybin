import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APIRateLimiter {
	
	private static HashMap<String, APIRateLimiter> rateLimiterLibrary = new HashMap<String, APIRateLimiter>();
	
	private String apiId;
	private long duration;
	private int apiRateLimit;
	private HashMap<String, List<Long>> userAccesses = new HashMap<String, List<Long>>();
	
	private APIRateLimiter(String apiId, int apiRateLimit) {
		this.apiId = apiId;
		this.apiRateLimit = apiRateLimit;
	}
	
	public APIRateLimiter getAPIRateLimiter(String apiId, long duration, int apiRateLimit) {
		APIRateLimiter apiRateLimiter = new APIRateLimiter(apiId, apiRateLimit);
		rateLimiterLibrary.put(apiId, apiRateLimiter);
		return apiRateLimiter;
	}
	
	public boolean isAllowed(String apiId, String userId) {
		boolean isAllowed = false;
		APIRateLimiter apiRateLimiter = rateLimiterLibrary.get(apiId);
		if (apiRateLimiter.userAccesses.containsKey(userId)) {
			List<Long> userAccessesTimestamps = apiRateLimiter.userAccesses.get(userId);
			Long currentTimestamp = 123L;
			Long earliestAllowedTime = currentTimestamp - duration;
			if (userAccessesTimestamps.size() < apiRateLimit) {
				userAccessesTimestamps.add(currentTimestamp);
				isAllowed = true;
			} else if (userAccessesTimestamps.size() == apiRateLimit) {
				Long earliestHitTimestamp = userAccessesTimestamps.get(0);
				if (earliestHitTimestamp < earliestAllowedTime) {
					userAccessesTimestamps.remove(0);
					userAccessesTimestamps.add(currentTimestamp);
					isAllowed = true;
				} else {
					System.out.println("Not Allowed.");
				}
			} else {
				System.out.println("Problem detected. Not allowed. Connect with operations team.");
			}
		} else {
			Long current_timestamp = 123L;
			List<Long> listUserAccesses = new ArrayList<Long>();
			listUserAccesses.add(current_timestamp);
			apiRateLimiter.userAccesses.put(userId, listUserAccesses);
			isAllowed = true;
		}
		return isAllowed;
	}

}
