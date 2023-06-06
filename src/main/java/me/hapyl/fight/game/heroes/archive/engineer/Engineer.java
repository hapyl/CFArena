package me.hapyl.fight.game.heroes.archive.engineer;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.preset.HotbarItem;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.util.Map;

public class Engineer extends Hero implements DisabledHero {

    public final int IRON_RECHARGE_RATE = Tick.fromSecond(10);
    public final int MAX_IRON = 10;

    private final Map<Player, Integer> playerIron = Maps.newHashMap();

    public Engineer() {
        super("Engineer");

        setRole(Role.STRATEGIST);
        setItem("55f0bfea3071a0eb37bcc2ca6126a8bdd79b79947734d86e26e4d4f4c7aa9");
    }

    @Override
    public void useUltimate(Player player) {
    }

    public int getIron(Player player) {
        return playerIron.computeIfAbsent(player, v -> 0);
    }

    public void addIron(Player player, int amount) {
        playerIron.compute(player, (p, i) -> Numbers.clamp(i == null ? amount : i + amount, 0, MAX_IRON));
        updateIron(player);
    }

    public void updateIron(Player player) {
        final PlayerInventory inventory = player.getInventory();

        inventory.setItem(
                HotbarItem.HERO_ITEM.getSlot(),
                ItemBuilder.of(Material.IRON_INGOT, "&aIron", "You iron to build your structures!")
                        .setAmount(playerIron.getOrDefault(player, 1))
                        .asIcon()
        );
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.ENGINEER.getAlivePlayers().forEach(player -> {
                    addIron(player.getPlayer(), 1);
                });
            }
        }.runTaskTimer(IRON_RECHARGE_RATE, IRON_RECHARGE_RATE);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ENGINEER_SENTRY.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ENGINEER_TURRET.getTalent();
    }

    @Nullable
    @Override
    public Talent getThirdTalent() {
        return Talents.ENGINEER_RECALL.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ENGINEER_PASSIVE.getTalent();
    }
}
