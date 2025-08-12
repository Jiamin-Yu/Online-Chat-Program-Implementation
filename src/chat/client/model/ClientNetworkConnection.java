package chat.client.model;

import static java.nio.charset.StandardCharsets.UTF_8;

import chat.client.view.chatview.UserTextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The network-connection of the client. Establishes a connection to the server and takes
 * care of sending and receiving messages in JSON format.
 */
public class ClientNetworkConnection {

  private static final String HOST = "localhost";
  private static final int PORT = 8080;
  private Socket socket;
  private JSONObject jsonObject;
  private final ChatClientModel model;
  private String nickname;
  private OutputStreamWriter writer;


  /**
   * construct a ClientNetworkConnection.
   *
   * @param model the ChatClientModel.
   */
  public ClientNetworkConnection(ChatClientModel model) {
    this.model = model;

  }

  /**
   * Start the network connection.
   */
  public void start() throws IOException {
    socket = new Socket(HOST, PORT);

    //output message to server
    writer = new OutputStreamWriter(socket.getOutputStream(), UTF_8);

    //receive message from server
    //set new thread so that this doesn't bock the main client thread
    Thread readerThread = new Thread() {
      public void run() {
        try {
          BufferedReader reader =
              new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
          while (true) {
            String line = reader.readLine();
            jsonObject = new JSONObject(line);
            String getType = (String) jsonObject.get("type");

            if (Objects.equals(getType, "login success")) {
              model.loggedIn(nickname);
            }
            if (Objects.equals(getType, "login failed")) {
              model.loginFailed();
            }
            if (Objects.equals(getType, "user joined")) {
              String getNickname = (String) jsonObject.get("nick");
              model.userJoined(getNickname);
            }
            if (Objects.equals(getType, "user left")) {
              String getNickname = (String) jsonObject.get("nick");
              model.userLeft(getNickname);
            }
            if (Objects.equals(getType, "message")) {
              model.addTextMessage((String) jsonObject.get("nick"),
                  new Date(), (String) jsonObject.get("content"));
            }
          }
        } catch (IOException | JSONException e) {
          e.printStackTrace();
        }
      }
    };
    readerThread.start();

  }


  /**
   * Stop the network-connection.
   */
  public void stop() throws IOException {
    //inform the server that this client is logged off,
    //and this client socket will be closed right away.
    jsonObject = new JSONObject();
    try {
      jsonObject.put("type", "close client socket");
      jsonObject.put("nick", nickname);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    writer.write(jsonObject + System.lineSeparator());
    writer.flush();

    socket.close();
  }

  /**
   * Send a login-request to the server.
   *
   * @param nickname The name of the user that requests to log in.
   */
  public void sendLogin(String nickname) throws JSONException, IOException {
    this.nickname = nickname;
    jsonObject = new JSONObject();
    jsonObject.put("type", "login");
    jsonObject.put("nick", nickname);

    writer.write(jsonObject + System.lineSeparator());
    writer.flush();
  }

  /**
   * Send a chat message to the server.
   *
   * @param chatMessage The {@link UserTextMessage} containing the message of the user.
   */
  public void sendMessage(UserTextMessage chatMessage) {

    try {
      jsonObject = new JSONObject();
      jsonObject.put("type", "post message");
      jsonObject.put("content", chatMessage.getContent());
      writer.write(jsonObject + System.lineSeparator());
      writer.flush();

    } catch (JSONException | IOException e) {
      e.printStackTrace();
    }
  }
}
