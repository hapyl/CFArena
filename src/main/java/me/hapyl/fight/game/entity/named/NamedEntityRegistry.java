package me.hapyl.fight.game.entity.named;

import me.hapyl.eterna.module.registry.SimpleRegistry;

public class NamedEntityRegistry extends SimpleRegistry<NamedEntityType> {

    public final Abobo ABOBO;
    public final AngryPiglin ANGRY_PIGLIN;
    public final Bladesoul BLADESOUL;
    public final Genie GENIE;
    public final VoidAbomination VOID_ABOMINATION;
    public final Voidgloom VOIDGLOOM;

    // Boss

    public NamedEntityRegistry() {
        ABOBO = register("abobo", Abobo::new);
        ANGRY_PIGLIN = register("angry_piglin", AngryPiglin::new);
        BLADESOUL = register("bladesoul", Bladesoul::new);
        GENIE = register("genie", Genie::new);
        VOID_ABOMINATION = register("void_abomination", VoidAbomination::new);
        VOIDGLOOM = register("voidgloom", Voidgloom::new);
    }


}
