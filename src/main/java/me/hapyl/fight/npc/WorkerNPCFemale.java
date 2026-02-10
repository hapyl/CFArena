package me.hapyl.fight.npc;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.appearance.AppearanceHumanoid;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WorkerNPCFemale extends PersistentNPC {
    public WorkerNPCFemale(@Nonnull Key key) {
        super(
                key, -11.5d, 63.0d, -9.5d, Component.text("Worker Emma", Color.DEFAULT), Skin.of(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY1MzI0OTkxOTgyOCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E2NzEwNmI1NGFmYjJhNDBjNjU2NWEwYWE2MGMxNTVlMTRiY2Y1ZGQyNGI3NWU3MjgwMjc3MDEzYTAzNDg2MiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                        "HOpgMHD97rVcpAZOk6xe1y2mVI7Mw5/tls0lnjgerf4vGSucTzbyXH+8M6z4dSC5eN3+EBxWRu2AUCRjnH4p9OEf/69/2rvommvnWXpZtJwDAb7PMCimDdNLbnKtILpdiA44Ohb6i0ggJo36pQLeewxHOo4O7a1aPRhq0RFunoPCjaiINppMEmJ9L89ojp9qADQwdlno4L0827l+7ROVpNYSypP/tIab/9ft9TnqR0mTQ4ttsyhAqRSttjELpnPBqGccqD8A3GKrAqpEn0MgA7LuCq7WFiIsRKaf5EQDMytAenUjSGmidv9MtYNH4giQrpGj5UhDm+ObD97V7WjhUTgvOejZxh23bxugwNcBMy5+huFFSzUtn64eNxTvCU2mAfTRI2mn0jHRT19qKeW4gYh3QjifnlIT+6r/parq0l7PUkzFkrT1olC5z6kI5dt3UNIAma2bB8pVZMg7YEbjXzJgOnxsFN4/JyAmf2Ep12Q3dmtDjUlZlvIpfqIPU7hyUVw9wghII91jfs8YRosxKgnuwOiPrFgxfTvwSuT8gdPguRPaqSzsPBXGv2mS0w6xIQwHo/b0LbOoEQTMDh9sgttVHccNQ/5PgO/oHFiIgv9EYJ02Pjg+LhuHXwvXQ0fhCv13H0liNRGT8dPNfDjvm2A1p6oIMXejcBKXcBDGkVg="
                )
        );
        
        getProperties().setLookAtClosePlayerDistance(0);
        
        getAppearance(AppearanceHumanoid.class).setEquipment(Equipment.builder()
                                                                      .helmet(Material.GOLDEN_HELMET)
                                                                      .build()
        );
        
        sound = new PersistentNPCSound(1.25f);
    }
    
    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        sendMessage(
                player,
                ComponentUtils.random(
                        Component.text("This looks fine..."),
                        Component.text("This should hold it!"),
                        Component.text("Looks sturdy!")
                )
        );
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (tick % 60 == 0) {
            final float newPitch = BukkitUtils.RANDOM.nextFloat(-45f, 10f);
            
            setHeadRotation(-135f, newPitch);
        }
    }
}
