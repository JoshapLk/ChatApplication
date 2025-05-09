package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private int chatId;
    private String senderNickname;
    private String message;
    private LocalDateTime timestamp;

    public ChatMessage() {}

    public ChatMessage(int chatId, String senderNickname, String message, LocalDateTime timestamp) {
        this.chatId = chatId;
        this.senderNickname = senderNickname;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getChatId() {
        return chatId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Optional: pretty string representation
    @Override
    public String toString() {
        return "[" + timestamp.toLocalTime() + "] " + senderNickname + ": " + message;
    }
}
