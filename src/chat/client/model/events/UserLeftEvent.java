package chat.client.model.events;

/**
 * Event that is sent by the model to the observers. It notifies the observers that a user has left
 * and that the model has adapted its state accordingly.
 */

public class UserLeftEvent extends ChatEvent {

  @Override
  public String getName() {
    return "UserLeftEvent";
  }
}
