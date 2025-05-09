package util;

import lombok.Getter;
import rmi.AdminService;
import rmi.ChatService;
import rmi.UserService;

import java.rmi.Naming;

public class RMIClient {
    @Getter
    public static ChatService chatService;
    @Getter
    public static AdminService adminService;
    @Getter
    public static UserService userService;

    public static void init() {
        try {
            chatService = (ChatService) Naming.lookup("rmi://localhost/ChatService");
            adminService = (AdminService) Naming.lookup("rmi://localhost/AdminService");
            userService = (UserService) Naming.lookup("rmi://localhost/UserService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
