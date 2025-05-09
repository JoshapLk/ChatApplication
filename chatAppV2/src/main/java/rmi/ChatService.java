package rmi;

import model.Chat;
import model.ChatMessage;
import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {
    //void sendMessage(String chatId, String userId, String message) throws RemoteException;
    void sendMessage(ChatMessage message) throws RemoteException;
    List<ChatMessage> getChatMessages(int chatId) throws RemoteException;
    void joinChat(String chatId, User user) throws RemoteException;
    void leaveChat(String chatId, User user) throws RemoteException;
    void registerClient(ClientCallback client) throws RemoteException;
    void unregisterClient(ClientCallback client) throws RemoteException;


}
