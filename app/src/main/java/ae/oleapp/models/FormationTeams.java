package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormationTeams {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("team_name")
        @Expose
        private String teamName;
        @SerializedName("team_shirt")
        @Expose
        private String teamShirt;
        @SerializedName("team_gk_shirt")
        @Expose
        private String teamGkShirt;
        @SerializedName("players")
        @Expose
        private List<PlayerInfo> players;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamShirt() {
            return teamShirt;
        }

        public void setTeamShirt(String teamShirt) {
            this.teamShirt = teamShirt;
        }

        public String getTeamGkShirt() {
            return teamGkShirt;
        }

        public void setTeamGkShirt(String teamGkShirt) {
            this.teamGkShirt = teamGkShirt;
        }

        public List<PlayerInfo> getPlayers() {
            return players;
        }

        public void setPlayers(List<PlayerInfo> players) {
            this.players = players;
        }

}
