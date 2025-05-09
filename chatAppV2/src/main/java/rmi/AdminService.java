package rmi;

import model.Chat;
import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AdminService extends Remote {
    Chat createChat(String name) throws RemoteException;
    void subscribeUserToChat(int chatId, int userId) throws RemoteException;
    void unsubscribeUserFromChat(int chatId, int userId) throws RemoteException;
    void removeUser(int userId) throws RemoteException;
    List<User> getAllUsers() throws RemoteException;
    List<Chat> getAllChats() throws RemoteException;
    List<User> getChatSubscribers(int chatId) throws RemoteException;
}
