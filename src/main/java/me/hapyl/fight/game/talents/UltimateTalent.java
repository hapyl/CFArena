package me.hapyl.fight.game.talents;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.translate.Language;
import me.hapyl.fight.translate.TranslateKey;
import me.hapyl.fight.util.displayfield.DisplayFieldData;
import me.hapyl.fight.util.displayfield.DisplayFieldDataProvider;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents an ultimate talent.
 * <div>
 * Note that this is not actual executor for ultimate,
 * the hero class is. This is essentially just a data for the ultimate.
 * </div>
 */
public class UltimateTalent extends Talent implements DisplayFieldDataProvider {

    private final List<DisplayFieldData> dataFields;

    private final Hero hero;
    private final int cost;
    private Sound sound;
    private float pitch;
    private int castDuration;

    public UltimateTalent(Hero hero, String name, int pointCost) {
        this(hero, name, "", pointCost);
    }

    public UltimateTalent(Hero hero, String name, String info, int pointCost) {
        super(name, info, Type.DAMAGE);

        this.hero = hero;
        cost = pointCost;
        sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
        pitch = 2.0f;
        dataFields = Lists.newArrayList();

        setDuration(0);
    }

    @Nonnull
    @Override
    public String getTranslateName(@Nonnull Language language) {
        return language.getFormatted(new TranslateKey(getParentTranslatableKey() + "name"));
    }

    @Nonnull
    @Override
    public String getTranslateDescription(@Nonnull Language language) {
        return language.getFormatted(new TranslateKey(getParentTranslatableKey() + "description"));
    }

    @Nonnull
    @Override
    public String getParentTranslatableKey() {
        return hero.getParentTranslatableKey() + "ultimate.";
    }

    @Override
    public UltimateTalent setType(@Nonnull Type type) {
        super.setType(type);
        return this;
    }

    public UltimateTalent appendDescription(String description, Object... format) {
        addDescription(description, format);
        return this;
    }

    public UltimateTalent appendAttributeDescription(String key, Object value) {
        addAttributeDescription(key, value);
        return this;
    }

    public UltimateTalent setDurationSec(int duration) {
        return setDuration(duration * 20);
    }

    public int getDuration() {
        return super.getDuration() + castDuration; // include cast duration
    }

    public UltimateTalent setDuration(int duration) {
        super.setDuration(duration);
        return this;
    }

    public UltimateTalent setSound(Sound sound, float pitch) {
        setSound(sound);
        setPitch(pitch);
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public UltimateTalent setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public Sound getSound() {
        return sound;
    }

    public UltimateTalent setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public int getCost() {
        return cost;
    }

    public UltimateTalent setItem(Material material) {
        super.setItem(material);
        return this;
    }

    @Override
    public UltimateTalent setItem(Material material, Consumer<ItemBuilder> function) {
        super.setItem(material, function);
        return this;
    }

    @Override
    public UltimateTalent setTexture(String texture64) {
        super.setTexture(texture64);
        return this;
    }

    @Override
    public UltimateTalent setCooldown(int cd) {
        super.setCooldown(cd);
        return this;
    }

    @Override
    public UltimateTalent setCooldownSec(int cd) {
        super.setCooldownSec(cd);
        return this;
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) throws IllegalStateException {
        throw new IllegalStateException("Ultimate not implemented");
    }

    /**
     * Must return true in order for talent to execute. If returns false show a message.
     *
     * @param player - player to test.
     * @see UltimateTalent#predicateMessage()
     * @deprecated use Hero instead
     */
    @Deprecated
    public boolean predicateUltimate(Player player) {
        return true;
    }

    @Deprecated
    public String predicateMessage() {
        return "invalid class call, use 'Hero#useUltimate' instead";
    }

    public UltimateTalent defaultCdFromCost() {
        return setCdFromCost(2);
    }

    public UltimateTalent setCdFromCost(int divide) {
        setCooldown((getCost() / divide) * 20);
        return this;
    }

    @Nonnull
    @Override
    public List<DisplayFieldData> getDisplayFieldData() {
        return dataFields;
    }

    public int getCastDuration() {
        return castDuration;
    }

    public UltimateTalent setCastDuration(int duration) {
        this.castDuration = duration;
        return this;
    }

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Ultimate";
    }
}
