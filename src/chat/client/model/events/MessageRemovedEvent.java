package chat.client.model.events;


import chat.client.view.chatview.ChatEntry;

/**
 * Event that is sent by the model to the observers. It notifies about a message that has been
 * removed from the collection of stored messages.
 */
public class MessageRemovedEvent extends ChatEvent {
  @Override
  public String getName() {
    return "MessageRemovedEvent";
  }
}
