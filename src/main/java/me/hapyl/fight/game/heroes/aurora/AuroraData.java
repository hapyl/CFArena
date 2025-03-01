package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.aurora.AuroraArrowTalent;
import me.hapyl.fight.util.ConcurrentConsumerHashMap;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuroraData extends PlayerData {

    protected AuroraArrowTalent arrow;
    protected int arrowCount;
    protected LivingGameEntity target;
    protected CelestialBond bond;

    protected final ConcurrentConsumerHashMap<LivingGameEntity, EtherealSpirit> buffMap;

    public AuroraData(GamePlayer player) {
        super(player);

        this.buffMap = new ConcurrentConsumerHashMap<>();
    }

    public boolean hasBond() {
        return bond != null;
    }

    @Override
    public void remove() {
        buffMap.values().forEach(EtherealSpirit::remove);
        buffMap.clear();
    }

    public void setArrow(@Nullable AuroraArrowTalent arrow) {
        // Start cooldown for the previous arrow
        if (this.arrow != null) {
            this.arrow.startCd(player);

            //player.sendSubtitle("&c- %s".formatted(this.arrow.getColor() + this.arrow.getName()), 5, 15, 5);
            player.playSound(Sound.ENTITY_HORSE_SADDLE, 1.25f);
        }

        if (arrow == null) {
            this.arrow = null;
            this.arrowCount = 0;
            return;
        }

        this.arrow = arrow;
        this.arrowCount = arrow.getMaxArrows();

        player.sendSubtitle("&a+ %s".formatted(arrow.getColor() + arrow.getName()), 5, 15, 5);
        player.playSound(Sound.ENTITY_HORSE_SADDLE, 2.0f);
    }

    @Override
    public void remove(@Nonnull LivingGameEntity entity) {
        buffMap.remove(entity, EtherealSpirit::remove);
    }

    public void buff(@Nonnull LivingGameEntity entity) {
        buffMap.computeIfAbsent(entity, fn -> new EtherealSpirit(player, entity)).addStack();
    }

    public void breakBond(@Nonnull String reason) {
        if (bond == null) {
            return;
        }

        final Aurora.AuroraUltimate ultimate = HeroRegistry.AURORA.getUltimate();
        ultimate.startCd(player, ultimate.cooldown);

        final LivingGameEntity entity = bond.getEntity();

        bond.cancel();
        bond = null;

        player.setUsingUltimate(false);

        // Fx
        entity.sendMessage("&b₰ The bond with %s is broken!".formatted(player.getName()));

        player.sendMessage("&b₰ &cThe bond is broken! &4" + reason);
        player.playWorldSound(Sound.ENTITY_ALLAY_DEATH, 0.75f);

        // In case we're in the air (we're 99% are) add parachute effect (lol funny name haha)
        player.addEffect(EffectType.PARACHUTE, Constants.INFINITE_DURATION);
    }

}
