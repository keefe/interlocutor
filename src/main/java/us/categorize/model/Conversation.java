package us.categorize.model;

import java.util.List;

// I am tempted to make this an iterable of Message?
// relationship to collections interfaces, JPA?
// in general the model is this is a list of messages sorted by timestamp
// conceptually, threads are implied on top of this and it being a thread is dependent on how you look at the conversation
// another conceptual idea candidate is to model a conversation as a partially remote object, that when filtered can reach 
// out to JDBC or to the source (slack, reddit, etc) and potentially load them into memory for further manipulation]
// should this take an interlocutor object that is used to query against the underlying database?
// then some decorator pattern or something that includes layers of caching etc?
public interface Conversation<C> {
  boolean add(Message message);
  // a message may not be relevant to this conversation

  List<Message> content();
  // mixed feelings about content() vs getContent(), it's not exactly a bean

  Message latest();

  String getName();

  void listen(Message message);

  List<Conversation<C>> filter(C criteria);
}
