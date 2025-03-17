package me.hapyl.fight.game.reward;

import javax.annotation.Nonnull;

public enum DropChance {

    GUARANTEED("&aGuaranteed", "21865de1a77da86f8423af4ba44101397429d200be42045651212a615cf754f2", 1.0d),
    COMMON("&a&lCOMMON", "21865de1a77da86f8423af4ba44101397429d200be42045651212a615cf754f2", 0.99d),
    UNCOMMON("&2&lUNCOMMON", "d16d25953d4b8e6334926e206ac323e77733ed70401fd1a8eef7e2390e40db3d", 0.55d),
    RARE("&9&lRARE", "64095ac2f42b9b649d00de7d761b56396d4e4dac9382ac02ed15e6ddf901aa4b", 0.30d),
    VERY_RARE("&b&lVERY RARE", "debf6be82e823e582ff8f45e01cf51751734d9a776cff514e88ea26d4973bbf0", 0.10d),
    RNGESUS("&d&lRNGesus", "eca4130f68c71172be032b2fcb2d0ace7ffb143f375709a7f5a2bc84f6f9fbd3", 0.04d),
    INSANE("&c&lINSANE", "d4675158c0767ee508c52d52426cef3a2c2b29b7e487c92953a3823f561d06af", 0.01d);

    private final String toString;
    private final String texture;
    private final double chance;

    DropChance(String toString, String texture, double chance) {
        this.toString = toString;
        this.texture = texture;
        this.chance = chance;
    }

    @Nonnull
    public String texture() {
        return texture;
    }

    @Nonnull
    public static DropChance of(double chance) {
        for (int i = values().length - 1; i >= 0; i--) {
            final DropChance dropChance = values()[i];

            if (chance <= dropChance.chance) {
                return dropChance;
            }
        }

        return GUARANTEED;
    }

    @Nonnull
    public String format(double chance) {
        return "%s &8(%.2f%%)".formatted(toString, chance * 100d);
    }
}
