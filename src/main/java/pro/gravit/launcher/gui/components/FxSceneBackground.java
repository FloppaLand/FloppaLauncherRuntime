package pro.gravit.launcher.gui.components;

import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.impl.FxComponent;

public class FxSceneBackground extends FxComponent {
    public FxSceneBackground(JavaFXApplication application) {
        super("components/background.fxml", application);
    }

    @Override
    public String getName() {
        return "background";
    }

    @Override
    protected void doInit() {

    }

    @Override
    public void reset() {

    }
}
