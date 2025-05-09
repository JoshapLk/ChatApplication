package rmi;

import dao.ChatDAO;
import dao.ChatLogDAO;
import dao.SubscriptionDAO;
import dao.UserDAO;
import model.Chat;
import model.ChatLog;
import model.ChatMessage;
import model.User;


import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.util.*;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    private final ChatDAO chatDAO;
    private final ChatLogDAO chatLogDAO;
    private final UserDAO userDAO;

    private final List<ClientCallback> clients = new ArrayList<>();
    private final Map<Integer, List<ChatMessage>> chatBuffers = new HashMap<>();
    private final Map<Integer, Set<String>> activeUsers = new HashMap<>();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();


    public ChatServiceImpl() throws RemoteException {
        super();
        chatDAO = new ChatDAO();
        chatLogDAO = new ChatLogDAO();
        userDAO = new UserDAO();
    }

    @Override
    public void registerClient(ClientCallback client) throws RemoteException {
        synchronized (clients) {
            if (!clients.contains(client)) {
                clients.add(client);
            }
        }
    }

    @Override
    public void unregisterClient(ClientCallback client) throws RemoteException {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    @Override
    public void joinChat(String chatIdStr, User user) throws RemoteException {
        int chatId = Integer.parseInt(chatIdStr);
        activeUsers.computeIfAbsent(chatId, k -> new HashSet<>()).add(user.getNickname());

        String time = LocalTime.now().toString();
        String joinMsg = user.getNickname() + " has joined : " + time;
        //appendToBuffer(chatId, joinMsg);

        //broadcastToAll(chatId, joinMsg);
        broadcastUserJoined(chatId, user.getNickname(), time);
    }

    @Override
    public void leaveChat(String chatIdStr, User user) throws RemoteException {
        int chatId = Integer.parseInt(chatIdStr);
        String time = LocalTime.now().toString();
        String leaveMsg = user.getNickname() + " left : " + time;
        //appendToBuffer(chatId, leaveMsg);
        //broadcastToAll(chatId, leaveMsg);
        broadcastUserLeft(chatId, user.getNickname(), time);

        // Remove user
        if (activeUsers.containsKey(chatId)) {
            activeUsers.get(chatId).remove(user.getNickname());

            // If last user left
            if (activeUsers.get(chatId).isEmpty()) {
                endChat(chatId);
            }
        }
    }

//    @Override
//    public void sendMessage(String chatIdStr, String userId, String message) throws RemoteException {
//        String groupIdStr = chatIdStr.split(" - ")[0];  // Get just the "1"
//        int chatId = Integer.parseInt(groupIdStr);
//
//        // Message format: "[nickname]: message"
//        String chatLine = userId + ": " + message;
//        appendToBuffer(chatId, chatLine);
//        broadcastToAll(chatId, chatLine);
//    }

    @Override
    public void sendMessage(ChatMessage message) throws RemoteException {
        int chatId = message.getChatId();
        String nickname = message.getSenderNickname();

        // Check if sender is subscribed
        User user = userDAO.findByNickname(message.getSenderNickname());
        if (!subscriptionDAO.isUserSubscribed(chatId, user.getId())) {
            return;
        }


        appendToBuffer(message);
        broadcastToAll(chatId, message);
    }



    // --- Helper methods ---
    private void broadcastToAll(int chatId, ChatMessage message) {
        synchronized (clients) {
            for (ClientCallback client : clients) {
                try {
                    client.receiveMessage(message);
                } catch (RemoteException ignored) {}
            }
        }
    }


//    private void broadcastToAll(int chatId, String msg) {
//        synchronized (clients) {
//            clients.forEach(client -> {
//                try {
//                    client.receiveMessage(msg);
//                } catch (RemoteException e) {
//                    // Log error or remove failed client
//                }
//            });
//        }
//    }

    private void broadcastUserJoined(int chatId, String nickname, String time) {
        for (ClientCallback client : clients) {
            try {
                client.notifyUserJoined(chatId, nickname, time);
            } catch (RemoteException ignored) {}
        }
    }

    private void broadcastUserLeft(int chatId, String nickname, String time) {
        for (ClientCallback client : clients) {
            try {
                client.notifyUserLeft(chatId, nickname, time);
            } catch (RemoteException ignored) {}
        }
    }


    private void endChat(int chatId) {
        List<ChatMessage> messages = chatBuffers.get(chatId);
        if (messages == null || messages.isEmpty()) return;

        String filePath = "chat_" + chatId + "_" + System.currentTimeMillis() + ".txt";

        try (FileWriter writer = new FileWriter(filePath)) {
            for (ChatMessage msg : messages) {
                writer.write(msg.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save log to DB
        Chat chat = chatDAO.findById(chatId);
        if (chat != null) {
            chat.setActive(false);
            chatDAO.update(chat);

            ChatLog log = new ChatLog();
            log.setChat(chat);
            log.setFilePath(filePath);
            chatLogDAO.save(log);
        }

        // Notify all clients
        for (ClientCallback client : clients) {
            try {
                client.notifyChatEnded(LocalTime.now().toString(), filePath);
            } catch (RemoteException ignored) {}
        }

        chatBuffers.remove(chatId);
        activeUsers.remove(chatId);
    }

    private void appendToBuffer(ChatMessage message) {
        chatBuffers.computeIfAbsent(message.getChatId(), k -> new ArrayList<>()).add(message);
    }

    @Override
    public List<ChatMessage> getChatMessages(int chatId) throws RemoteException {
        return chatBuffers.getOrDefault(chatId, new ArrayList<>());
    }

}
