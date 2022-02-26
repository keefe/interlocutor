package us.categorize.advice;

import us.categorize.model.Conversation;

public interface Advisor {
	SentimentAdvice detectSentiment(Conversation conversation);
	
	KeyphraseAdvice detectKeyphrases(Conversation conversation);
}
