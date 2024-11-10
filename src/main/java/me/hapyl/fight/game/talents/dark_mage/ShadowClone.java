package me.hapyl.fight.game.talents.dark_mage;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.dark_mage.SpellButton;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShadowClone extends DarkMageTalent {

    protected final PlayerMap<ShadowCloneNPC> clones = PlayerMap.newMap();

    public ShadowClone(@Nonnull Key key) {
        super(key, "Shadow Clone", """
                Create a &breflection&7 of &nyourself&7 at your &ncurrent&7 &nlocation&7 for a maximum of {duration}.
                
                The clone &ninherits&7 your &cHealth&7, &bEnergy&7, and &acooldowns&7.
                
                Cast this spell again to &bteleport&7 to the &8clone&7, &arestoring&7 yourself to the &3reflected&7 version of yourself.
                
                &cThe clone is very fragile!
                
                &8;;Using your ultimate will remove the clone.
                """
        );

        setType(TalentType.SUPPORT);
        setItem(Material.NETHERITE_SCRAP);
        setDurationSec(15);
        setCooldownSec(20);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        clones.removeAnd(player, ShadowCloneNPC::remove);
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {
        clones.forEachAndClear(ShadowCloneNPC::remove);
    }

    @Override
    public Response executeSpell(@Nonnull GamePlayer player) {
        final ShadowCloneNPC previousClone = clones.remove(player);

        if (previousClone != null) {
            previousClone.teleport();
            return Response.OK;
        }

        clones.put(player, new ShadowCloneNPC(this, player));

        // Fx
        startCd(player, 5);

        return Response.AWAIT;
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

    public void removeClone(GamePlayer player) {
        final ShadowCloneNPC clone = clones.remove(player);

        if (clone != null) {
            clone.remove();
        }
    }

}
