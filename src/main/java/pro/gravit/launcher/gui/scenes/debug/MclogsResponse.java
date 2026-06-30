package pro.gravit.launcher.gui.scenes.debug;

public class MclogsResponse {
    public boolean success; // Успешна ли загрузка
    public String id;       // Уникальный ID лога
    public String url;      // Ссылка (https://mclo.gs/...)
    public String raw;      // Ссылка на сырой текст
    public String error;    // Текст ошибки (если success = false)

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
