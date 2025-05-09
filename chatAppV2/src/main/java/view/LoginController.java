package view;

import model.User;
import rmi.UserService;
import util.RMIClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.SceneManager;

import java.util.Objects;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserService userService = RMIClient.getUserService();

    @FXML
    public void handleLogin() {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();
            User user = userService.login(email, password);

            statusLabel.setText("Login successful!");

            // Load user dashboard
            if(Objects.equals(user.getRole(), "admin")) {
               SceneManager.loadScene("admin_dashboard.fxml",user);
            }else {
                System.out.println(user.getRole());
                SceneManager.loadScene("user_dashboard.fxml", user);
            }

        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void switchToRegister() {
        SceneManager.loadScene("register.fxml");
    }
}
