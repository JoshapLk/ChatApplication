package view;

import model.User;
import rmi.UserService;
import util.RMIClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import util.SceneManager;

public class RegisterController {

    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nicknameField;
    @FXML private Label statusLabel;

    private final UserService userService = RMIClient.getUserService();

    @FXML
    public void handleRegister() {
        try {
            User user = userService.register(
                    emailField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    nicknameField.getText()
            );
            statusLabel.setText("Registration successful!");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void switchToLogin() {
        SceneManager.loadScene("login.fxml");
    }
}
