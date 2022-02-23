package us.categorize.model.simple;

import java.util.ArrayList;
import java.util.List;

import us.categorize.model.Conversation;
import us.categorize.model.Message;

public class SimpleConversation implements Conversation {
	
	private final List<Message> messages = new ArrayList<Message>();

	@Override
	public boolean listen(Message message) {
		messages.add(message);
		return true;
	}

	@Override
	public List<Message> content() {
		return messages;
	}

}
