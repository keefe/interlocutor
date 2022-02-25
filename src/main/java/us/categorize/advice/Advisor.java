package us.categorize.advice;

import us.categorize.model.Conversation;

public interface Advisor {
	//thinking about how to break down the different types of advice, like key phrase detection
	Advice advise(Conversation conversation);
}
