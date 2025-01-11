package dev.idank.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@GameCollection(name = CollectionNameHelper.GAME)
record DummyGameModel(
        @JsonProperty("_id")
        UUID uuid,
        String name,
        int kills,
        List<String> ownedTanks
) implements GameModel {
}
