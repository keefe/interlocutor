package us.categorize.model;

//think through interface vs class
public interface Message {
	String getId();
	String getRepliesToId();
	String getText();
	String getChannel();
	User getUser();
	long getTimestampSeconds();
}
