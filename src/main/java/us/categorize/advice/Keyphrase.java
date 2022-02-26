package us.categorize.advice;

public class Keyphrase {
	private final double score;
	private final String phrase;

	public Keyphrase(double score, String phrase) {
		this.score = score;
		this.phrase = phrase;
	}
	
	public double getScore() {
		return score;
	}

	public String getPhrase() {
		return phrase;
	}
}
