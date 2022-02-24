package us.categorize.conversation.slack;

import us.categorize.model.Message;
import us.categorize.model.User;
import us.categorize.model.simple.SimpleUser;

public class SlackMessage implements Message {

	private final com.slack.api.model.Message slackMessage;
	private final SimpleUser user;
	private final long secondsTS;
	
	public SlackMessage(com.slack.api.model.Message slackMessage) {
		this.slackMessage = slackMessage;
		user = new SimpleUser();
		user.setId(slackMessage.getUser());
		user.setName(slackMessage.getUsername());
		secondsTS = (long) Double.parseDouble(slackMessage.getTs());
	}
	
	@Override
	public String getId() {
		return slackMessage.getClientMsgId();
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

}