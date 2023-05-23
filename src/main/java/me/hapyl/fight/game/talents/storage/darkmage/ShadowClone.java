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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class ShadowClone extends DarkMageTalent {

    @DisplayField(suffix = "blocks") protected final double damageRadius = 3.0d;
    @DisplayField protected final double damage = 3.0d;

    private final Map<Player, ShadowCloneNPC> clones;

    public ShadowClone() {
        super("Shadow Clone", """
                Creates a shadow clone of you at your current location and completely hides you.
                                        
                After a brief delay or whenever enemy hits the clone, it explodes, stunning, blinding and dealing damage to nearby players.
                """, Material.NETHERITE_SCRAP);

        clones = Maps.newHashMap();

        setDuration(60);
        setCd(300);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The clone will not disappear. Instead, if will persist until you take damage. When taking damage, nullify it and teleport to the clone.";
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

    public void removeNpc(Player player, int delay) {
        final ShadowCloneNPC clone = clones.remove(player);

        if (clone != null) {
            clone.remove(delay);
        }
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

        // Show player
        GameTask.runLater(() -> Utils.showPlayer(player), getDuration());

        // Only explode if not using ultimate
        if (witherData == null) {
            shadowClone.explode(null, getDuration());
            removeNpc(player, getDuration());
        }

        clones.put(player, shadowClone);

        return Response.OK;
    }

}
