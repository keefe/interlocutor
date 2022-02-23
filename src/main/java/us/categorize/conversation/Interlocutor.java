package us.categorize.conversation;

import com.slack.api.model.Message;

public interface Interlocutor {

	void listen(Message message);
	
}
