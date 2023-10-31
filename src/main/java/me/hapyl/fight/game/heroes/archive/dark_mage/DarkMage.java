package me.hapyl.fight.game.heroes.archive.dark_mage;

import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.KeepNull;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowClone;
import me.hapyl.fight.game.talents.archive.dark_mage.ShadowCloneNPC;
import me.hapyl.fight.game.ui.UIFormat;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static org.bukkit.Sound.*;

public class DarkMage extends Hero implements ComplexHero, Listener {

    private final double PASSIVE_CHANCE = 0.12d;

    private final PlayerMap<DarkMageSpell> spellMap = PlayerMap.newMap();
    private final PlayerMap<WitherData> withers = PlayerMap.newMap();

    public DarkMage() {
        super("Dark Mage");

        setArchetype(Archetype.MAGIC);

        setDescription("A mage that was cursed by &8&lDark &8&lMagic&8&o. But even it couldn't kill him...");
        setItem("e6ca63569e8728722ecc4d12020e42f086830e34e82db55cf5c8ecd51c8c8c29");

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.CRIT_CHANCE, 0.15d);

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(102, 255, 255);
        equipment.setLeggings(Material.IRON_LEGGINGS);
        equipment.setBoots(153, 51, 51);

        setWeapon(new Weapon(Material.WOODEN_HOE)
                .setName("Ancient Wand")
                .setDamage(7.0d)
                .setDescription("""
                        An ancient item capable of casting the darkest of spells...
                                                
                        &e&lSpell Mode:
                        &6&lRIGHT&7 click to enter spell mode.
                                                
                        Add buttons with &nright&7 or &nleft&7 clicks (R or L). Two buttons activate the corresponding spells.
                                                
                        &b;;Hover over the talents to see their usage.
                        """));
        //"A powerful wand, that's capable of casting multiple spells!____&e&lRIGHT CLICK &7to enter casting, then, combine &e&lRIGHT CLICK &7and/or &e&lLEFT CLICK &7to execute the spell!"

        setUltimate(new UltimateTalent("Witherborn", """
                Summon a baby wither that will assist you in battle for {duration}.
                                
                While attacking, the wither will unleash a coordinated attack.
                                
                While casting a spell, the wither will improve the spell.
                """, 70)
                .setItem(Material.WITHER_SKELETON_SKULL)
                .setDuration(240)
                .setCooldownSec(30)
                .setSound(Sound.ENTITY_WITHER_SPAWN, 2.0f));
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        killWither(player);
    }

    @Override
    public void onStop() {
        withers.values().forEach(WitherData::remove);
        withers.clear();
    }

    @Override
    public void useUltimate(@Nonnull GamePlayer player) {
        killWither(player);
        withers.put(player, new WitherData(player));
    }

    @Override
    public void onUltimateEnd(@Nonnull GamePlayer player) {
        killWither(player);
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final ShadowClone talent = getFourthTalent();
        final GamePlayer player = input.getEntityAsPlayer();
        final LivingGameEntity entity = input.getDamagerAsLiving();
        final ShadowCloneNPC clone = talent.getClone(player);

        // Handle passive
        if (entity != null && new Random().nextDouble() < PASSIVE_CHANCE) {
            entity.addEffect(GameEffectType.WITHER_BLOOD, 60, true);

            entity.sendMessage("&8☠ &c%s poisoned your blood!", player.getName());
            player.sendMessage("&8☠ &aYou poisoned %s's blood!", entity.getName());
        }

        // Handle clone
        if (clone != null && clone.ultimate) {
            player.teleport(clone.getLocation());
            talent.removeClone(clone);

            // Fx
            player.sendMessage("&aYour %s nullified the damage!", talent.getName());

            player.addPotionEffect(PotionEffectType.BLINDNESS, 10, 1);

            player.playSound(ENTITY_ENDERMAN_TELEPORT, 1.0f);
            player.playSound(ENTITY_PLAYER_BREATH, 1.0f);
            player.playSound(ENTITY_GHAST_SCREAM, 0.75f);

            return DamageOutput.CANCEL;
        }

        return null;
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer gamePlayer = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        // Skip witherboard damage
        if (gamePlayer == null || input.getDamageCause() == EnumDamageCause.WITHERBORN) {
            return null;
        }

        final WitherData data = getWither(gamePlayer);

        if (data != null) {
            data.assistAttack(entity);
        }

        return null;
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
        return withers.get(player);
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

    //@EventHandler()
    //public void handleProjectileHit(ProjectileHitEvent ev) {
    //    if (!(ev.getEntity() instanceof WitherSkull skull) || !(skull.getShooter() instanceof Player player)) {
    //        return;
    //    }
    //
    //    Utils.getPlayersInRange(skull.getLocation(), 3.0d).forEach(victim -> {
    //        GamePlayer.damageEntity(victim, 10.0d, player, EnumDamageCause.WITHER_SKULLED);
    //    });
    //}

    @Override
    @Nonnull
    public ShadowClone getFourthTalent() {
        return (ShadowClone) Talents.SHADOW_CLONE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.DARK_MAGE_PASSIVE.getTalent();
    }

    @Override
    @KeepNull
    public Talent getFifthTalent() {
        return null;
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
        // Check for actual wand maybe?
        if (player.getInventory().getItemInMainHand().getType() != getWeapon().getMaterial()) {
            return;
        }

        // Handle wand
        final DarkMageSpell spell = spellMap.computeIfAbsent(player, DarkMageSpell::new);

        // When empty:
        // - Right-clicking ONCE will enter the mode.

        // When not empty:
        // Either left-clicking or right-clicking will add the button.
        // If after adding the button, the spell is full, it will be cast.

        // Check for timeout
        if (spell.isTimeout()) {
            spell.clear();
        }

        final WitherData data = getWither(player);

        if (!isLeftClick) {
            if (spell.isEmpty()) {
                spell.markUsed();
                spell.display();
                return;
            }

            spell.addButton(DarkMageSpell.SpellButton.RIGHT);
        }
        else if (!spell.isEmpty()) {
            spell.addButton(DarkMageSpell.SpellButton.LEFT);
        }

        final boolean usingUltimate = isUsingUltimate(player);
        if (spell.isFull()) {
            spell.cast(data);
        }
    }

    private void killWither(GamePlayer player) {
        final WitherData data = withers.get(player);

        if (data != null) {
            data.remove();
        }

        withers.remove(player);
    }

    private void updateWitherName(Player player, Wither wither) {
        wither.setCustomName(Chat.format(
                "&4&l☠ &c%s %s &a&l%s ❤",
                player.getName(),
                UIFormat.DIV,
                BukkitUtils.decimalFormat(wither.getHealth())
        ));
    }

}
