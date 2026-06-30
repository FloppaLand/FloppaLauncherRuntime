package pro.gravit.launcher.gui.scenes.debug;

import com.google.gson.Gson;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pro.gravit.launcher.core.backend.LauncherBackendAPI;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.impl.ContextHelper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessLogOutput extends LauncherBackendAPI.RunCallback {
    static final long MAX_LENGTH = 1024 * 256;
    static final int REMOVE_LENGTH = 1024 * 16;
    private final TextArea output;
    private final Object syncObject = new Object();
    private String appendString = "";
    private boolean isOutputRunned;
    private Runnable terminateProcessCallback;
    private boolean exitWhenStarted;
    AtomicBoolean isReadyToExit = new AtomicBoolean();
    private AtomicBoolean isRunned = new AtomicBoolean();
    private AtomicBoolean isAttached = new AtomicBoolean(true);

    public ProcessLogOutput(TextArea output, boolean exitWhenStarted) {
        this.output = output;
        this.exitWhenStarted = exitWhenStarted;
    }

    public String getText() {
        return output.getText();
    }

    public void clear() {
        output.clear();
    }

    private void putStringToClipboard(String string) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(string);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(clipboardContent);
    }

    public void copyToClipboard() {
        putStringToClipboard(getText());
    }

    public CompletableFuture<String> upload() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Gson gson = new Gson();

                String requestBody = gson.toJson(new MclogsRequest(getText(), "GravitLauncher"));
                HttpClient client = HttpClient.newHttpClient();


                HttpRequest request = HttpRequest.newBuilder()
                                                 .uri(URI.create("https://api.mclo.gs/1/log"))
                                                 .header("Content-Type", "application/json; charset=utf-8")
                                                 .header("Accept", "application/json")
                                                 .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                                                 .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("API Error " + response.statusCode());
                }
                MclogsResponse mclogsResponse = gson.fromJson(response.body(), MclogsResponse.class);

                return mclogsResponse.url;

            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public void append(String text) {
        boolean needRun = false;
        synchronized (syncObject) {
            if (appendString.length() > MAX_LENGTH) {
                appendString = "<logs buffer overflow>\n".concat(text);
            } else {
                appendString = appendString.concat(text);
            }
            if (!isOutputRunned) {
                needRun = true;
                isOutputRunned = true;
            }
        }
        if (needRun) {
            ContextHelper.runInFxThreadStatic(() -> {
                synchronized (syncObject) {
                    if (output.lengthProperty().get() > MAX_LENGTH) {
                        output.deleteText(0, REMOVE_LENGTH);
                    }
                    output.appendText(appendString);
                    appendString = "";
                    isOutputRunned = false;
                }
            });
        }
    }

    @Override
    public void onStarted() {
        super.onStarted();
        isRunned.set(true);
    }

    @Override
    public void onCanTerminate(Runnable terminate) {
        super.onCanTerminate(terminate);
        terminateProcessCallback = terminate;
    }

    @Override
    public void onFinished(int code) {
        super.onFinished(code);
        isRunned.set(false);
        if(!isAttached.get()) {
            return;
        }
        append(String.format("Process finished with exit code %s", code));
    }

    @Override
    public void onNormalOutput(byte[] buf, int offset, int size) {
        super.onNormalOutput(buf, offset, size);
        if(!isAttached.get()) {
            return;
        }
        append(new String(buf, offset, size));
    }

    @Override
    public void onErrorOutput(byte[] buf, int offset, int size) {
        super.onErrorOutput(buf, offset, size);
        if(!isAttached.get()) {
            return;
        }
        append(new String(buf, offset, size));
    }

    public void terminate() {
        if(terminateProcessCallback == null) {
            return;
        }
        terminateProcessCallback.run();
    }

    public void detach() {
        isAttached.set(false);
    }

    @Override
    public void onReadyToExit() {
        isReadyToExit.set(true);
        if(exitWhenStarted) {
            JavaFXApplication.getInstance().getMainStage().close();
        }
    }
}

