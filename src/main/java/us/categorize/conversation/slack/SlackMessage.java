package us.categorize.conversation.slack;

import us.categorize.model.Message;
import us.categorize.model.User;
import us.categorize.model.simple.SimpleUser;

public class SlackMessage implements Message {

	private final com.slack.api.model.Message slackMessage;
	private final SimpleUser user;
	private final String channel;
	private final long secondsTS;
	
	public SlackMessage(String channel, com.slack.api.model.Message slackMessage) {
		this.slackMessage = slackMessage;
		this.channel = channel;
		user = new SimpleUser();
		user.setId(slackMessage.getUser());
		user.setName(slackMessage.getUsername());
		secondsTS = (long) Double.parseDouble(slackMessage.getTs());
	}
	
	@Override
	public String getId() {
		return slackMessage.getTs();
	}

	@Override
	public String getText() {
		return slackMessage.getText();
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
		//this getChannel is null for some reason
		//return slackMessage.getChannel();
		return channel;
	}

	@Override
	public String getRepliesToId() {
		return slackMessage.getThreadTs();
	}

	@Override
	public String getThreadId() {
		return slackMessage.getThreadTs();
	}

}
