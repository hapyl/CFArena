package me.hapyl.fight.game.heroes.archive.dark_mage;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowCloneNPC;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static org.bukkit.Sound.*;

public class DarkMage extends Hero implements ComplexHero, Listener, PlayerDataHandler<DarkMageData> {

    @DisplayField private final double passiveChance = 0.12d;
    @DisplayField private final double ultimateCooldownBoost = 0.5d;

    private final PlayerDataMap<DarkMageData> playerData = PlayerMap.newDataMap(DarkMageData::new);

    public DarkMage(@Nonnull Heroes handle) {
        super(handle, "Dark Mage");

        setArchetype(Archetype.MAGIC);
        setAffiliation(Affiliation.THE_WITHERS);

        setDescription("A mage who was cursed by the &8&l&oDark Magic&8&o, but even it couldn't kill him...");
        setItem("e6ca63569e8728722ecc4d12020e42f086830e34e82db55cf5c8ecd51c8c8c29");

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.CRIT_CHANCE, 0.15d);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(102, 255, 255);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(153, 51, 51);

        setWeapon(new DarkMageWeapon());

        setUltimate(new UltimateTalent(this, "Witherborn", """
                Raised by the &8Withers&7, they will always assist you in battle.
                                
                While &cattacking&7, the &8Wither&7 will unleash a &acoordinated&7 attack.
                                
                While &acastring&7 a spell, it will be &aimproved&7, and the cooldown is &breduced&7.
                                
                After {duration}, the &8Wither&7 will leave.
                """, 70)
                .setType(Talent.Type.ENHANCE)
                .setItem(Material.WITHER_SKELETON_SKULL)
                .setDurationSec(12)
                .setCooldownSec(30)
                .setSound(Sound.ENTITY_WITHER_SPAWN, 2.0f)
                .appendAttributeDescription("Assist Delay", WitherData.ASSIST_DELAY)
                .appendAttributeDescription("Assist Hits", WitherData.ASSIST_HITS)
                .appendAttributeDescription("Assist Damage", WitherData.ASSIST_DAMAGE_TOTAL)
        );
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final EntityAttributes attributes = player.getAttributes();

        getPlayerData(player).newWither();
        attributes.decreaseTemporary(Temper.WITHERBORN, AttributeType.COOLDOWN_MODIFIER, ultimateCooldownBoost, getUltimateDuration());
        return UltimateCallback.OK;
    }

    @Override
    public void onUltimateEnd(@Nonnull GamePlayer player) {
        getPlayerData(player).removeWither();
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final ShadowClone talent = getFourthTalent();
        final GamePlayer player = instance.getEntityAsPlayer();
        final LivingGameEntity entity = instance.getDamager();
        final ShadowCloneNPC clone = talent.getClone(player);

        // Handle passive
        if (entity != null && new Random().nextDouble() < passiveChance) {
            entity.addEffect(Effects.WITHER_BLOOD, 60, true);

            entity.sendMessage("&8☠ &c%s poisoned your blood!", player.getName());
            player.sendMessage("&8☠ &aYou poisoned %s's blood!", entity.getName());
        }

        // Handle clone
        if (clone != null && clone.isUltimate()) {
            player.teleport(clone.getLocation());
            talent.removeClone(clone);

            // Fx
            player.sendMessage("&aYour %s nullified the damage!", talent.getName());

            player.addEffect(Effects.BLINDNESS, 1, 10);

            player.playSound(ENTITY_ENDERMAN_TELEPORT, 1.0f);
            player.playSound(ENTITY_PLAYER_BREATH, 1.0f);
            player.playSound(ENTITY_GHAST_SCREAM, 0.75f);

            instance.setCancelled(true);
        }
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer gamePlayer = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        // Skip witherboard damage
        if (gamePlayer == null || instance.getCause() == EnumDamageCause.WITHERBORN || !instance.isEntityAttack()) {
            return;
        }

        final WitherData data = getWither(gamePlayer);

        if (data != null) {
            data.assistAttack(entity);
        }
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
                || player.hasCooldown(getWeapon().getMaterial())
                || ev.getAction() == Action.PHYSICAL) {
            return;
        }

        final boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        processSpellClick(player, isLeftClick);
        ev.setCancelled(true);
    }

    @Nullable
    public WitherData getWither(GamePlayer player) {
        return getPlayerData(player).getWitherData();
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.BLINDING_CURSE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SLOWING_AURA.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.HEALING_AURA.getTalent();
    }

    @Override
    @Nonnull
    public ShadowClone getFourthTalent() {
        return (ShadowClone) Talents.SHADOW_CLONE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.DARK_MAGE_PASSIVE.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<DarkMageData> getDataMap() {
        return playerData;
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
        if (!player.isHeldSlot(HotbarSlots.WEAPON)) {
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

}
