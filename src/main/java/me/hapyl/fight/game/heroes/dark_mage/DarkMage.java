package me.hapyl.fight.game.heroes.dark_mage;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.dark_mage.ShadowCloneNPC;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DarkMage extends Hero implements ComplexHero, Listener, PlayerDataHandler<DarkMageData>, UIComplexComponent {

    private final PlayerDataMap<DarkMageData> playerData = PlayerMap.newDataMap(DarkMageData::new);

    public DarkMage(@Nonnull Key key) {
        super(key, "Dark Mage");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.HEXBANE);
        profile.setAffiliation(Affiliation.THE_WITHERS);
        profile.setGender(Gender.MALE);

        setDescription("A mage, who was cursed by the &8&l&oDark Magic&8&o, but even it couldn't kill him...");
        setItem("e6ca63569e8728722ecc4d12020e42f086830e34e82db55cf5c8ecd51c8c8c29");

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.CRIT_CHANCE, 0.15d);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(102, 255, 255);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(153, 51, 51);

        setWeapon(new DarkMageWeapon());
        setUltimate(new DarkMageUltimate());
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                playerData.values()
                        .forEach(data -> data.forEach(entity -> data.player.spawnParticle(
                                entity.getLocation().add(0, 2.5, 0),
                                Particle.SMOKE,
                                5,
                                0.1,
                                0.1,
                                0.1,
                                0.01f
                        )));
            }
        }.runTaskTimer(0, 5);
    }

    @EventHandler()
    public void handleDataOtherDeath(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        playerData.values().forEach(data -> data.remove(entity));
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        // Skip Wither Rose
        if (player == null || player.isUsingUltimate() || !instance.isEntityAttack()) {
            return;
        }

        final DarkMageData data = getPlayerData(player);
        data.addWithered(entity);
    }

    @EventHandler()
    public void handleInteraction(PlayerInteractAtEntityEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null || !validatePlayer(player) || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        processSpellClick(player, false);
    }

    @EventHandler()
    public void handleInteraction(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final Action action = ev.getAction();

        if (player == null
                || !validatePlayer(player)
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || player.hasCooldownInternal(getWeapon().getMaterial())
                || ev.getAction() == Action.PHYSICAL) {
            return;
        }

        final boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        processSpellClick(player, isLeftClick);
        ev.setCancelled(true);
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.BLINDING_CURSE;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.SLOWING_AURA;
    }

    @Override
    public Talent getThirdTalent() {
        return TalentRegistry.HEALING_AURA;
    }

    @Override
    @Nonnull
    public ShadowClone getFourthTalent() {
        return TalentRegistry.SHADOW_CLONE;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.DARK_MAGE_PASSIVE;
    }

    @Nonnull
    @Override
    public PlayerDataMap<DarkMageData> getDataMap() {
        return playerData;
    }

    @Nullable
    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        final ShadowClone talent = getFourthTalent();
        final ShadowCloneNPC clone = talent.getClone(player);
        final DarkMageData data = getPlayerData(player);

        final int witheredCount = data.getWitheredCount();

        return List.of("%s %s".formatted(Named.WITHER_ROSE.getCharacter(), witheredCount), clone != null ? clone.toString() : "");
    }

    // [hapyl's rant about interaction detection]
    //
    // This is the stupidest thing in the fucking world,
    // why is it if I'm clicking on entity, it doesn't fire interact event,
    // I'm still fucking interacting aren't I?
    // And also the fucking left-clicking entity does not fire the event either, like why?
    // I'm clearly fucking interacting???
    // But of course, the event handles with 2 hands, even if I have nothing in my b hand, makes sense.
    private void processSpellClick(GamePlayer player, boolean isLeftClick) {
        if (!player.isHeldSlot(HotBarSlot.WEAPON)) {
            return;
        }

        final DarkMageData data = getPlayerData(player);
        final DarkMageSpell spell = data.getDarkMageSpell();

        // When empty:
        // - Right-clicking ONCE will enter the mode.

        // When not empty:
        // Either left-clicking or right-clicking will add the button.
        // If after adding the button, the spell is full, it will be cast.

        // Check for timeout
        if (spell.isTimeout()) {
            spell.remove();
        }

        if (!isLeftClick) {
            if (spell.isEmpty()) {
                spell.markUsed();
                spell.display();
                return;
            }

            spell.addButton(SpellButton.RIGHT);
        }
        else if (!spell.isEmpty()) {
            spell.addButton(SpellButton.LEFT);
        }

        if (spell.isFull()) {
            data.cast();
        }
    }

    public class DarkMageUltimate extends UltimateTalent {

        @DisplayField(percentage = true) public final double cooldownReduction = 0.5d;
        @DisplayField(percentage = true) public final double durationIncrease = 0.3d;
        @DisplayField private final int durationIncreasePerStack = 30;
        @DisplayField private final int baseDuration = Tick.fromSecond(12);

        public DarkMageUltimate() {
            super(DarkMage.this, "Witherborn", 60);

            setDescription("""
                    Raised by the %1$sWithers&7, they will &nalways&7 assist you in battle.
                    
                    The %1$swither&7 will &nimprove&7 your &bspells&7 by increasing their &bduration&7 and decreasing &acooldowns&7.
                    
                    Each stack of %2$s will &nincrease&7 the &bduration&7 of the %1$swither&7.
                    
                    &8;;You cannot plant Wither Roses while the ultimate is active!
                    """.formatted(Color.WITHERS, Named.WITHER_ROSE)
            );

            setType(TalentType.ENHANCE);
            setItem(Material.WITHER_SKELETON_SKULL);
            setSound(Sound.ENTITY_WITHER_SPAWN, 2.0f);

            setCooldownSec(30);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
                final DarkMageData playerData = getPlayerData(player);

                // Remove clone if present
                final ShadowClone talent = getFourthTalent();
                talent.removeClone(player);

                final int witheredCount = playerData.getWitheredCount();
                final int duration = baseDuration + (durationIncreasePerStack * witheredCount);

                player.setUsingUltimate(duration);

                playerData.resetWitheredCountWithFx();
                playerData.newWither(duration);

                player.schedule(playerData::removeWither, duration);
            });
        }
    }
}
