package us.categorize.advice.aws.comprehend;

import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import us.categorize.advice.SentimentAdvice;

public class ComprehendSentimentAdvice implements SentimentAdvice {
	private final DetectSentimentResult awsResult;

	public ComprehendSentimentAdvice(DetectSentimentResult awsResult) {
		this.awsResult = awsResult;
	}

	@Override
	public String getSentiment() {
		return awsResult.getSentiment();
	}

	@Override
	public double getPositive() {
		return awsResult.getSentimentScore().getPositive();
	}

	@Override
	public double getNegative() {
		return awsResult.getSentimentScore().getNegative();
	}

	@Override
	public double getNeutral() {
		return awsResult.getSentimentScore().getNeutral();
	}

	@Override
	public double getMixed() {
		return awsResult.getSentimentScore().getMixed();
	}
}
