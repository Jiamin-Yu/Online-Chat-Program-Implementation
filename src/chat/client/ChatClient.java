package chat.client;

import chat.client.controller.ChatController;
import chat.client.model.ChatClientModel;
import chat.client.model.ClientNetworkConnection;
import chat.client.view.ChatFrame;
import java.io.IOException;
import org.json.JSONException;

/**
 * Starts the chat-client.
 */
public class ChatClient {
  /**
   * starts the ChatClient program.
   *
   * @param args command-line arguments.
   * @throws IOException the IOException that is thrown.
   * @throws JSONException the JSONException that is thrown.
   */

  public static void main(String[] args) throws IOException, JSONException {

    ChatClientModel model = new ChatClientModel();
    ChatController controller = new ChatController(model);
    ChatFrame chatFrame = new ChatFrame(controller, model);

    model.addPropertyChangeListener(chatFrame);

    ClientNetworkConnection connection = new ClientNetworkConnection(model);
    model.setConnection(connection);
    connection.start();

    chatFrame.setVisible(true);
  }
}
