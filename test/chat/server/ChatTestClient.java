package chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * construct a ChatTestClient.
 */
public class ChatTestClient {

  private static final String ADDRESS = "localhost";
  private static final int PORT = 8080;

  private final Socket socket;
  private final BufferedWriter writer;
  private final BufferedReader reader;

  /**
   * Construct a ChatTestClient.
   *
   * @throws IOException the Exception that is thrown.
   */
  public ChatTestClient() throws IOException {
    socket = new Socket(ADDRESS, PORT);
    writer = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    reader =
        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
  }

  /**
   * send a Json message.
   */
  public void send(JSONObject message) throws IOException {
    writer.write(message + System.lineSeparator());
    writer.flush();
  }

  /**
   * receive all Json messages.
   *
   * @return list of Json messages.
   * @throws IOException the exception that is throwns.
   */
  public List<JSONObject> receiveAll() throws IOException {
    List<JSONObject> messages = new ArrayList<>();
    while (reader.ready()) {
      messages.add(receive());
    }
    return messages;
  }

  /**
   * receive a Json message.
   *
   * @return Json message.
   * @throws IOException the exception that is thrown.
   */
  public JSONObject receive() throws IOException {
    try {
      String line = reader.readLine();
      if (line == null || line.isEmpty()) {
        return null;
      }

      return new JSONObject(line);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Unable to parse message as JSON object", e);
    }
  }

  /**
   * close the socket.
   *
   * @throws IOException the exception that is thrown.
   */
  public void close() throws IOException {
    socket.close();
  }
}
