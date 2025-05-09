package rmi;

import model.ChatMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void notifyChatStarted(String message) throws RemoteException;
    //void receiveMessage(String message) throws RemoteException;
    void receiveMessage(ChatMessage message) throws RemoteException;

    void notifyUserJoined(int chatId, String nickname, String time) throws RemoteException;
    void notifyUserLeft(int chatId, String nickname, String time) throws RemoteException;

    void notifyChatEnded(String time, String filePath) throws RemoteException;
}
