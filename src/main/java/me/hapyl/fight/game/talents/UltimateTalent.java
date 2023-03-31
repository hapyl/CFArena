package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.Response;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Represents an ultimate talent.
 * <div>
 * Note that this is not actual executor for ultimate,
 * the hero class is. This is essentially just a data for the ultimate.
 * </div>
 */
public class UltimateTalent extends Talent {

    private final int cost;
    private Sound sound;
    private float pitch;

    public UltimateTalent(String name, String info, int pointCost) {
        super(name, info, Type.ULTIMATE);
        cost = pointCost;
        sound = Sound.ENTITY_ENDER_DRAGON_GROWL;
        pitch = 2.0f;

        setDuration(0);
    }

    public UltimateTalent setDuration(int duration) {
        super.setDuration(duration);
        return this;
    }

    public UltimateTalent setDurationSec(int duration) {
        return setDuration(duration * 20);
    }

    public int getDuration() {
        return super.getDuration();
    }

    public UltimateTalent setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public UltimateTalent setSound(Sound sound, float pitch) {
        setSound(sound);
        setPitch(pitch);
        return this;
    }

    public UltimateTalent setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public int getCost() {
        return cost;
    }

    public UltimateTalent setItem(Material material) {
        super.setItem(material);
        return this;
    }

    @Override
    public UltimateTalent setItem(String headTexture) {
        super.setItem(headTexture);
        return this;
    }

    @Override
    public UltimateTalent setTexture(String texture64) {
        super.setTexture(texture64);
        return this;
    }

    @Override
    public UltimateTalent setCd(int cd) {
        super.setCd(cd);
        return this;
    }

    @Override
    public UltimateTalent setCdSec(int cd) {
        super.setCdSec(cd);
        return this;
    }

    @Deprecated
    public void useUltimate(Player player) {
    }

    @Deprecated
    @Override
    public final Response execute(Player player) {
        throw new IllegalStateException("use Hero#useUltimate");
    }

    /**
     * Must return true in order for talent to execute. If returns false shows a message.
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

    public UltimateTalent setCdFromDuration(int divide) {
        setCd(getDuration() / divide);
        return this;
    }
}
