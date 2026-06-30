package pro.gravit.launcher.gui.scenes.debug;

import com.google.gson.annotations.SerializedName;

public class MclogsRequest {
    @SerializedName("content")
    public String content;
    @SerializedName("source")
    public String source;

    public MclogsRequest(String content, String source) {
        this.content = content;
        this.source = source;
    }
}
