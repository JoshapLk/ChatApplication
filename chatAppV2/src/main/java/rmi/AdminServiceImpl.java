package rmi;

import dao.ChatDAO;
import dao.SubscriptionDAO;
import dao.UserDAO;
import model.Chat;
import model.Subscription;
import model.User;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;

public class AdminServiceImpl extends UnicastRemoteObject implements AdminService {

    private final ChatDAO chatDAO;
    private final UserDAO userDAO;
    private final SubscriptionDAO subscriptionDAO;

    public AdminServiceImpl() throws RemoteException {
        super();
        this.chatDAO = new ChatDAO();
        this.userDAO = new UserDAO();
        this.subscriptionDAO = new SubscriptionDAO();
    }

    @Override
    public Chat createChat(String name) throws RemoteException {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setCreatedTime(LocalDateTime.now());
        chat.setActive(true);

        chatDAO.save(chat);
        return chat;
    }

    @Override
    public void subscribeUserToChat(int chatId, int userId) throws RemoteException {
        Chat chat = chatDAO.findById(chatId);
        User user = userDAO.findById(userId);

        if (chat == null || user == null) {
            throw new RemoteException("Chat or user not found.");
        }

        Subscription sub = new Subscription();
        sub.setChat(chat);
        sub.setUser(user);
        subscriptionDAO.save(sub);
    }

    @Override
    public void unsubscribeUserFromChat(int chatId, int userId) throws RemoteException {
        List<Subscription> allSubs = subscriptionDAO.findAll();

        for (Subscription sub : allSubs) {
            if (sub.getChat().getId() == chatId && sub.getUser().getId() == userId) {
                subscriptionDAO.delete(sub);
                break;
            }
        }
    }

    @Override
    public void removeUser(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user != null) {
            userDAO.delete(user);
        }
    }

    @Override
    public List<User> getAllUsers() throws RemoteException {
        return userDAO.findAll();
    }

    @Override
    public List<Chat> getAllChats() throws RemoteException {
        return chatDAO.findAll();
    }

    @Override
    public List<User> getChatSubscribers(int chatId) throws RemoteException {
        return subscriptionDAO.findAll().stream()
                .filter(sub -> sub.getChat().getId() == chatId)
                .map(Subscription::getUser)
                .toList();
    }
}
