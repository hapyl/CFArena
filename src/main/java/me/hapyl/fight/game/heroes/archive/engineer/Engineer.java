package me.hapyl.fight.game.heroes.archive.engineer;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.engineer.Construct;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.http.WebSocket;

public class Engineer extends Hero implements Listener {

    public final int IRON_RECHARGE_RATE = 60;
    public final int MAX_IRON = 10;

    private final Weapon ironFist = new Weapon(Material.IRON_BLOCK).setDamage(10.0d).setName("&6&lIron Fist");
    private final int Hitcd = 5;
    public final PlayerMap<Construct> constructs = PlayerMap.newMap();
    private final PlayerMap<Integer> playerIron = PlayerMap.newMap();

    public Engineer() {
        super("Engineer",
                """
            A Genius with 12 PHDs. All of his buildings were made by himself. Though, he uses just 2 of those.
            And your best hope - Not pointed at you.""", Material.IRON_INGOT);

        setArchetype(Archetype.STRATEGY);

        setItem("55f0bfea3071a0eb37bcc2ca6126a8bdd79b79947734d86e26e4d4f4c7aa9");
        setWeapon(new Weapon(Material.IRON_HOE).setName("Prototype Wrench").setDescription("""
                A Prototype Wrench for all the needs.
                It.. Probably hurts to be hit with it.""").setDamage(5.0d));

            final Equipment equipment = getEquipment();
              equipment.setChestPlate(255, 0, 0);
             equipment.setLeggings(0, 0, 0);
             equipment.setBoots(0, 0, 0);



        setUltimate(new UltimateTalent(
                "Mecha-Industries",
                """
                        Create a Big Boss and get ready for a fight!
                         &lBig Boss &7 has more HP and more Attack Stats. 
                         However, it doesn't really like water..""",
                70
        ).setItem(Material.IRON_SWORD)
                .setDurationSec(25)
                .setCooldownSec(35)
                .setSound(Sound.BLOCK_ANVIL_USE, 0.25f));

    }


    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        Nulls.runIfNotNull(constructs.remove(player), Construct::remove);
    }

    @Nullable
    public Construct getConstruct(GamePlayer player) {
        return constructs.get(player);
    }

    /**
     * This removes the current construct if exists and refunds 50% of the cost.
     *
     * @param player - Player.
     */
    public void destruct(GamePlayer player) {
        final Construct construct = constructs.remove(player);

        if (construct == null) {
            return;
        }

        Heroes.ENGINEER.getHero(Engineer.class).addIron(player, (int) (construct.getCost() * 0.25));
        construct.remove();
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        player.setItemAndSnap(HotbarSlots.TALENT_4, ironFist.getItem());
        player.setCooldown(ironFist.getMaterial(), Hitcd);

        int ultimateDuration = getUltimateDuration();

        EntityAttributes ultAttributes = player.getAttributes();
        ultAttributes.increaseTemporary(Temper.MECHA_INDUSTRY, AttributeType.MAX_HEALTH, 120.0d, ultimateDuration);
        ultAttributes.decreaseTemporary(Temper.MECHA_INDUSTRY, AttributeType.SPEED, 0.05d, ultimateDuration);
        ultAttributes.increaseTemporary(Temper.MECHA_INDUSTRY, AttributeType.DEFENSE, 1.0d, ultimateDuration);


        GameTask.runLater(() -> {
            player.setItem(HotbarSlots.TALENT_4, null);
            player.snapToWeapon();
        }, ultimateDuration);
        return UltimateCallback.OK;
    }

    public int getIron(GamePlayer player) {
        return playerIron.computeIfAbsent(player, v -> 0);
    }

    public void subtractIron(GamePlayer player, int amount) {
        addIron(player, -amount);
    }

    public void addIron(GamePlayer player, int amount) {
        playerIron.compute(player, (p, i) -> Numbers.clamp(i == null ? amount : i + amount, 0, MAX_IRON));
        updateIron(player);
    }

    public void updateIron(GamePlayer player) {
        final PlayerInventory inventory = player.getInventory();

        player.setItem(HotbarSlots.HERO_ITEM, ItemBuilder.of(Material.IRON_INGOT, "&aIron", "Your iron to build your structures!")
                .setAmount(playerIron.getOrDefault(player, 1))
                .asIcon());
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.ENGINEER.getAlivePlayers().forEach(player -> {
                    addIron(player, 1);

                    if (!player.getPlayer().isInWater()) {
                        return;
                    }


                    if(!isUsingUltimate(player)){
                        return;
                    }
                    player.damage(10.0d);
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
    public Construct removeConstruct(GamePlayer player) {
        final Construct construct = constructs.remove(player);

        if (construct == null) {
            return null;
        }

        construct.remove();
     //   player.sendMessage("&aYour previous %s was removed!", construct.getName());
        return construct;
    }

    public void setConstruct(GamePlayer player, Construct construct) {
        constructs.put(player, construct);
        construct.runTaskTimer(0, 1);
    }
}
