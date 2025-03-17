package me.hapyl.fight.game.entity.commission;

import me.hapyl.eterna.module.registry.SimpleRegistry;
import me.hapyl.fight.game.entity.commission.crypt.LeechBoss;
import me.hapyl.fight.game.entity.commission.crypt.MegaGolem;
import me.hapyl.fight.game.entity.commission.crypt.RookieZombie;
import me.hapyl.fight.game.entity.commission.hypixel.Bladesoul;
import me.hapyl.fight.game.entity.commission.hypixel.Genie;
import me.hapyl.fight.game.entity.commission.hypixel.VoidAbomination;
import me.hapyl.fight.game.entity.commission.hypixel.Voidgloom;

public class EntityRegistry extends SimpleRegistry<CommissionEntityType> {

    // [!] Keep entities sorted by level [!] //

    // The Crypt
    public final RookieZombie ROOKIE_ZOMBIE;
    public final MegaGolem MEGA_GOLEM;
    public final LeechBoss LEECH_BOSS;

    // Other
    public final Bladesoul BLADESOUL;
    public final Genie GENIE;
    public final VoidAbomination VOID_ABOMINATION;
    public final Voidgloom VOIDGLOOM;
    public final MinosInquisitor MINOS_INQUISITOR;

    public EntityRegistry() {
        VOID_ABOMINATION = register("void_abomination", VoidAbomination::new);
        VOIDGLOOM = register("voidgloom", Voidgloom::new);

        // Commission Entities
        ROOKIE_ZOMBIE = register("rookie_zombie", RookieZombie::new);
        MEGA_GOLEM = register("mega_golem", MegaGolem::new);
        BLADESOUL = register("bladesoul", Bladesoul::new);
        LEECH_BOSS = register("leech_boss", LeechBoss::new);
        GENIE = register("genie", Genie::new);
        MINOS_INQUISITOR = register("minos_inquisitor", MinosInquisitor::new);
    }


}
