package chat.client.view.chatview;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A chat message sent by a user at a specific point in time.
 */
public class UserTextMessage extends ChatEntry {

  private final String source;

  private final Date time;

  private final String content;

  /**
   * construct a UserTextMessage.
   *
   * @param source the source of the UserTextMessage.
   * @param time the time when the UserTextMessage was sent.
   * @param content the content of the UserTextMessage.
   */
  public UserTextMessage(String source, Date time, String content) {
    this.source = source;
    this.time = time;
    this.content = content;
  }


  public String getSource() {
    return source;
  }

  public Date getTime() {
    Date timeCopy = (Date) time.clone();
    return timeCopy;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    String dateString = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.SHORT, Locale.GERMANY).format(time);
    return String.format("%s (%s): %s", source, dateString, content);
  }
}
