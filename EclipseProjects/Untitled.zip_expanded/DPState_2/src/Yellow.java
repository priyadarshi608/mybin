
public class Yellow implements State {
    public void changeState(TrafficLight light)
    {
        light.state = new Red();
    }

    public void reportState() {
        System.out.println("Yellow Light");
    }
}
