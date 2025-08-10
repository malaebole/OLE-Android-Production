package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleHistoryDetail {

    @SerializedName("club_name")
    @Expose
    private String clubName;
    @SerializedName("booking_date")
    @Expose
    private String bookingDate;
    @SerializedName("player_one")
    @Expose
    private OlePlayerInfo playerOne;
    @SerializedName("player_two")
    @Expose
    private OlePlayerInfo playerTwo;

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public OlePlayerInfo getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(OlePlayerInfo playerOne) {
        this.playerOne = playerOne;
    }

    public OlePlayerInfo getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(OlePlayerInfo playerTwo) {
        this.playerTwo = playerTwo;
    }

}
