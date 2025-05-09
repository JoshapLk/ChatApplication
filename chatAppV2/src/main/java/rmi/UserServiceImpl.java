package rmi;

import dao.ChatDAO;
import dao.SubscriptionDAO;
import dao.UserDAO;
import model.Chat;
import model.ChatMessage;
import model.Subscription;
import model.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private final UserDAO userDAO;
    private final ChatDAO chatDAO;
    private final SubscriptionDAO subscriptionDAO;





    public UserServiceImpl() throws RemoteException {
        super();
        this.chatDAO = new ChatDAO();
        this.userDAO = new UserDAO();
        this.subscriptionDAO = new SubscriptionDAO();
    }

    @Override
    public User register(String email, String username, String password, String nickname) throws RemoteException {
        // Check if user already exists
        User existing = userDAO.findByEmail(email);
        if (existing != null) {
            throw new RemoteException("User already exists with this email.");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setPassword(password); // NOTE: hash in production!
        newUser.setNickname(nickname);
        userDAO.save(newUser);

        return newUser;
    }

    @Override
    public User login(String email, String password) throws RemoteException {
        User user = userDAO.findByEmail(email);
        System.out.println(user.getUsername());
        if (user == null || !user.getPassword().equals(password)) {
            throw new RemoteException("Invalid email or password.");
        }
        return user;
    }

    @Override
    public void updateProfile(User updatedUser) throws RemoteException {
        User existing = userDAO.findById(updatedUser.getId());
        if (existing == null) {
            throw new RemoteException("User not found.");
        }

        existing.setUsername(updatedUser.getUsername());
        existing.setPassword(updatedUser.getPassword());
        existing.setNickname(updatedUser.getNickname());
        existing.setProfilePic(updatedUser.getProfilePic());

        userDAO.update(existing);
    }

    @Override
    public void subscribeToChat(int chatId, int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        Chat chat = chatDAO.findById(chatId);

        if (user == null || chat == null) {
            throw new RemoteException("User or chat not found.");
        }

        // Optional: check if already subscribed to prevent duplicates
        List<Subscription> existingSubs = subscriptionDAO.findAll();
        for (Subscription sub : existingSubs) {
            if (sub.getChat().getId() == chatId && sub.getUser().getId() == userId) {
                return; // Already subscribed
            }
        }

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setChat(chat);
        subscriptionDAO.save(sub);
    }


    @Override
    public void unsubscribeFromChat(int chatId, int userId) throws RemoteException {
        List<Subscription> allSubs = subscriptionDAO.findAll();
        for (Subscription sub : allSubs) {
            if (sub.getChat().getId() == chatId && sub.getUser().getId() == userId) {
                subscriptionDAO.delete(sub);
                break;
            }
        }
    }


    @Override
    public List<Chat> getAllChat() throws RemoteException {
        return chatDAO.findAll();
    }




}
