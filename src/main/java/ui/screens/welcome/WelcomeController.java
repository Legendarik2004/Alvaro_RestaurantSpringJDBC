package ui.screens.welcome;

import javafx.scene.control.Label;
import ui.screens.common.BaseScreenController;

public class WelcomeController extends BaseScreenController {

    public Label welcomeLabel;

    @Override
    public void principalCargado() {
        welcomeLabel.setText("Welcome " + getPrincipalController().actualUser.getNombre());
    }
}
