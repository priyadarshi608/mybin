public class StandbyState implements State {
	public void pressPlay(MP3PlayerContext context) {
		System.out.println("stand by state..");
		context.setState(new PlayingState());
	}
}