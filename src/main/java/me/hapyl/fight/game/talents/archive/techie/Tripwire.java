package me.hapyl.fight.game.talents.archive.techie;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Set;

public class Tripwire {

    private final Player player;
    private final Set<Block> blocks;
    private final long createdAt;

    public Tripwire(Player player, Set<Block> blocks) {
        this.player = player;
        this.blocks = blocks;
        this.createdAt = System.currentTimeMillis();

        PlayerLib.playSound(player, Sound.ENTITY_SPIDER_AMBIENT, 0.75f);
    }

    public boolean isActive() {
        return (System.currentTimeMillis() - createdAt) >= (Talents.TRAP_WIRE.getTalent(TrapWire.class).getWindupTimeAsMillis());
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void drawLine() {
        for (Block block : blocks) {
            PlayerLib.spawnParticle(
                    getPlayer(),
                    BukkitUtils.centerLocation(block.getLocation()).subtract(0.0d, 0.4d, 0.0d),
                    Particle.CRIT,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }
    }

    public void setBlocks() {
        this.blocks.forEach(block -> block.setType(Material.TRIPWIRE, false));
    }

    public void clearBlocks() {
        this.blocks.forEach(block -> block.setType(Material.AIR, false));
    }

    public void affectPlayer(GamePlayer player) {
        //PlayerLib.addEffect(player, PotionEffectType.SLOW, 80, 4);
        //GamePlayer.getPlayer(player).addEffect(GameEffectType.VULNERABLE, 80);
        //
        //// Fx
        //Glowing.glow(player, ChatColor.RED, 80, getPlayer());
        //PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_SCREAM, 1.25f);
        //Chat.sendTitle(this.getPlayer(), "&aTripwire Triggered!", "&7You caught " + player.getName(), 10, 20, 10);
    }

    public boolean isBlockATrap(Block block) {
        return this.getBlocks().contains(block);
    }

}
