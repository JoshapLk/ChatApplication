package dao;

import model.Chat;

public class ChatDAO extends GenericDAOImpl<Chat> {
    public ChatDAO() {
        super(Chat.class);
    }
}
