package me.hapyl.fight.npc;

import me.hapyl.eterna.module.registry.SimpleRegistry;

public class NPCRegistry extends SimpleRegistry<PersistentNPC> {
    
    public final TheEyeNPC THE_EYE;
    public final HypixelNPC HYPIXEL;
    public final UndeadWatcherNPC UNDEAD_WATCHER;
    public final BloodfiendNPC BLOODFIEND;
    public final MuseumManagerNPC MUSEUM_MANAGER;
    public final StoreOwnerNPC STORE_OWNER;

    public NPCRegistry() {
        THE_EYE = register("the_eye", TheEyeNPC::new);
        HYPIXEL = register("hypixel", HypixelNPC::new);
        UNDEAD_WATCHER = register("undead_watcher", UndeadWatcherNPC::new);
        BLOODFIEND = register("bloodfiend", BloodfiendNPC::new);
        MUSEUM_MANAGER = register("museum_manager", MuseumManagerNPC::new);
        STORE_OWNER = register("store_owner", StoreOwnerNPC::new);
    }
    
}
