package me.hapyl.fight.game.heroes.archive.swooper;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Set;

public class SwooperData extends PlayerData {

    private final Set<LivingGameEntity> highlightedEntities;
    protected int ultimateShots;
    private boolean stealthMode;

    public SwooperData(GamePlayer player) {
        super(player);

        this.highlightedEntities = Sets.newHashSet();
    }

    public int getUltimateShots() {
        return ultimateShots;
    }

    public boolean isStealthMode() {
        return stealthMode;
    }

    public void setStealthMode(boolean b) {
        this.stealthMode = b;

        // Enter mode
        if (b) {
            player.hidePlayer();

            // Fx
            player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
        }
        else {
            player.showPlayer();

            // Fx
            player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.0f);
        }

        // Title because humans are stupid
        player.sendTitle(Named.REFRACTION.getCharacterColored(), b ? "&aᴀᴄᴛɪᴠᴀᴛᴇᴅ" : "&cᴅᴇᴀᴄᴛɪᴠᴀᴛᴇᴅ", 0, 15, 5);
    }

    public void addHighlighted(@Nonnull LivingGameEntity entity) {
        highlightedEntities.add(entity);

        Glowing.glowInfinitely(entity.getEntity(), ChatColor.DARK_PURPLE, player.getPlayer());
    }

    public void removeHighlighted(@Nonnull LivingGameEntity entity) {
        if (!highlightedEntities.contains(entity)) {
            return;
        }

        highlightedEntities.remove(entity);

        Glowing.stopGlowing(player.getPlayer(), entity.getEntity());
    }

    public boolean isHighlighted(LivingGameEntity entity) {
        return highlightedEntities.contains(entity);
    }

    @Override
    public void remove() {
        highlightedEntities.forEach(entity -> {
            Glowing.stopGlowing(player.getPlayer(), entity.getEntity());
        });
        highlightedEntities.clear();
    }
}
