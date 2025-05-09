package server;

import rmi.AdminServiceImpl;
import rmi.ChatServiceImpl;
import rmi.UserServiceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("ChatService", new ChatServiceImpl());
            Naming.rebind("AdminService", new AdminServiceImpl());
            Naming.rebind("UserService", new UserServiceImpl());
            System.out.println("RMI Server started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
