public class PlayingState implements State {
	public void pressPlay(MP3PlayerContext context) {
		System.out.println("playing state..");
		context.setState(new StandbyState());
	}
}