package pro.gravit.launcher.gui.scenes.debug;

import com.google.gson.annotations.SerializedName;

public class MclogsResponse {
    @SerializedName("success")
    public boolean success;

    @SerializedName("id")
    public String id;

    @SerializedName("url")
    public String url;

    @SerializedName("raw")
    public String raw;

    @SerializedName("error")
    public String error;

    @Override
    public String toString() {
        return "MclogsResponse{" +
                "success=" + success +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", raw='" + raw + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
