package us.categorize.model.simple;

import java.util.Arrays;
import java.util.List;

import us.categorize.model.Channel;
import us.categorize.model.Conversation;
import us.categorize.model.Message;

//the way this is structured makes me want to retire the whole channel concept
//maybe make Channel as subclass of conversation? what is different?
public class SimpleChannel implements Channel<SimpleCriteria>{

	private String name;
	
	private Conversation conversation;
	
	public SimpleChannel(String name) {
		conversation = new SimpleConversation();
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void listen(Message message) {
		conversation.add(message);
	}

	@Override
	public List<Conversation> filter(SimpleCriteria criteria) {
	    
		if(criteria==null) {
	    	return Arrays.asList(conversation);
	    }
	    //since this operation is dependent on the internal structure
		//e.g. arraylist implements this different than linkedlist
		//leaning towards killing the channel abstraction and moving all this 
		//to conversation
		Conversation latest = new SimpleConversation();
		for(Message m : conversation.content()) {
			if(latest.latest()!=null && (m.getTimestampSeconds() - latest.latest().getTimestampSeconds()) > criteria.getContinuitySeconds()) {
				latest = new SimpleConversation(); //alternatively return full list of convos
			}
		}
		return Arrays.asList(conversation);
	}

}
