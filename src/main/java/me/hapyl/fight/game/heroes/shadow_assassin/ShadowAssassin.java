package me.hapyl.fight.game.heroes.shadow_assassin;

import me.hapyl.eterna.module.particle.ParticleBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.shadow_assassin.DarkCover;
import me.hapyl.fight.game.talents.shadow_assassin.PlayerCloneList;
import me.hapyl.fight.game.talents.shadow_assassin.ShadowAssassinClone;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowAssassin extends Hero implements Listener, UIComponent {

    public final Equipment furyEquipment = new Equipment();

    private final int nevermissCd = 20;
    private final double nevermissDistance = 10.0d;

    public final double attackIncrease;
    public final double speedDecrease;

    private final PlayerMap<ShadowAssassinData> playerData = PlayerMap.newMap();

    public ShadowAssassin(@Nonnull DatabaseKey key) {
        super(key, "Shadow Assassin");

        setArchetypes(Archetype.DAMAGE, Archetype.STRATEGY, Archetype.MELEE, Archetype.SELF_BUFF, Archetype.POWERFUL_ULTIMATE);
        setGender(Gender.UNKNOWN);
        setRace(Race.UNKNOWN);

        setDescription("""
                An assassin with anger management issues from dimension of shadows. Capable of switching between being &9&oStealth&8&o, and &c&oFurious&8&o.
                """);
        setItem("d7fcfa5b0af855f314606a5cd2b597475286a152d1ee08d9949a6386cbc46a8e");

        final HeroAttributes attributes = getAttributes();
        attributes.setAttack(70);
        attributes.setSpeed(120);

        this.attackIncrease = AttributeType.ATTACK.getDefaultValue() - attributes.get(AttributeType.ATTACK);
        this.speedDecrease = attributes.get(AttributeType.SPEED) - AttributeType.SPEED.getDefaultValue();

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(14, 23, 41);
        equipment.setLeggings(7, 12, 23);
        equipment.setBoots(Color.BLACK);

        furyEquipment.setTexture("2bbc217afc3a10cb268fec0426ccdee2b83906a8162d69f8c6d065b5aebc119c");
        furyEquipment.setChestPlate(54, 22, 22);
        furyEquipment.setLeggings(28, 10, 10);
        furyEquipment.setBoots(Color.BLACK);

        setWeapon(new ShadowAssassinWeapon(this));
        setUltimate(new ShadowAssassinUltimate());

        getUltimate().addAttributeDescription("Cooldown Per Hit", nevermissCd);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        playerData.clear();
    }

    @Nonnull
    public ShadowAssassinData getData(@Nonnull GamePlayer player) {
        return playerData.computeIfAbsent(player, fn -> new ShadowAssassinData(player, this));
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        // Reset data
        playerData.remove(player);
    }

    @EventHandler()
    public void handleUltimate(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final ShadowAssassinWeapon weapon = getWeapon();

        if (player == null
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || ev.getAction() == Action.PHYSICAL
                || !validatePlayer(player)
                || !player.isUsingUltimate()
                || player.hasCooldown(weapon.getMaterial())) {
            return;
        }

        final LivingGameEntity livingEntity = getNearestEntity(player);

        if (livingEntity == null) {
            player.sendMessage("&cNo valid opponent!");
            player.playSound(Sound.ENTITY_SILVERFISH_AMBIENT, 0.0f);
            return;
        }

        livingEntity.damage(weapon.getDamage(), player, EnumDamageCause.NEVERMISS);
        player.setCooldown(weapon.getMaterial(), nevermissCd);

        // Fx
        player.playWorldSound(Sound.BLOCK_NETHER_ORE_BREAK, 1.75f);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    if (!player.hasEffect(Effects.INVISIBILITY)) {
                        return;
                    }

                    player.spawnWorldParticle(Particle.ENCHANT, 5, 0.5, 1, 0.5, 1f);
                });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        getSecondTalent().onDamage(player, entity, damage);
        return false;
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getEntityAsPlayer();
        final DarkCover darkCover = getSecondTalent();

        if (darkCover.isInDarkCover(player)) {
            instance.setCancelled(true);
        }
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (player == null || !instance.isEntityAttack()) {
            return;
        }

        final LivingGameEntity entity = instance.getEntity();

        if (!validateCanBackStab(player, entity)) {
            return;
        }

        final Vector playerDirection = player.getLocation().getDirection();
        final Vector entityDirection = entity.getLocation().getDirection();

        if (playerDirection.dot(entityDirection) > 0) {
            getWeapon().performBackStab(player, entity);
        }
    }

    public void displayFootprints(Location location) {
        ParticleBuilder
                .blockDust(location.getBlock().getRelative(BlockFace.DOWN).getType())
                .display(location, 3, 0.25d, 0.0d, 0.25d, 1.0f);
    }

    @Override
    public ShadowAssassinWeapon getWeapon() {
        return (ShadowAssassinWeapon) super.getWeapon();
    }

    @EventHandler()
    public void handlePlayerSneakEvent(PlayerToggleSneakEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null || !validatePlayer(player)) {
            return;
        }

        final ShadowAssassinClone talent = getThirdTalent();
        final PlayerCloneList playerClones = talent.getPlayerClones(player);

        playerClones.linkToClone();
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.SHADOW_SWITCH;
    }

    @Override
    public DarkCover getSecondTalent() {
        return (DarkCover) TalentRegistry.DARK_COVER;
    }

    @Override
    public ShadowAssassinClone getThirdTalent() {
        return (ShadowAssassinClone) TalentRegistry.SHADOW_ASSASSIN_CLONE;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SHADOW_ENERGY;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final int energy = getData(player).getEnergy();
        return ChatColor.DARK_PURPLE + Named.SHADOW_ENERGY.getCharacter() + energy +
                (energy == ShadowAssassinData.MAX_ENERGY ? " &lMAX!" : "");
    }

    @Nullable
    private LivingGameEntity getNearestEntity(GamePlayer player) {
        return Collect.targetEntityDot(player, nevermissDistance, 0.5d, t -> !player.isSelfOrTeammate(t) && t.hasLineOfSight(player));
    }

    private boolean validateCanBackStab(GamePlayer player, LivingGameEntity entity) {
        return entity != null
                && !player.isUsingUltimate()
                && player != entity
                && !player.hasCooldown(getWeapon().getMaterial()) && player.isHeldSlot(HotbarSlots.WEAPON);
    }

    private class ShadowAssassinUltimate extends UltimateTalent {
        public ShadowAssassinUltimate() {
            super(ShadowAssassin.this, "Extreme Focus", 80);

            setDescription("""
                    Enter {name} for {duration}.
                    
                    While active, your &amelee&7 attacks will &nnot&7 miss if an &cenemy&7 is within your line of sight.
                    
                    You cannot perform &eShadow Stab&7 while {name} is active.
                    """
            );

            setType(TalentType.ENHANCE);
            setItem(Material.GOLDEN_CARROT);
            setDurationSec(10);
            setCooldownSec(40);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            // FIXME (hapyl): 001, Mar 1: Change for reach attribute if it's 1.21 or whatever the fuck it is
            player.setCooldown(getWeapon().getMaterial(), 0);

            // Fx
            player.playWorldSound(Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
            player.playWorldSound(Sound.BLOCK_BEACON_AMBIENT, 1.75f);
            player.addEffect(Effects.SLOW, getUltimateDuration());

            GameTask.runLater(() -> player.playWorldSound(Sound.BLOCK_BEACON_DEACTIVATE, 1.85f), getUltimateDuration());

            return UltimateResponse.OK;
        }
    }
}
