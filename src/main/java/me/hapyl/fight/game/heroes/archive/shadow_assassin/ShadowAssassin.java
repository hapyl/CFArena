package me.hapyl.fight.game.heroes.archive.shadow_assassin;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.shadow_assassin.DarkCover;
import me.hapyl.fight.game.talents.archive.shadow_assassin.PlayerCloneList;
import me.hapyl.fight.game.talents.archive.shadow_assassin.ShadowAssassinClone;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowAssassin extends Hero implements Listener, UIComponent {

    public final Equipment furyEquipment = new Equipment();

    private final int nevermissCd = 20;
    private final PlayerMap<Data> playerData = PlayerMap.newMap();

    public ShadowAssassin() {
        super("Shadow Assassin");

        setArchetype(Archetype.STRATEGY);

        setDescription("""
                Well-trained assassin from dimension of shadows.
                Capable of switching between being &9Stealth&8&o, and &cFurious&8&o.
                """);
        setItem("d7fcfa5b0af855f314606a5cd2b597475286a152d1ee08d9949a6386cbc46a8e");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(14, 23, 41);
        equipment.setLeggings(7, 12, 23);
        equipment.setBoots(Color.BLACK);

        furyEquipment.setTexture("2bbc217afc3a10cb268fec0426ccdee2b83906a8162d69f8c6d065b5aebc119c");
        furyEquipment.setChestPlate(54, 22, 22);
        furyEquipment.setLeggings(28, 10, 10);
        furyEquipment.setBoots(Color.BLACK);

        setWeapon(new ShadowAssassinWeapon(this));

        setUltimate(new UltimateTalent(
                "Extreme Focus", """
                Enter {name} for {duration}.
                                
                While active, your &amelee&7 attacks will &nnot&7 miss if an enemy is close enough and has no cover.
                                
                You cannot perform &eShadow Stab&7 while {name} is active.
                """, 80
        )
                .setDurationSec(10)
                .setCooldownSec(40)
                .setItem(Material.GOLDEN_CARROT));

        getUltimate().addAttributeDescription("Cooldown Per Hit", nevermissCd);
    }

    @Override
    public void onStop() {
        playerData.clear();
    }

    @Nonnull
    public Data getData(@Nonnull GamePlayer player) {
        return playerData.computeIfAbsent(player, fn -> new Data(player, this));
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        // Reset data
        playerData.remove(player);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        player.setCooldown(getWeapon().getMaterial(), 0);

        // Fx
        player.playWorldSound(Sound.BLOCK_BEACON_ACTIVATE, 1.75f);
        player.playWorldSound(Sound.BLOCK_BEACON_AMBIENT, 1.75f);
        player.addPotionEffect(PotionEffectType.SLOW, getUltimateDuration(), 0);

        GameTask.runLater(() -> player.playWorldSound(Sound.BLOCK_BEACON_DEACTIVATE, 1.85f), getUltimateDuration());

        return UltimateCallback.OK;
    }

    @EventHandler()
    public void handleUltimate(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final ShadowAssassinWeapon weapon = getWeapon();

        if (player == null
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || ev.getAction() == Action.PHYSICAL
                || !validatePlayer(player)
                || !isUsingUltimate(player)
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
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    if (!player.hasEffect(GameEffectType.INVISIBILITY)) {
                        return;
                    }

                    player.spawnWorldParticle(Particle.ENCHANTMENT_TABLE, 5, 0.5, 1, 0.5, 1f);
                });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public boolean processInvisibilityDamage(GamePlayer player, LivingGameEntity entity, double damage) {
        getSecondTalent().onDamage(player, entity, damage);
        return false;
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final DarkCover darkCover = getSecondTalent();

        if (darkCover.isInDarkCover(player)) {
            return DamageOutput.CANCEL;
        }

        return DamageOutput.OK;
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();

        if (player == null || !input.isEntityAttack()) {
            return DamageOutput.OK;
        }

        final LivingGameEntity entity = input.getEntity();

        if (!validateCanBackStab(player, entity)) {
            return DamageOutput.OK;
        }

        final Vector playerDirection = player.getLocation().getDirection();
        final Vector entityDirection = entity.getLocation().getDirection();

        if (playerDirection.dot(entityDirection) > 0) {
            getWeapon().performBackStab(player, entity);
        }

        return DamageOutput.OK;
    }

    public void displayFootprints(Location location) {
        ParticleBuilder
                .blockDust(location.getBlock().getRelative(BlockFace.DOWN).getType())
                .setAmount(3)
                .setOffX(0.25d)
                .setOffZ(0.25d)
                .display(location);
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
        return Talents.SHADOW_SWITCH.getTalent();
    }

    @Override
    public DarkCover getSecondTalent() {
        return (DarkCover) Talents.DARK_COVER.getTalent();
    }

    @Override
    public ShadowAssassinClone getThirdTalent() {
        return (ShadowAssassinClone) Talents.SHADOW_ASSASSIN_CLONE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SHADOW_ENERGY.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final int energy = getData(player).getEnergy();
        return ChatColor.DARK_PURPLE + Named.SHADOW_ENERGY.getCharacter() + energy + (energy == Data.MAX_ENERGY ? " &lMAX!" : "");
    }

    @Nullable
    private LivingGameEntity getNearestEntity(GamePlayer player) {
        return Collect.targetEntity(player, 10, 0.5d, t -> t.hasLineOfSight(player));
    }

    private boolean validateCanBackStab(GamePlayer player, LivingGameEntity entity) {
        return entity != null
                && !isUsingUltimate(player)
                && player != entity
                && !player.hasCooldown(getWeapon().getMaterial()) && player.isHeldSlot(HotbarSlots.WEAPON);
    }

}
