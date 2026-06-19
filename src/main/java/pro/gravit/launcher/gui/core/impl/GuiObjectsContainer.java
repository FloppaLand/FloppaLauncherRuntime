package pro.gravit.launcher.gui.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.gravit.launcher.gui.components.FxSceneBackground;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.overlays.ProcessingOverlay;
import pro.gravit.launcher.gui.overlays.UploadAssetOverlay;
import pro.gravit.launcher.gui.overlays.WelcomeOverlay;
import pro.gravit.launcher.gui.scenes.console.ConsoleScene;
import pro.gravit.launcher.gui.scenes.debug.DebugScene;
import pro.gravit.launcher.gui.scenes.internal.BrowserScene;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.launcher.gui.scenes.options.OptionsScene;
import pro.gravit.launcher.gui.scenes.serverinfo.ServerInfoScene;
import pro.gravit.launcher.gui.scenes.servermenu.ServerMenuScene;
import pro.gravit.launcher.gui.scenes.settings.GlobalSettingsScene;
import pro.gravit.launcher.gui.scenes.settings.SettingsScene;
import pro.gravit.launcher.gui.scenes.update.UpdateScene;
import pro.gravit.launcher.gui.stage.ConsoleStage;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GuiObjectsContainer {

    private static final Logger logger =
            LoggerFactory.getLogger(GuiObjectsContainer.class);

    private final JavaFXApplication application;
    private final Map<String, FxComponent> components = new HashMap<>();
    public ProcessingOverlay processingOverlay;
    public WelcomeOverlay welcomeOverlay;
    public UploadAssetOverlay uploadAssetOverlay;
    public UpdateScene updateScene;
    public DebugScene debugScene;

    public ServerMenuScene serverMenuScene;
    public ServerInfoScene serverInfoScene;
    public LoginScene loginScene;
    public OptionsScene optionsScene;
    public SettingsScene settingsScene;
    public GlobalSettingsScene globalSettingsScene;
    public ConsoleScene consoleScene;

    public ConsoleStage consoleStage;
    public BrowserScene browserScene;
    public FxSceneBackground background;

    public GuiObjectsContainer(JavaFXApplication application) {
        this.application = application;
    }

    public void init() {
        background = registerComponent(FxSceneBackground.class);
        loginScene = registerComponent(LoginScene.class);
        processingOverlay = registerComponent(ProcessingOverlay.class);
        welcomeOverlay = registerComponent(WelcomeOverlay.class);
        uploadAssetOverlay = registerComponent(UploadAssetOverlay.class);

        serverMenuScene = registerComponent(ServerMenuScene.class);
        serverInfoScene = registerComponent(ServerInfoScene.class);
        optionsScene = registerComponent(OptionsScene.class);
        settingsScene = registerComponent(SettingsScene.class);
        globalSettingsScene = registerComponent(GlobalSettingsScene.class);
        consoleScene = registerComponent(ConsoleScene.class);

        updateScene = registerComponent(UpdateScene.class);
        debugScene = registerComponent(DebugScene.class);
        browserScene = registerComponent(BrowserScene.class);
    }

    public Collection<FxComponent> getComponents() {
        return components.values();
    }

    public void reload() throws Exception {
        String sceneName = application.getCurrentScene().getName();
        ContextHelper.runInFxThreadStatic(() -> {
            application.getMainStage().setScene(null, false);
            application.getMainStage().pullBackground(background);
            application.resetDirectory();
            components.clear();
            application.getMainStage().resetStyles();
            init();
            application.getMainStage().pushBackground(background);
            for (FxComponent s : components.values()) {
                if (sceneName.equals(s.getName())) {
                    application.getMainStage().setScene(s, false);
                }
            }
        }).get();
    }

    public FxComponent getByName(String name) {
        return components.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T extends FxComponent> T registerComponent(Class<T> clazz) {
        try {
            T instance = (T) MethodHandles
                    .publicLookup().findConstructor(clazz, MethodType.methodType(void.class, JavaFXApplication.class))
                    .invokeWithArguments(application);
            components.put(instance.getName(), instance);
            return instance;
        } catch (Throwable e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }
}