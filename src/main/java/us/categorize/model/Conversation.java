package us.categorize.model;

import java.util.List;

//I am tempted to make this an iterable of Message
public interface Conversation {
	boolean add(Message message);//a message may not be relevant to this conversation
	List<Message> content();//mixed feelings about content() vs getContent(), it's not exactly a bean
	Message latest();
}
