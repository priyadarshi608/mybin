
public class TrafficLight {
	State state;

	public TrafficLight() {
		state = new Red();
	}
	
    public void ChangeState() {
        state.changeState(this);
    }
    
    public void Reportstate() {
        state.reportState();
    }
}
