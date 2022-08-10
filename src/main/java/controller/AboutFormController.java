package controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class AboutFormController {
    public AnchorPane anchrPaneAbout;
    public ImageView imgLogo;

    public void initialize(){
        ScaleTransition st = new ScaleTransition(Duration.millis(1000), imgLogo);
        st.setFromX(0);
        st.setFromY(0);
        st.setToX(1);
        st.setToY(1);
        st.playFromStart();

        FadeTransition ft = new FadeTransition(Duration.millis(1000), imgLogo);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.playFromStart();

        FadeTransition ftWindow = new FadeTransition(Duration.millis(1000), anchrPaneAbout);
        ftWindow.setFromValue(0);
        ftWindow.setToValue(1);
        ftWindow.playFromStart();
    }


}
