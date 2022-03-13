package us.categorize.advice;

import us.categorize.model.Conversation;

public interface Advisor {
	<C> SentimentAdvice detectSentiment(Conversation<C> conversation);
	
	<C> KeyphraseAdvice detectKeyphrases(Conversation<C> conversation);
}
