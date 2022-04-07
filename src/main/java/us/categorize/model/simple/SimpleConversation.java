package us.categorize.model.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import us.categorize.model.Conversation;
import us.categorize.model.Message;

public class SimpleConversation implements Conversation<SimpleCriteria> {
	
	private final ArrayList<Message> messages = new ArrayList<Message>();
	private String name;
	

	public SimpleConversation(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean add(Message message) {
		Message existing = messages.stream()
								.filter(m -> m.getId().equals(message.getId()))
								.findAny()
								.orElse(null);
		if(existing!=null) return false;
		messages.add(message);
		Collections.sort(messages, new Comparator<Message>() {
			@Override
			public int compare(Message m1, Message m2) {
				//TODO reproduce the subtle bug here
				return (int) (m1.getTimestampSeconds() - m2.getTimestampSeconds());
			}
		});
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
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void listen(Message message) {
		add(message);
	}

	@Override
	public List<Conversation> filter(SimpleCriteria criteria) {
	    
		if(criteria==null) {
	    	return Arrays.asList(this);
	    }
	    //since this operation is dependent on the internal structure
		//e.g. arraylist implements this different than linkedlist
		//leaning towards killing the channel abstraction and moving all this 
		//to conversation
		Conversation latest = new SimpleConversation(name);
		for(Message m : content()) {
			if(latest.latest()!=null && (m.getTimestampSeconds() - latest.latest().getTimestampSeconds()) > criteria.getContinuitySeconds()) {
				latest = new SimpleConversation(name); //alternatively return full list of convos
			}
			latest.add(m);
		}
		return Arrays.asList(latest);
	}


}
