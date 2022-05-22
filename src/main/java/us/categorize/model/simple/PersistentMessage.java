package us.categorize.model.simple;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import us.categorize.model.Message;
import us.categorize.model.User;

@Entity
@Table(name="messages")
public class PersistentMessage implements Message {

  @Id
  private String id;
  private String repliesToId;
  private String text;
  private String channel;
  private String threadId;
  private long timestampSeconds;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRepliesToId() {
    return repliesToId;
  }

  public void setRepliesToId(String repliesToId) {
    this.repliesToId = repliesToId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public long getTimestampSeconds() {
    return timestampSeconds;
  }

  public void setTimestampSeconds(long timestampSeconds) {
    this.timestampSeconds = timestampSeconds;
  }

  public String getThreadId() {
    return threadId;
  }

  public void setThreadId(String threadId) {
    this.threadId = threadId;
  }

  @Override
  public User getUser() {
    return null;
  }


}
