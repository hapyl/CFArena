package me.hapyl.fight.game.talents.shaman;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.shaman.resonance.ResonanceType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class TotemTalent extends InputTalent {

    @DisplayField protected final double verticalVelocity = 0.25d;
    @DisplayField protected final double velocity = 0.75d;
    @DisplayField(percentage = true) protected final double chanceToExplode = 0.2d;
    @DisplayField protected final double explodeDamage = 10.0d;
    @DisplayField protected final int interval = 30;
    @DisplayField private final short maxTotems = 3;

    private final PlayerMap<LinkedList<Totem>> playerTotems = PlayerMap.newMap();

    public TotemTalent(@Nonnull Key key) {
        super(key, "Totem");

        setDescription("""
                Equip a &aTotem&7 and prepare to toss it.
                
                After a &aTotem&7 lands, it &aactivates&7 and does one of the following actions every &b{interval}&7 based on the &3Resonance&7.
                &8;;There is a small chance for totem to explode violently.
                """
        );

        leftData.setAction("Resonate Discord");
        leftData.setType(TalentType.DAMAGE);
        leftData.setCooldownSec(6);
        leftData.setDescription(ResonanceType.DAMAGE_AURA.toString());

        rightData.setAction("Resonate Harmony");
        rightData.setType(TalentType.SUPPORT);
        rightData.setCooldownSec(10);
        rightData.setDescription(ResonanceType.HEALING_AURA.toString());

        setItem(Material.SPRUCE_FENCE);
        setDurationSec(20);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        playerTotems.forEachAndClear(list -> {
            list.forEach(Totem::cancel);
            list.clear();
        });

        playerTotems.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final LinkedList<Totem> totems = playerTotems.remove(player);

        if (totems != null) {
            totems.forEach(Totem::cancel);
            totems.clear();
        }
    }

    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        throwTotem(player, ResonanceType.DAMAGE_AURA);

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        throwTotem(player, ResonanceType.HEALING_AURA);

        return Response.OK;
    }

    @Nonnull
    public LinkedList<Totem> getTotems(@Nonnull GamePlayer player) {
        return playerTotems.computeIfAbsent(player, fn -> Lists.newLinkedList());
    }

    private void throwTotem(GamePlayer player, ResonanceType type) {
        final LinkedList<Totem> totems = getTotems(player);

        if (totems.size() >= maxTotems) {
            final Totem last = totems.pollFirst();

            if (last != null) {
                last.cancel();
            }
        }

        totems.add(new Totem(this, player, type.getResonance()));

        // Fx
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DEATH, 0.75f);
    }

}
