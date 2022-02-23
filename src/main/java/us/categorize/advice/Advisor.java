package us.categorize.advice;

import us.categorize.model.Conversation;

public interface Advisor {
	Advice advise(Conversation conversation);
}
