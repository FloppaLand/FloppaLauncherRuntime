package pro.gravit.launcher.gui.scenes.debug;

import javafx.application.Platform;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;

import pro.gravit.launcher.core.backend.LauncherBackendAPI;
import pro.gravit.launcher.gui.components.BasicUserControls;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.JavaRuntimeModule;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.core.impl.FxScene;

import java.io.EOFException;

public class DebugScene extends FxScene {
    private volatile ProcessLogOutput processLogOutput;

    private StyleClassedTextArea output;

    public DebugScene(JavaFXApplication application) {
        super("scenes/debug/debug.fxml", application);
        this.isResetOnShow = true;
        this._basicUserControlConstructor = DebugBasicUserControls::new;
    }

    @Override
    protected void doInit() {
        output = new StyleClassedTextArea();
        output.setId("output");
        output.setEditable(false);
        output.setWrapText(true);

        VirtualizedScrollPane<StyleClassedTextArea> scrollPane = new VirtualizedScrollPane<>(output);

        StackPane outputDetail = LookupHelper.lookup(layout, "#output-detail");
        outputDetail.getChildren().add(scrollPane);

        processLogOutput = new ProcessLogOutput(output, !application.runtimeSettings.globalSettings.debugAllClients);

        LookupHelper.<ButtonBase>lookupIfPossible(header, "#controls", "#kill").ifPresent((x) -> x.setOnAction((e) -> {
            processLogOutput.terminate();
        }));

        LookupHelper.<Label>lookupIfPossible(layout, "#version")
                    .ifPresent((v) -> v.setText(JavaRuntimeModule.getMiniLauncherInfo()));

        LookupHelper.<ButtonBase>lookupIfPossible(header, "#controls", "#copy").ifPresent((x) ->
                                                                                                  x.setOnAction((e) -> processLogOutput.copyToClipboard())
        );

        LookupHelper.<ButtonBase>lookupIfPossible(header, "#controls", "#upload").ifPresent((x) ->
                                                                                                    x.setOnAction((e) -> {
                                                                                                        x.setDisable(true);
                                                                                                        processLogOutput.upload().thenAccept(url -> {
                                                                                                            Platform.runLater(() -> {
                                                                                                                x.setDisable(false);
                                                                                                                ClipboardContent clipboardContent = new ClipboardContent();
                                                                                                                clipboardContent.putString(url);
                                                                                                                Clipboard clipboard = Clipboard.getSystemClipboard();
                                                                                                                clipboard.setContent(clipboardContent);
                                                                                                                application.messageManager.createNotification(
                                                                                                                        application.getTranslation("runtime.overlay.debug.uploadlog.success.header"),
                                                                                                                        application.getTranslation("runtime.overlay.debug.uploadlog.success.description")
                                                                                                                );
                                                                                                            });
                                                                                                        }).exceptionally(ex -> {
                                                                                                            Platform.runLater(() -> {
                                                                                                                x.setDisable(false);
                                                                                                                Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                                                                                                                application.messageManager.createNotification(
                                                                                                                        application.getTranslation("runtime.overlay.debug.uploadlog.fail.header"),
                                                                                                                        cause.getMessage()
                                                                                                                );
                                                                                                            });
                                                                                                            return null;
                                                                                                        });
                                                                                                    })
        );

        LookupHelper.<ButtonBase>lookup(header, "#back").setOnAction((e) -> {
            processLogOutput.detach();
            processLogOutput = new ProcessLogOutput(output, !application.runtimeSettings.globalSettings.debugAllClients);
            try {
                switchToBackScene();
            } catch (Exception ex) {
                errorHandle(ex);
            }
        });


        TextField searchField = LookupHelper.lookup(layout, "#searchField");
        ButtonBase searchUpBtn = LookupHelper.lookup(layout, "#searchUpBtn");
        ButtonBase searchDownBtn = LookupHelper.lookup(layout, "#searchDownBtn");


        searchField.setOnAction(e -> findText(searchField.getText(), true));

        searchDownBtn.setOnAction(e -> findText(searchField.getText(), true));
        searchUpBtn.setOnAction(e -> findText(searchField.getText(), false));

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.isEmpty()) {
                output.deselect();
            }
        });
    }

    @Override
    public void reset() {
        processLogOutput.clear();
    }

    public void run(LauncherBackendAPI.ReadyProfile readyProfile) {
        try {
            readyProfile.run(processLogOutput);
        } catch (Throwable e) {
            errorHandle(e);
        }
    }

    public void append(String text) {
        processLogOutput.append(text);
    }

    @Override
    public void errorHandle(Throwable e) {
        if (!(e instanceof EOFException)) {
            processLogOutput.append(e.toString());
        }
    }

    @Override
    public String getName() {
        return "debug";
    }

        private void findText(String query, boolean searchDown) {
            if (query == null || query.isEmpty()) {
                output.deselect();
                return;
            }

            // Получаем весь текст из консоли (в нижнем регистре для нечувствительности к регистру)
            String content = output.getText().toLowerCase();
            String lowerQuery = query.toLowerCase();

            int matchIndex = -1;

            if (searchDown) {

                int startIndex = output.getSelection().getEnd();
                matchIndex = content.indexOf(lowerQuery, startIndex);

                // Если дошли до конца логов, начинаем искать с самого начала
                if (matchIndex == -1) {
                    matchIndex = content.indexOf(lowerQuery, 0);
                }
            } else {

                int startIndex = output.getSelection().getStart() - 1;
                matchIndex = content.lastIndexOf(lowerQuery, startIndex);


                if (matchIndex == -1) {
                    matchIndex = content.lastIndexOf(lowerQuery, content.length());
                }
            }


            if (matchIndex != -1) {

                output.selectRange(matchIndex, matchIndex + query.length());

                output.requestFollowCaret();
            }
        }

    private class DebugBasicUserControls extends BasicUserControls {
        public DebugBasicUserControls(Pane layout, JavaFXApplication application) {
            super(layout, application);
        }

        @Override
        protected void onExit() {
            if(processLogOutput == null || processLogOutput.isReadyToExit.get()) {
                super.onExit();
            } else {
                application.messageManager.showApplyDialog(
                        application.getTranslation("runtime.scenes.debug.forceExitDialog.header"),
                        application.getTranslation("runtime.scenes.debug.forceExitDialog.description"),
                        () -> {
                            processLogOutput.terminate();
                            super.onExit();
                        }, () -> {}, true);
            }
        }
    }
}