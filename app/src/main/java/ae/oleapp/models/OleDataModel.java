package ae.oleapp.models;

import java.io.Serializable;

public class OleDataModel implements Serializable {
    private String turn, type, player_id, booking_id;

    public OleDataModel() {
    }

    public OleDataModel(String turn, String type, String player_id, String booking_id) {
        this.turn = turn;
        this.type = type;
        this.player_id = player_id;
        this.booking_id = booking_id;
    }

    public String getTurn() {
        return turn;
    }

    public String getType() {
        return type;
    }

    public String getPlayer_id() {
        return player_id;
    }

    public String getBooking_id() {
        return booking_id;
    }
}
