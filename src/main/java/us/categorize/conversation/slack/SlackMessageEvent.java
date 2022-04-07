package us.categorize.conversation.slack;

import com.slack.api.model.event.MessageEvent;

import us.categorize.model.Message;
import us.categorize.model.User;
import us.categorize.model.simple.SimpleUser;

public class SlackMessageEvent implements Message {

	private MessageEvent event;
	private final SimpleUser user;
	private final long secondsTS;
	private final String channel;
	
	public SlackMessageEvent(String channel, MessageEvent event) {
		this.event = event;
		user = new SimpleUser();
		user.setId(event.getUser());//where did username go?
		user.setName(event.getUser());
		secondsTS = (long) Double.parseDouble(event.getTs());
		this.channel = channel;

	}
	@Override
	public String getId() {
		return event.getTs();
	}

	@Override
	public String getText() {
		return event.getText();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public long getTimestampSeconds() {
		return secondsTS;
	}
	@Override
	public String getChannel() {
		return channel;
	}
	@Override
	public String getRepliesToId() {
		return event.getThreadTs();
	}
}
