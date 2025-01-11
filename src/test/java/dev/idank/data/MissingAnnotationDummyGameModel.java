package dev.idank.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

record MissingAnnotationDummyGameModel(
        @JsonProperty("_id")
        UUID uuid,
        String name
) implements GameModel {
}
