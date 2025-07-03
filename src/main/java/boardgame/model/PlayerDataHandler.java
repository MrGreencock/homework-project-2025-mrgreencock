package boardgame.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is for file handling. Storing the players in a JSON file
 */

public class PlayerDataHandler {

    private static final String FILE_NAME = "jatekosok.json";
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Storing the players. Giving a default ELO rating (1000 points).
     * @param names parameter list of the player names
     * @throws IOException whether the file does not exist.
     */
    public void savePlayersIfNotExists(String... names) throws IOException {
        var players = readPlayersFromFile();
        boolean modified = false;

        for (String name : names) {
            if (players.stream().noneMatch(p -> p.name.equalsIgnoreCase(name))) {
                players.add(new PlayerData(name, 1000));
                modified = true;
            }
        }

        if (modified) {
            objectMapper.writeValue(new File(FILE_NAME), players);
        }
    }

    /**
     * When a match is over the points of the players will change.
     * Winning a game increases this value by 50,
     * while losing a game decreases it by 50.
     * @param winnerPlayer name of winner
     * @param loserPlayer name of loser
     * @throws IOException whether the file does not exist.
     */
    public void updatePoints(String winnerPlayer, String loserPlayer) throws IOException {
        var players = readPlayersFromFile();
        for (var player : players) {
            if(player.getName().equals(winnerPlayer)) {
                player.eloPoint += 50;
            }
            else if (player.getName().equals(loserPlayer)) {
                player.eloPoint -= 50;
            }
        }

        objectMapper.writeValue(new File(FILE_NAME), players);

    }

    private List<PlayerData> readPlayersFromFile() throws IOException {
        var file = new File(FILE_NAME);
        if (!file.exists()) return new java.util.ArrayList<>();
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

    /**
     * The {@code PlayerData} nested class represents a player's data,
     * including their name and Elo rating.
     * It also provides functionality to load data from a JSON file.
     */
    public static class PlayerData {
        /**
         * The name of the player.
         * JSON key: "nev"
         */
        @JsonProperty("nev")
        public String name;

        /**
         * The Elo rating of the player.
         * JSON key: "elo_pont"
         */
        @JsonProperty("elo_pont")
        public int eloPoint;

        public PlayerData() {}

        public PlayerData(String name, int eloPoint) {
            this.name = name;
            this.eloPoint = eloPoint;
        }

        public String getName() {
            return name;
        }

        public int getEloPoint() {
            return eloPoint;
        }

        /**
         * Loads all player data from a JSON file.
         * The file name is defined by the {@code FILE_NAME} constant.
         * If the file is not found or an error occurs during reading,
         * an empty list is returned.
         *
         * @return a list of {@code PlayerData} objects, or an empty list on failure
         */

        public static List<PlayerData> getAll() {
            try {
                return objectMapper.readValue(new File(FILE_NAME), new TypeReference<List<PlayerData>>() {});
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        }
    }
}
