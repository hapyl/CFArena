package me.hapyl.fight.game.collectible.relic;

public enum Type {

    AMETHYST("413600e6c4737192c3121f861487616b134423796d23bc396df521baddb31564"),
    EMERALD("228dd3d9b81cc9f96a4c11fe13b079494f27fc5b933c4f0833f6564472ee1609"),
    SAPPHIRE("cabb51f59481132545b50e475e766239c79c624e9b96ab3a0acb2af301d96c79"),
    ROSE_QUARTZ("75362f90ff29ccd40f4235a05dd91df091cf43f3a2f71414dfbbca6e5d3634d9"),
    DIAMOND("d6794a75e877fc48bb772d59ebf6e076d1b0a54e9f2bd317f1a1224997deeb37");

    private final String texture;

    Type(String texture) {
        this.texture = texture;
    }

    public String getTexture() {
        return texture;
    }

    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }

}
