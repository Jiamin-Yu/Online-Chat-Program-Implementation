package chat.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The network layer of the chat server. Takes care of processing both the connection requests and
 * message handling.
 */
public class ServerNetworkConnection {
  private static final int PORT = 8080;
  private ServerSocket serverSocket;
  private HashMap<Integer, Socket> socketHashMap;
  private JSONObject jsonObject;
  private HashSet<String> usedNickname;
  private String officialUsername;
  private Object lock = new Object();


  /**
   * Construct a ServerNetworkConnect.
   */
  public ServerNetworkConnection() {
    usedNickname = new HashSet<>();
    socketHashMap = new HashMap<>();
  }

  /**
   * Start the network-connection such that clients can establish a connection to this server.
   */
  public void start() throws IOException {
    serverSocket = new ServerSocket(PORT);

    Thread thread = new Thread() {
      public void run() {
        int counter = 0;
        while (true) {
          try {
            Socket clientSocket = serverSocket.accept();
            int socketCounter = counter++;

            //only allow one thread to access the hashmap of client sockets
            //prevent consistency problems.
            synchronized (lock) {
              socketHashMap.put(socketCounter, clientSocket);
            }
            Thread clientThread = new Thread() {
              public void run() {
                try {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(
                      clientSocket.getInputStream(), UTF_8));
                  OutputStreamWriter writer = new OutputStreamWriter(
                      clientSocket.getOutputStream(), UTF_8);

                  while (!clientSocket.isClosed()) {
                    String readLine = reader.readLine();

                    jsonObject = new JSONObject(readLine);
                    String getType = (String) jsonObject.get("type");
                    //client might send different kinds of messages to serve
                    //make sure that the message is a login message
                    if (Objects.equals(getType, "login")) {
                      String loginNickname = (String) jsonObject.get("nick");

                      //only allow one thread to access the set of used nickname
                      //and the hashmap of client sockets
                      //prevent consistency problems
                      synchronized (lock) {
                        if (!usedNickname.contains(loginNickname)) {
                          //login is successful, add new nickname to the list of used nickname
                          //assign the login nickname to the official username
                          usedNickname.add(loginNickname);
                          officialUsername = loginNickname;
                          jsonObject = new JSONObject();
                          jsonObject.put("type", "login success");
                          writer.write(jsonObject + System.lineSeparator());
                          writer.flush();

                          //the server sends to all other clients messages to inform that
                          //a new participant has joined.
                          for (HashMap.Entry<Integer, Socket> socketSet :
                              socketHashMap.entrySet()) {
                            if (socketSet.getKey() != socketCounter) {
                              Socket remainClientSocket = socketSet.getValue();
                              writer = new OutputStreamWriter(
                                  remainClientSocket.getOutputStream(), UTF_8);
                              jsonObject = new JSONObject();
                              jsonObject.put("type", "user joined");
                              jsonObject.put("nick", officialUsername);
                              writer.write(jsonObject + System.lineSeparator());
                              writer.flush();
                            }

                          }

                        } else {
                          //if the login nickname is used, login in failed
                          jsonObject = new JSONObject();
                          jsonObject.put("type", "login failed");
                          writer.write(jsonObject + System.lineSeparator());
                          writer.flush();
                        }
                      }
                    }

                    //this client's message is posted to other clients
                    if (Objects.equals(getType, "post message")) {
                      String content = (String) jsonObject.get("content");
                      for (HashMap.Entry<Integer, Socket> socketSet : socketHashMap.entrySet()) {
                        if (socketSet.getKey() != socketCounter) {
                          Socket remainClientSocket = socketSet.getValue();
                          writer = new OutputStreamWriter(
                              remainClientSocket.getOutputStream(), UTF_8);
                          jsonObject = new JSONObject();
                          jsonObject.put("type", "message");
                          jsonObject.put("time", new Date());
                          jsonObject.put("nick", officialUsername);
                          jsonObject.put("content", content);
                          writer.write(jsonObject + System.lineSeparator());
                          writer.flush();
                        }
                      }
                    }

                    synchronized (lock) {
                      //the client ask the server to close the corresponding client socket
                      //close the client socket
                      //remove the socket from the hashmap of client sockets
                      //remove the client's username from the set of used nickname
                      if (Objects.equals(getType, "close client socket")) {
                        String leftUser = (String) jsonObject.get("nick");

                        for (HashMap.Entry<Integer, Socket> socketSet : socketHashMap.entrySet()) {
                          if (socketSet.getKey() != socketCounter) {
                            System.out.print("Attention!" + socketSet + "\n");
                            jsonObject = new JSONObject();
                            jsonObject.put("type", "user left");
                            jsonObject.put("nick", leftUser);
                            Socket remainClientSocket = socketSet.getValue();
                            writer = new OutputStreamWriter(
                                remainClientSocket.getOutputStream(), UTF_8);
                            writer.write(jsonObject + System.lineSeparator());
                            writer.flush();
                          }
                        }
                        usedNickname.remove(leftUser);
                        clientSocket.close();
                        socketHashMap.remove(socketCounter);
                      }
                    }
                  }
                } catch (IOException | JSONException e) {
                  e.printStackTrace();
                }
              }

            };

            clientThread.start();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    };
    thread.start();
  }

  /**
   * Stop the network-connection.
   */
  public void stop() throws IOException {
    serverSocket.close();
  }
}
