package me.hapyl.fight.game.talents.dylan;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.dylan.Dylan;
import me.hapyl.fight.game.heroes.dylan.DylanData;
import me.hapyl.fight.game.heroes.dylan.DylanFamiliar;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SummonWhelp extends Talent {
    
    @DisplayField(percentage = true, suffix = " of D'lan's Max Health") public final double whelpHealth = 0.8;
    @DisplayField(percentage = true) private final double healing = 0.4;
    
    public SummonWhelp(@Nonnull Key key) {
        super(key, "Summon Whelp");
        
        setDescription("""
                       Summons a nether whelp familiar — &3%1$s&7 — to fight alongside you.
                       
                       If &3%1$s&7 is already on the field, &aheal&7 it for &a{healing}&7 of its %2$s.
                       &8&o;;Healing %1$s clears any existing %3$s stacks.
                       """.formatted(Dylan.familiarName, AttributeType.MAX_HEALTH, Named.SCORCH.getName())
        );
        
        setType(TalentType.SUPPORT);
        setMaterial(Material.VEX_SPAWN_EGG);
        
        setCooldownSec(30);
    }
    
    @Nullable
    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Dylan dylan = HeroRegistry.DYLAN;
        final DylanData data = dylan.getPlayerData(player);
        
        // Check for combust
        if (data.familiar != null && data.familiar.selfDestruct() != null) {
            return Response.error("Cannot use this now!");
        }
        
        // Summon whelp
        if (data.familiar == null) {
            data.familiar = new DylanFamiliar(player, this);
            dylan.whelpTalents(player, true);
            return Response.OK;
        }
        
        // Else heal
        data.familiar.entity().healRelativeToMaxHealth(healing, player);
        data.familiar.resetBuff();
        
        // Heal fx
        player.playWorldSound(Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.75f);
        player.playWorldSound(Sound.ENTITY_VEX_AMBIENT, 1.75f);
        
        return Response.OK;
    }
}
