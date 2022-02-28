package us.categorize.model;

import java.util.List;

//TODO think about this generic here, as we get to this level of generality, things become complicated
//so, we start with something and we move on from there
//conversation vs channel abstraction is kind of mess
public interface Channel<C> {
	String getName();
	void listen(Message message);
	List<Conversation> filter(C criteria);
}
