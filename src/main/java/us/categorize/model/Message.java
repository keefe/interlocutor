package us.categorize.model;

//think through interface vs class
public interface Message {
	String getId();
	String getText();
	User getUser();
	long getTimestampSeconds();
}
