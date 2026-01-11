
public class Green implements State {
	
    public void changeState(TrafficLight light) {
        light.state = new Yellow();
    }

    public void reportState() {
        System.out.println("Green light");
    }
}
