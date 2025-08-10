package ae.oleapp.models;

import java.io.Serializable;

public class DataModel implements Serializable {
    private String turn, type, player_id, game_id,
            captain_id, team_id, x_coord, y_coord,
            team_a_shirt, team_a_gk_shirt, team_b_shirt, team_b_gk_shirt;

    public DataModel() {
    }

    public DataModel(String turn, String type, String player_id, String game_id, String captain_id, String team_id, String x_coord, String y_coord, String team_a_shirt, String team_a_gk_shirt, String team_b_shirt, String team_b_gk_shirt) {
        this.turn = turn;
        this.type = type;
        this.player_id = player_id;
        this.game_id = game_id;
        this.captain_id = captain_id;
        this.team_id = team_id;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.team_a_shirt = team_a_shirt;
        this.team_a_gk_shirt = team_a_gk_shirt;
        this.team_b_shirt = team_b_shirt;
        this.team_b_gk_shirt = team_b_gk_shirt;
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

    public String getGame_id() {
        return game_id;
    }

    public String getCaptain_id() {
        return captain_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public String getX_coord() {
        return x_coord;
    }

    public String getY_coord() {
        return y_coord;
    }

    public String getTeam_a_shirt() {
        return team_a_shirt;
    }

    public String getTeam_a_gk_shirt() {
        return team_a_gk_shirt;
    }

    public String getTeam_b_shirt() {
        return team_b_shirt;
    }

    public String getTeam_b_gk_shirt() {
        return team_b_gk_shirt;
    }
}
