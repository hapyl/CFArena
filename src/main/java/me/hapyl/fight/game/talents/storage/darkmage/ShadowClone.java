package me.hapyl.fight.game.talents.storage.darkmage;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.DarkMage;
import me.hapyl.fight.game.heroes.storage.extra.DarkMageSpell;
import me.hapyl.fight.game.heroes.storage.extra.WitherData;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class ShadowClone extends DarkMageTalent {

    @DisplayField(suffix = "blocks")
    protected final double damageRadius = 3.0d;
    @DisplayField
    protected final double damage = 3.0d;

    private final Map<Player, ShadowCloneNPC> clones;

    public ShadowClone() {
        super("Shadow Clone", """
                Create a shadow clone of you at your current location and completely hides you.
                                        
                After a brief delay or whenever an enemy hits the clone, it explodes, stunning, blinding and dealing damage to nearby players.
                """, Material.NETHERITE_SCRAP);

        clones = Maps.newHashMap();

        setDuration(60);
        setCooldown(300);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The clone persists until you take damage, at which point the damage is nullified, and you teleport to the clone.";
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton first() {
        return DarkMageSpell.SpellButton.LEFT;
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton second() {
        return DarkMageSpell.SpellButton.RIGHT;
    }

    @Nullable
    public ShadowCloneNPC getClone(Player player) {
        return clones.get(player);
    }

    @Override
    public Response executeSpell(Player player) {
        final WitherData witherData = Heroes.DARK_MAGE.getHero(DarkMage.class).getWither(player);
        final HumanNPC previousClone = getClone(player);

        if (previousClone != null) {
            previousClone.remove();
            Chat.sendMessage(player, "&aYour previous clone was removed!");
        }

        final ShadowCloneNPC shadowClone = new ShadowCloneNPC(player);
        shadowClone.ultimate = witherData != null;

        // Only explode if not using ultimate
        if (witherData == null) {
            GameTask.runLater(() -> {
                shadowClone.explode(null);
                clones.remove(player);
            }, getDuration());
        } else {
            // Hide the wither too
            final Wither wither = witherData.wither;

            Utils.hideEntity(wither);
            GameTask.runLater(() -> {
                if (wither == null || wither.isDead()) {
                    return;
                }

                Utils.showEntity(wither);
            }, getDuration());
        }

        clones.put(player, shadowClone);

        return Response.OK;
    }

    public void removeClone(@Nonnull ShadowCloneNPC clone) {
        clone.remove(0);
        clones.remove(clone.player);
    }
}
