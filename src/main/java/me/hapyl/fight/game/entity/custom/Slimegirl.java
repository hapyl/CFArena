package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.overlay.OverlayGameEntityType;
import me.hapyl.eterna.module.player.PlayerSkin;

public class Slimegirl extends OverlayGameEntityType {
    public Slimegirl() {
        super(
                "Slime Girl",
                new PlayerSkin(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY3MzkwOTI1NTMzOCwKICAicHJvZmlsZUlkIiA6ICI2MDJmMjA0M2YzYjU0OGU1ODQyYjE4ZjljMDg2Y2U0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCb3J5c18iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYxYWExNzQ1NzM3NmIzMGJkNzljYjQ1YTg2NzAwMTEzNzNmZWI5Zjg5YjgwNzZmMzE4Zjc4ZmZiNTliYTRkOSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                        "fEpU5fCgN+71KBSPSa5ItyGV4V2W/ZIDxkRiUZD3J6ykIGsHBx+VWVq85UDSlUBbEwU8nIIGSnIY7Iki2fcb9KCBw6v8oB/zzKe+20zTfITq4lIUOx5RxjPUPcBt2al2ze2qmrRUK7XVvGqcGNJcuxjs/Hx2Wlxxq+8T107BRneh6T4tR1IqRtDNehDcgUWw+3CoNyNPi9pKVoIZkqraXNh5etP7qse394wJ57/PHsH/ndwMTuTTy7uIfo59a75frZzj02CAoQyLSpvfRVH/7a2fUEuigTZdrrFtSsB58rfHzYJqZR0kuBvrt7rn4sHBTvoyyi/IK4H601NdE/srcyQzT0zvoY+ISsN2FscbBjW4aR+FDmrXRyPoQGE0ST9+yAoUTjtEnV7ABN0s51zt3fAQsqkYNbBUSQJSnNBSzsWd5naYAZQ68p2dKiFdzP1Kw9pz98h0r3/MM38/WZ1oDZ6YHtzx+D8boFjKLrfHkSYhYU4UgUZIqpYpSFVS43KPdCwjy7mG1cqB/c6D0AL22MQd0knzgl8jEswmA+WG2ptqggu/KZ6U7BlbQfhOXRvFe6/Wyyf6geJJZinYjwWaWkjC8Gx59a4N9fdUojxQJOIq2aBAbatqu56CJUHM2r9W+W5n95TcNTi3LajicwyFgRPm5Yo7gjY2fdNFzl1Qz+c="
                )
        );

        final Attributes attributes = getAttributes();
        attributes.setHealth(200);
    }
}
