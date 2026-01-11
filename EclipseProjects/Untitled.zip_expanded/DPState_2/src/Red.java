
public class Red implements State {
	
    public void changeState(TrafficLight light) {
        light.state = new Green();
    }

    public void reportState() {
        System.out.println("Red Light");
    }
}
