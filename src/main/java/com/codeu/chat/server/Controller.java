// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.server;

import java.util.Collection;
import java.io.File;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.PasswordGenerator;

public final class Controller implements RawController, BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final Model model;
  private final Uuid.Generator uuidGenerator;

  private MessageDatabase msgData = new MessageDatabase();
  private RetrieveMessageData msgRetrieve;

  public Controller(Uuid serverId, Model model) {
    this.model = model;
    this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());

    msgData.createUserTable();
    msgData.createConversationTable();
    msgData.createMessageTable();

    msgRetrieve = new RetrieveMessageData(msgData);
    msgRetrieve.retrieveUser(this);
    msgRetrieve.retrieveConversation(this);
    msgRetrieve.retrieveMessage(this); 
  }

  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {
    Uuid newId = createId();
    Time time = Time.now();

    msgData.insertMessageTable(newId.toDataString(), author.toDataString(), 
        conversation.toDataString(), body, time.inMs());

    return newMessage(newId, author, conversation, body, time);
  }

  @Override
  public User newUser(String name, String password) {
    Uuid newId = createId();
    Time time = Time.now();
    return newUser(newId, name, password, time);
  }

  @Override
  public Conversation newConversation(String title, Uuid owner) {
    Uuid newId = createId();
    Time time = Time.now();
    msgData.insertConversationTable(newId.toDataString(), title, owner.toDataString(), time.inMs());
    return newConversation(newId, title, owner, time);
  }

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body, Time creationTime) {

    final User foundUser = model.userById().first(author);
    final Conversation foundConversation = model.conversationById().first(conversation);

    Message message = null;

    if (foundUser != null && foundConversation != null && isIdFree(id)) {

      message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, author, body);
      model.add(message);
      LOG.info("Message added: %s", message.id);

      // Find and update the previous "last" message so that it's "next" value
      // will point to the new message.

      if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

        // The conversation has no messages in it, that's why the last message is NULL (the first
        // message should be NULL too. Since there is no last message, then it is not possible
        // to update the last message's "next" value.

      } else {
        final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
        lastMessage.next = message.id;
      }

      // If the first message points to NULL it means that the conversation was empty and that
      // the first message should be set to the new message. Otherwise the message should
      // not change.

      foundConversation.firstMessage =
          Uuid.equals(foundConversation.firstMessage, Uuid.NULL) ?
          message.id :
          foundConversation.firstMessage;

      // Update the conversation to point to the new last message as it has changed.

      foundConversation.lastMessage = message.id;

      if (!foundConversation.users.contains(foundUser)) {
        foundConversation.users.add(foundUser.id);
      }
    }

    return message;
  }

  @Override
  public User newUser(Uuid id, String name, String password, Time creationTime) {

    byte[] salt = PasswordGenerator.getSalt();
    byte[] hashedPassword = PasswordGenerator.hash(password.toCharArray(), salt);

    if (isIdFree(id)) {
      msgData.insertUser(id.toDataString(), name, salt, hashedPassword, creationTime.inMs());
    }

    return loadUser(id, name, salt, hashedPassword, creationTime);
  }

  public User loadUser(Uuid id, String name, byte[] salt, byte[] hashedPassword, Time creationTime) {

    User user = null;

    if (isIdFree(id)) {

      user = new User(id, name, salt, hashedPassword, creationTime);
      model.add(user);

      LOG.info(
          "newUser success (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);

    } else {

      LOG.info(
          "newUser fail - id in use (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);
    }

    return user;
  }  

  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

    final User foundOwner = model.userById().first(owner);

    Conversation conversation = null;

    if (foundOwner != null && isIdFree(id)) {
      conversation = new Conversation(id, owner, creationTime, title);
      model.add(conversation);

      LOG.info("Conversation added: " + conversation.id);
    }

    return conversation;
  }

  private Uuid createId() {

    Uuid candidate;

    for (candidate = uuidGenerator.make();
         isIdInUse(candidate);
         candidate = uuidGenerator.make()) {

     // Assuming that "randomUuid" is actually well implemented, this
     // loop should never be needed, but just incase make sure that the
     // Uuid is not actually in use before returning it.

    }

    return candidate;
  }

  private boolean isIdInUse(Uuid id) {
    return model.messageById().first(id) != null ||
           model.conversationById().first(id) != null ||
           model.userById().first(id) != null;
  }

  public void deleteEverything() {
    msgData.deleteUsers();
    msgData.deleteConversation();
    msgData.deleteMessage();
    msgData.closeConnection();
  }

  private boolean isIdFree(Uuid id) { return !isIdInUse(id); }

}
