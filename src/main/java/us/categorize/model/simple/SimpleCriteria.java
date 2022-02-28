package us.categorize.model.simple;

public class SimpleCriteria {
	//if this many seconds pass, it's a new conversation
	private int continuitySeconds = 60*10;

	public int getContinuitySeconds() {
		return continuitySeconds;
	}

	public void setContinuitySeconds(int continuitySeconds) {
		this.continuitySeconds = continuitySeconds;
	}
}
