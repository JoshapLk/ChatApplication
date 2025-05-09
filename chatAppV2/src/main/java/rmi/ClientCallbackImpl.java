package rmi;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Setter;
import model.ChatMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    private final TextArea chatArea;
    @Setter
    private int currentChatId;

    public ClientCallbackImpl(TextArea chatArea) throws RemoteException {
        super();
        this.chatArea = chatArea;
    }

    @Override
    public void notifyChatStarted(String message) throws RemoteException {
        append("[Chat Started] " + message);
    }

//    @Override
//    public void receiveMessage(String message) throws RemoteException {
//        if (matchesChat(message)) {
//            append(message);
//        }
//    }

    public void receiveMessage(ChatMessage message) throws RemoteException {
        if (message.getChatId() == currentChatId) {
            append(message.toString());
        }
    }


    @Override
    public void notifyUserJoined(int chatId, String nickname, String time) throws RemoteException {
        if (chatId == currentChatId) {
            chatArea.appendText(nickname + " has joined at " + time + "\n");
        }
    }

    @Override
    public void notifyUserLeft(int chatId, String nickname, String time) throws RemoteException {
        if (chatId == currentChatId) {
            chatArea.appendText(nickname + " left at " + time + "\n");
        }
    }


    @Override
    public void notifyChatEnded(String time, String filePath) throws RemoteException {
        append("[Chat Ended at " + time + "] Log saved to: " + filePath);
    }

    private void append(String text) {
        Platform.runLater(() -> chatArea.appendText(text + "\n"));
    }

    private boolean matchesChat(String message) {
        // Optional logic: check if message includes currentChatId or other marker
        // Here, always return true unless you separate chat message routing
        return true;
    }
}
