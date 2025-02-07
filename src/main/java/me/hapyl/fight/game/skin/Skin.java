package me.hapyl.fight.game.skin;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.RubyPurchasable;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.skin.trait.SkinTrait;
import me.hapyl.fight.game.skin.trait.SkinTraitType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class Skin implements Described, RubyPurchasable, SkinEffectHandler {

    private final Hero hero;
    private final HeroEquipment equipment;
    private final Map<SkinTraitType<?>, SkinTrait> traits = Maps.newHashMap();

    private Rarity rarity;

    private String name;
    private String description;
    private long price;

    public Skin(@Nonnull Hero hero) {
        this.hero = hero;
        this.equipment = new HeroEquipment();
        this.rarity = Rarity.COMMON;

        this.name = "Unnamed skin.";
        this.description = "No description.";
        this.price = -1;
    }

    @Override
    public long getRubyPrice() {
        return price;
    }

    @Override
    public void setRubyPrice(long price) {
        this.price = price;
    }

    @Nonnull
    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(@Nonnull Rarity rarity) {
        this.rarity = rarity;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    @Nonnull
    public HeroEquipment getEquipment() {
        return equipment;
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }

    public void equip(@Nonnull Player player) {
        equipment.equip(player);
    }

    public void equip(@Nonnull GamePlayer player) {
        equipment.equip(player);
    }

    @Override
    public void onTick(@Nonnull GamePlayer player, int tick) {
        callTrait(SkinTraitType.TICK, trait -> trait.onTick(player, tick));
    }

    @Override
    public void onKill(@Nonnull GamePlayer player, @Nonnull GameEntity victim) {
        callTrait(SkinTraitType.KILL, trait -> trait.onKill(player, victim));
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player, @Nullable GameEntity killer) {
        callTrait(SkinTraitType.DEATH, trait -> trait.onDeath(player, killer));
    }

    @Override
    public void onMove(@Nonnull GamePlayer player, @Nonnull Location to) {
        callTrait(SkinTraitType.MOVE, trait -> trait.onMove(player, to));
    }

    @Override
    public void onStandingStill(@Nonnull GamePlayer player) {
        callTrait(SkinTraitType.STILL, trait -> trait.onStandingStill(player));
    }

    @Override
    public void onWin(@Nonnull GamePlayer player) {
        callTrait(SkinTraitType.WIN, trait -> trait.onWin(player));
    }

    /**
     * Gets a {@link SkinTrait} with the given {@link SkinTraitType}.
     *
     * @param type - Type.
     * @return the skin trait, or null if skin doesn't have a trait with that type.
     */
    @Nullable
    public <T extends SkinTrait> T getTrait(@Nonnull SkinTraitType<T> type) {
        final SkinTrait trait = traits.get(type);

        if (trait == null) {
            return null;
        }

        return type.cast(trait);
    }

    /**
     * Gets a copy of this {@link Skin} {@link SkinTrait}.
     *
     * @return a copy of this skin's traits.
     */
    @Nonnull
    public Map<SkinTraitType<?>, SkinTrait> getTraits() {
        return Maps.newHashMap(traits);
    }

    protected <T extends SkinTrait> void setTrait(@Nonnull SkinTraitType<T> type, @Nonnull T trait) {
        traits.put(type, trait);
    }

    private <T extends SkinTrait> void callTrait(SkinTraitType<T> type, Consumer<T> consumer) {
        final T trait = getTrait(type);

        if (trait != null) {
            consumer.accept(trait);
        }
    }


}
