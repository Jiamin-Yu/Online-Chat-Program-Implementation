package chat.client.model.events;

/**
 * Event that is sent by the model to the observers. It notifies the observers that
 * a user has joined and that the model has adapted its state accordingly.
 */

public class UserJoinEvent extends ChatEvent {
  @Override
  public String getName() {
    return "UserJoinEvent";
  }
}
