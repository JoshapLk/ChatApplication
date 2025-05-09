package view;

import model.Chat;
import model.User;
import rmi.AdminService;
import util.DataReceiver;
import util.RMIClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.RemoteException;
import java.util.List;

public class AdminDashboardController implements DataReceiver {

    @FXML private ListView<String> chatListView;
    @FXML private ListView<String> userListView;
    @FXML private TextField chatNameField;
    @FXML private Label statusLabel;

    private AdminService adminService;
    private List<Chat> chatList;
    private List<User> userList;

    @Override
    public void initData(Object data) {
        try {
            adminService = RMIClient.getAdminService();
            loadChats();
            loadUsers();
        } catch (Exception e) {
            statusLabel.setText("Failed to load admin data.");
            e.printStackTrace();
        }
    }

    private void loadChats() throws RemoteException {
        chatList = adminService.getAllChats();
        chatListView.getItems().clear();
        for (Chat chat : chatList) {
            chatListView.getItems().add(chat.getId() + " - " + chat.getName());
        }
    }

    private void loadUsers() throws RemoteException {
        userList = adminService.getAllUsers();
        userListView.getItems().clear();
        for (User user : userList) {
            userListView.getItems().add(user.getId() + " - " + user.getNickname());
        }
    }

    @FXML
    public void handleCreateChat() {
        try {
            String chatName = chatNameField.getText();
            if (!chatName.isEmpty()) {
                adminService.createChat(chatName);
                statusLabel.setText("Chat created.");
                loadChats();
            } else {
                statusLabel.setText("Chat name required.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error creating chat.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSubscribeUser() {
        try {
            int chatId = getSelectedChatId();
            int userId = getSelectedUserId();
            adminService.subscribeUserToChat(chatId, userId);
            statusLabel.setText("User subscribed.");
        } catch (Exception e) {
            statusLabel.setText("Error subscribing user.");
        }
    }

    @FXML
    public void handleUnsubscribeUser() {
        try {
            int chatId = getSelectedChatId();
            int userId = getSelectedUserId();
            adminService.unsubscribeUserFromChat(chatId, userId);
            statusLabel.setText("User unsubscribed.");
        } catch (Exception e) {
            statusLabel.setText("Error unsubscribing user.");
        }
    }

    @FXML
    public void handleRemoveUser() {
        try {
            int userId = getSelectedUserId();
            adminService.removeUser(userId);
            statusLabel.setText("User removed.");
            loadUsers();
        } catch (Exception e) {
            statusLabel.setText("Error removing user.");
        }
    }

    @FXML
    public void handleViewSubscribers() {
        try {
            int chatId = getSelectedChatId();
            List<User> subscribers = adminService.getChatSubscribers(chatId);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Subscribers");
            alert.setHeaderText("Users subscribed to chat ID: " + chatId);

            StringBuilder sb = new StringBuilder();
            for (User u : subscribers) {
                sb.append(u.getId()).append(" - ").append(u.getNickname()).append("\n");
            }
            alert.setContentText(sb.toString());
            alert.showAndWait();
        } catch (Exception e) {
            statusLabel.setText("Failed to get subscribers.");
        }
    }

    private int getSelectedChatId() throws Exception {
        String selected = chatListView.getSelectionModel().getSelectedItem();
        if (selected == null) throw new Exception("No chat selected");
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private int getSelectedUserId() throws Exception {
        String selected = userListView.getSelectionModel().getSelectedItem();
        if (selected == null) throw new Exception("No user selected");
        return Integer.parseInt(selected.split(" - ")[0]);
    }
}
