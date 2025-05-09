package rmi;

import model.Chat;
import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserService extends Remote {
    User register(String email, String username, String password, String nickname) throws RemoteException;
    User login(String email, String password) throws RemoteException;
    void updateProfile(User user) throws RemoteException;
    void subscribeToChat(int chatId, int userId) throws RemoteException;
    void unsubscribeFromChat(int chatId, int userId) throws RemoteException;
    List<Chat> getAllChat() throws RemoteException;
    List<Integer> getSubscribedChatIds(int userId) throws RemoteException;

}
