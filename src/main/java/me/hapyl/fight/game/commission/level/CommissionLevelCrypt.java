package me.hapyl.fight.game.commission.level;

import me.hapyl.fight.game.commission.CommissionReward;
import me.hapyl.fight.game.commission.MonsterSpawn;
import me.hapyl.fight.game.maps.CommissionLevel;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.PlayerRequirement;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;

public class CommissionLevelCrypt extends CommissionLevel {

    public CommissionLevelCrypt(@Nonnull EnumLevel handle) {
        super(handle, "The Crypts");

        setDescription("""
                A crypt filled with undead.
                """);

        texture = "5e897cb0b465a71e902148d58ed352793e6340f8e85d7d67e3f4a89f1bc0e6b3";
        boss = () -> Registries.entities().LEECH_BOSS;

        requirements.add(PlayerRequirement.of("Test Requirement", player -> false));
        requirements.add(PlayerRequirement.of("Test Requirement 2", player -> true));

        rewards.addGuaranteedReward(new CommissionReward("&aGuaranteed Reward"));

        rewards.add(new CommissionReward("&aCommon Reward"), 100);
        rewards.add(new CommissionReward("&aCommon Reward"), 100);
        rewards.add(new CommissionReward("&aCommon Reward"), 50);
        rewards.add(new CommissionReward("&bRare Reward"), 25);
        rewards.add(new CommissionReward("&6Insane Reward"), 10);
        rewards.add(new CommissionReward("&dRNGEsus Reward"), 5);
        rewards.add(new CommissionReward("&cInsane Reward"), 1);

        expReward = 250;

        addLocation(87, 60, 500, 90f, 0f);

        // Setup entities
        monsterSpawns.add(
                new MonsterSpawn(80, 60, 500, 3.0f, 10, new BoundingBox(77, 59, 495, 83, 67, 505))
                        .add(registry -> registry.ROOKIE_ZOMBIE, 2, 10)
                        .add(registry -> registry.MEGA_GOLEM, 2, 1)
        );
    }

}
