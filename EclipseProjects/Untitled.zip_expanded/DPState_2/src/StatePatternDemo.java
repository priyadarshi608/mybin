
public class StatePatternDemo {

	public static void main(String[] args) {
		TrafficLight trafficLight = new TrafficLight();
		while (true) {
			trafficLight.Reportstate();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			trafficLight.ChangeState();
		}
	}

}
