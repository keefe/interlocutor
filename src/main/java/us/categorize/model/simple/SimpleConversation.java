package us.categorize.model.simple;

import java.util.ArrayList;
import java.util.List;

import us.categorize.model.Conversation;
import us.categorize.model.Message;

public class SimpleConversation implements Conversation {
	
	private final ArrayList<Message> messages = new ArrayList<Message>();

	@Override
	public boolean listen(Message message) {
		if(messages.size()==0 || message.getTimestampSeconds() > messages.get(messages.size()-1).getTimestampSeconds()) {
			messages.add(message);
		} else {
			int where=0; //TODO this is super simple but validate it anyway since it's before 7am
			for(; where<messages.size();where++) {
				if(messages.get(where).getTimestampSeconds() > message.getTimestampSeconds()) {
					break;
				}
			}
			messages.add(where, message);
		}
		return true;
	}

	@Override
	public List<Message> content() {
		return messages;
	}

	@Override
	public Message latest() {
		return messages.size()==0 ? null : messages.get(messages.size()-1);
	}

}
