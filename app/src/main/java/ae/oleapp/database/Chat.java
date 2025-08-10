package ae.oleapp.database;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Chat extends RealmObject {
    private String chatId;
    private String bookingId;
    private String date;
    private RealmList<Message> messages = new RealmList<>();

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RealmList<Message> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<Message> messages) {
        this.messages = messages;
    }
}
