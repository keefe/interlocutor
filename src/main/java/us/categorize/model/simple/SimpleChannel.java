package us.categorize.model.simple;

import java.util.List;

import us.categorize.model.Channel;
import us.categorize.model.Conversation;
import us.categorize.model.Message;

public class SimpleChannel implements Channel<SimpleCriteria>{

	private String name;
	
	public SimpleChannel(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void listen(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Conversation> filter(SimpleCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}

}
