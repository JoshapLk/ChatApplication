package dao;

import model.ChatLog;

public class ChatLogDAO extends GenericDAOImpl<ChatLog> {
    public ChatLogDAO() {
        super(ChatLog.class);
    }
}
