package ae.oleapp.models;

public class OlePlayerCoordinate {
    String playerId, teamId;
    float xCoordinate, yCoordinate;

    public OlePlayerCoordinate() {

    }

    public OlePlayerCoordinate(String playerId, String teamId, float xCoordinate, float yCoordinate) {
        this.playerId = playerId;
        this.teamId = teamId;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public float getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(float xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public float getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
}
