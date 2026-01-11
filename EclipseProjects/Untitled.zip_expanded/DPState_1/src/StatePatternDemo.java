
public class StatePatternDemo {
	public static void main(String[] args) {
		PlayingState playingState = new PlayingState();
		MP3PlayerContext mp3PlayerContext = new MP3PlayerContext(playingState);
		while (true) {
			mp3PlayerContext.play();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
