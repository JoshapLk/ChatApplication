package view;

import model.Chat;
import model.ChatMessage;
import model.User;
import rmi.ChatService;
import rmi.ClientCallback;
import rmi.ClientCallbackImpl;
import rmi.UserService;
import util.DataReceiver;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import util.RMIClient;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardController implements DataReceiver {

    @FXML private ListView<String> chatListView;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private TextField nicknameField;
    @FXML private TextField profilePicField;

    private User currentUser;
    private List<Chat> chatList;

    private ChatService chatService;
    private UserService userService;

    private int currentChatId = -1;

    private ClientCallbackImpl callback;

    private List<Integer> subscribedChatIds = new ArrayList<>();





    @FXML
    public void initialize() {
        try {
            chatService = RMIClient.getChatService();
            userService = RMIClient.getUserService();
            callback = new ClientCallbackImpl(chatArea);
            chatService.registerClient(callback);
            loadChatList();

            chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    try {
                        int chatId = Integer.parseInt(newVal.split(" - ")[0]);
                        // Join the new chat (if not already joined)
                        if (chatId != currentChatId) {
                            currentChatId = chatId;
                            List<ChatMessage> messages = chatService.getChatMessages(currentChatId);
                            chatArea.clear();

                            for (ChatMessage msg : messages) {
                                chatArea.appendText(msg.toString() + "\n");
                            }
                            callback.setCurrentChatId(chatId);
                            //chatArea.clear();
                            chatArea.appendText("Chat started with ID " + currentChatId + ".\n");
                            //chatService.joinChat(String.valueOf(chatId), currentUser); // optional
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData(Object data) {
        if (data instanceof User) {
            this.currentUser = (User) data;
            nicknameField.setText(currentUser.getNickname());
            profilePicField.setText(currentUser.getProfilePic());
        }


    }

    private void loadChatList() throws RemoteException {
        chatList = userService.getAllChat();
        chatListView.getItems().clear();
        for (Chat chat : chatList) {
            chatListView.getItems().add(chat.getId() + " - " + chat.getName());
        }

    }

    @FXML
    public void handleSend() {
        String messageText = messageField.getText();
        if (messageText.isEmpty() || currentChatId == -1) return;

        if (!subscribedChatIds.contains(currentChatId)) {
            chatArea.appendText("❌ You must subscribe to this chat to send messages.\n");
            return;
        }

        ChatMessage message = new ChatMessage(
                currentChatId,
                currentUser.getNickname(),
                messageText,
                java.time.LocalDateTime.now()
        );

        try {
            chatService.sendMessage(message);
            messageField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void handleSubscribe() {
        String selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            int chatId = Integer.parseInt(selected.split(" - ")[0]);
            userService.subscribeToChat(chatId, currentUser.getId());
            subscribedChatIds.add(chatId);
            chatService.joinChat(String.valueOf(chatId), currentUser);
            chatArea.appendText("You have subscribed to chat ID " + chatId + ".\n");
            currentChatId = chatId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleUnsubscribe() {
        String selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            int chatId = Integer.parseInt(selected.split(" - ")[0]);
            userService.unsubscribeFromChat(chatId, currentUser.getId());
            subscribedChatIds.remove(Integer.valueOf(chatId));
            chatService.leaveChat(String.valueOf(chatId), currentUser);
            chatArea.appendText("You have unsubscribed from chat ID " + chatId + ".\n");
            currentChatId = chatId;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleUpdateProfile() {
        try {
            currentUser.setNickname(nicknameField.getText());
            currentUser.setProfilePic(profilePicField.getText());
            userService.updateProfile(currentUser);
            chatArea.appendText("Profile updated successfully.\n");
        } catch (Exception e) {
            chatArea.appendText("Failed to update profile.\n");
            e.printStackTrace();
        }
    }

}
