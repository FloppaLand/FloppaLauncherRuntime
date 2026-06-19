package pro.gravit.launcher.gui.overlays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Labeled;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.impl.FxOverlay;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.core.impl.FxStage;
import pro.gravit.launcher.gui.core.impl.ContextHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProcessingOverlay extends FxOverlay {

    private static final Logger logger =
            LoggerFactory.getLogger(ProcessingOverlay.class);

    private Labeled description;
    private ImageView logo;

    public ProcessingOverlay(JavaFXApplication application) {
        super("overlay/processing/processing.fxml", application);
    }

    @Override
    public String getName() {
        return "processing";
    }

    @Override
    protected void doInit() {
        // spinner = LookupHelper.lookup(pane, "#spinner"); //TODO: DrLeonardo?
        description = LookupHelper.lookup(layout, "#description");
        logo = LookupHelper.lookup(layout, "#logo");
        ScaleTransition breathing = new ScaleTransition(Duration.seconds(0.3), logo);
        breathing.setFromX(1.0);
        breathing.setFromY(1.0);
        breathing.setToX(1.2);  // Увеличение на 20%
        breathing.setToY(1.2);
        breathing.setAutoReverse(true);  // Автоматическое обратное воспроизведение
        breathing.setInterpolator(Interpolator.EASE_BOTH);
        breathing.setCycleCount(ScaleTransition.INDEFINITE);  // Бесконечное повторение
        breathing.play();
    }

    @Override
    public void reset() {
        description.textProperty().unbind();
        description.getStyleClass().remove("error");
        description.setText("...");
    }

    public void errorHandle(Throwable e) {
        super.errorHandle(e);
        description.textProperty().unbind();
        description.getStyleClass().add("error");
        description.setText(e.toString());
    }

    public final <T> void processRequest(FxStage stage, String message, CompletableFuture<T> request,
            Consumer<T> onSuccess, EventHandler<ActionEvent> onError) {
        processRequest(stage, message, request, onSuccess, null, onError);
    }

    public final <T> void processRequest(FxStage stage, String message, CompletableFuture<T> request,
            Consumer<T> onSuccess, Consumer<Throwable> onException, EventHandler<ActionEvent> onError) {
        try {
            ContextHelper.runInFxThreadStatic(() -> show(stage, (e) -> {
                description.setText(message);
                request.thenAccept((result) -> {
                    logger.trace("RequestFuture complete normally");
                    onSuccess.accept(result);
                    ContextHelper.runInFxThreadStatic(() -> hide(0, null));
                }).exceptionally((error) -> {
                    if (onException != null) onException.accept(error);
                    else ContextHelper.runInFxThreadStatic(() -> errorHandle(error.getCause()));
                    ContextHelper.runInFxThreadStatic(() -> hide(1500, onError));
                    return null;
                });
            }));
        } catch (Exception e) {
            ContextHelper.runInFxThreadStatic(() -> errorHandle(e));
        }
    }
}