package me.hapyl.fight.game.heroes.archive.engineer;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.preset.HotbarItem;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.engineer.Construct;
import me.hapyl.fight.game.talents.archive.engineer.ImmutableArray;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.util.Map;

public class Engineer extends Hero implements DisabledHero {

    public final int IRON_RECHARGE_RATE = 1;
    public final int MAX_IRON = 10;

    private final Map<Player, Construct> constructs = Maps.newHashMap();
    private final Map<Player, Integer> playerIron = Maps.newHashMap();

    public Engineer() {
        super("Engineer");

        setArchetype(Archetype.STRATEGY);

        setItem("55f0bfea3071a0eb37bcc2ca6126a8bdd79b79947734d86e26e4d4f4c7aa9");
    }

    @Override
    public void onDeath(Player player) {
        Nulls.runIfNotNull(constructs.remove(player), Construct::remove);
    }

    @Nullable
    public Construct getConstruct(Player player) {
        return constructs.get(player);
    }

    /**
     * This removes the current construct if exists and refunds 50% of the cost.
     *
     * @param player - Player.
     */
    public void destruct(Player player) {
        final Construct construct = constructs.remove(player);

        if (construct == null) {
            return;
        }

        Heroes.ENGINEER.getHero(Engineer.class).addIron(player, (int) (construct.getCost() * 0.25));
        construct.remove();
    }

    @Override
    public void useUltimate(Player player) {
    }

    public int getIron(Player player) {
        return playerIron.computeIfAbsent(player, v -> 0);
    }

    public void subtractIron(Player player, int amount) {
        addIron(player, -amount);
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

    @Nullable
    public Construct removeConstruct(Player player) {
        final Construct construct = constructs.remove(player);

        if (construct == null) {
            return null;
        }

        construct.remove();

        return construct;
    }

    public void setConstruct(Player player, Construct construct) {
        constructs.put(player, construct);

        final ImmutableArray<Integer> duration = construct.durationScaled();

        new GameTask() {
            private int tick = 0;

            private void remove() {
                removeConstruct(player);
                cancel();
            }

            @Override
            public void run() {
                final int dTick = duration.get(construct.getLevel(), Construct.MAX_DURATION_SEC) * 20;

                if (construct.getStand().isDead() || (tick++ >= dTick)) {
                    remove();
                    return;
                }

                construct.onTick();
            }
        }.runTaskTimer(0, 1);
    }
}
