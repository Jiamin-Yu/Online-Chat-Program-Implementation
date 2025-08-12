package chat.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * store JsonMessages.
 */
public enum JsonMessage {

  LOGIN("login"), LOGIN_SUCCESS("login success"), LOGIN_FAILED("login failed"),
  USER_JOINED("user joined"), POST_MESSAGE("post message"), MESSAGE("message"),
  USER_LEFT("user left");

  public static final String TYPE_FIELD = "type";

  public static final String NICK_FIELD = "nick";

  public static final String CONTENT_FIELD = "content";

  public static final String TIME_FIELD = "time";

  private final String jsonName;

  /**
   * construct a Json message.
   *
   * @param jsonName the name of the Json Message.
   */
  JsonMessage(String jsonName) {
    this.jsonName = jsonName;
  }

  /**
   * create a Json message of type of the parameter 'message'.
   *
   * @param message the Json message.
   *
   * @return Json message.
   */
  public static JsonMessage typeOf(JSONObject message) {
    String typeName;
    try {
      typeName = message.getString(TYPE_FIELD);
    } catch (JSONException e) {
      throw new IllegalArgumentException(String.format("Unknown message type '%s'", message), e);
    }

    Optional<JsonMessage> opt =
        Arrays.stream(JsonMessage.values()).filter(x -> x.getJsonName().equals(typeName))
            .findFirst();
    return opt.orElseThrow(
        () -> new IllegalArgumentException(String.format("Unknown message type '%s'", typeName)));
  }

  /**
   * create login message.
   *
   * @param nickname nickname of the login client.
   *
   * @return login message.
   */
  public static JSONObject login(String nickname) {
    try {
      return createMessageOfType(LOGIN).put(NICK_FIELD, nickname);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to create a json object.", e);
    }
  }

  /**
   * post message.
   *
   * @param content content of message.
   *
   * @return a Json message.
   */
  public static JSONObject postMessage(String content) {
    try {
      JSONObject message = createMessageOfType(POST_MESSAGE);
      message.put(CONTENT_FIELD, content);

      return message;
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to create a json object.", e);
    }
  }

  /**
   * create a Json message.
   *
   * @param type the type required
   *
   * @return a Json message.
   * @throws JSONException the exception that is thrown.
   */
  private static JSONObject createMessageOfType(JsonMessage type) throws JSONException {
    return new JSONObject().put(TYPE_FIELD, type.getJsonName());
  }

  /**
   * get Nickname of the Json object.
   *
   * @param object the Json object.
   *
   * @return the Nickname.
   */
  public static String getNickname(JSONObject object) {
    try {
      return object.getString(NICK_FIELD);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to read a json object.", e);
    }
  }

  /**
   * get the content of the Json object.
   *
   * @param object the Json object.
   *
   * @return the content.
   */
  public static String getContent(JSONObject object) {
    try {
      return object.getString(CONTENT_FIELD);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to read a json object.", e);
    }
  }

  /**
   * convert date to a string.
   *
   * @param date the Date needed to be converted.
   *
   * @return the corresponding string.
   */
  private static String convertDateToString(Date date) {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY)
        .format(date);
  }

  /**
   * convert a string to date.
   *
   * @param date string needed to be converted.
   *
   * @return the corresponding date.
   * @throws ParseException the exception that is thrown.
   */
  private static Date convertStringToDate(String date) throws ParseException {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY)
        .parse(date);
  }

  /**
   * get Jsonname.
   *
   * @return Json name.
   */
  public String getJsonName() {
    return jsonName;
  }
}
