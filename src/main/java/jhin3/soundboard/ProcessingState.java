package jhin3.soundboard;

public class ProcessingState {

	public enum State {
		FADEIN, PLAYING, FADEOUT, STOPPED
	}

	private State state;

	public ProcessingState(State state) {
		setState(state);
	}

	public boolean isFadingIn() {
		return state == State.FADEIN;
	}

	public boolean isPlaying() {
		return state == State.PLAYING;
	}

	public boolean isFadingOut() {
		return state == State.FADEOUT;
	}

	public boolean isStopped() {
		return state == State.STOPPED;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

}
