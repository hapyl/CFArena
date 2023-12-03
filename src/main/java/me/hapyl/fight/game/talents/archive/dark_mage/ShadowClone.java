package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.dark_mage.SpellButton;
import me.hapyl.fight.game.heroes.archive.witcher.WitherData;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Material;
import org.bukkit.entity.Wither;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowClone extends DarkMageTalent {

    @DisplayField(suffix = "blocks")
    protected final double damageRadius = 3.0d;
    @DisplayField
    protected final double damage = 3.0d;

    private final PlayerMap<ShadowCloneNPC> clones = PlayerMap.newMap();

    public ShadowClone() {
        super("Shadow Clone", """
                Create a reflection of &nyourself&7 at your current location and become &binvisible&7.
                                
                After a &abrief delay&7 or whenever the clone is &cdamaged&7, it &4explodes&7, &cdamaging&7 and &eimpairing&7 nearby enemies.
                """);

        setType(Type.IMPAIR);
        setItem(Material.NETHERITE_SCRAP);
        setDurationSec(3);
        setCooldownSec(15);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The clone will &bpersist&7 until you &ntake damage&7, nullifying the damage and &bteleporting&7 you to its location.";
    }

    @Nonnull
    @Override
    public SpellButton first() {
        return SpellButton.LEFT;
    }

    @Nonnull
    @Override
    public SpellButton second() {
        return SpellButton.RIGHT;
    }

    @Nullable
    public ShadowCloneNPC getClone(GamePlayer player) {
        return clones.get(player);
    }

    @Override
    public Response executeSpell(@Nonnull GamePlayer player) {
        final HumanNPC previousClone = clones.remove(player);

        if (previousClone != null) {
            previousClone.remove();
            player.sendMessage("&aYour previous clone was removed!");
        }

        final ShadowCloneNPC shadowClone = new ShadowCloneNPC(this, player);
        final WitherData witherData = getWither(player);
        final int duration = getDuration();

        // Only explode if not using ultimate
        if (witherData != null) {
            final Wither wither = witherData.wither; // Hide the wither too
            shadowClone.ultimate = true;

            Reflect.hideEntity(wither);
            GameTask.runLater(() -> {
                if (wither.isDead()) {
                    return;
                }

                Reflect.showEntity(wither);
            }, duration);
        }
        else {
            GameTask.runLater(() -> {
                shadowClone.explode(null);
                clones.remove(player);
            }, duration);
        }

        clones.put(player, shadowClone);
        return Response.OK;
    }

    public void removeClone(@Nonnull ShadowCloneNPC clone) {
        clone.remove(0);
        clones.remove(clone.player);
    }
}
