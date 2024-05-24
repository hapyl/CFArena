package me.hapyl.fight.game.event;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.EnumClass;
import me.hapyl.spigotutils.module.player.PlayerSkin;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import java.util.stream.Collectors;

public final class ServerEvents implements EnumClass {

    /**
     * April Fools week, from <CODE>Apr 1st</CODE> to <CODE>Apr 7th</CODE> (inclusive).
     */
    public static final ServerEvent APRIL_FOOLS;

    private static final Set<ServerEvent> values;

    static {
        values = Sets.newHashSet();

        APRIL_FOOLS = new0(new ServerEvent("April Fools", "We are all clowns, aren't we.") {

            private final PlayerSkin theSkin = new PlayerSkin(
                    "ewogICJ0aW1lc3RhbXAiIDogMTcwODAwNTUyOTY1NywKICAicHJvZmlsZUlkIiA6ICJiM2E3NjExNGVmMzI0ZjYyYWM4NDRiOWJmNTY1NGFiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNcmd1eW1hbnBlcnNvbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ZjIwZDQ2Y2FiZmMxNWNlMDRiYzUyMzY5ZDI1NmJiOTc1OWY3MGM4ZjEzOWFhYzQ4ZGQ3NzZiYjZhYjI5Y2UyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                    "Q8R8yWm3pPwuSs8RBqDjYzl6KThb8hDmv7KHRQmFLM2O7gDntPRnD7kPVmB9oPtHohl0Zjfqyfkv+pCswy3ipfVF+uFIdNVOLRUEiWh4bJ5wZ81UZEQ0jqmnteaEoD0oEclpUc4L18oqm1v/4Z7M0DemYdQlM9DXn0edPkrXXE/YH9m/49xS3w8glKtZskaabDcINQDtCUju7w/lwUtmVOEOPSSKmAyopSVKyTGt2LtqKRRbK3EJcM0Mg/iGVW+4y+fa5M8rsEsE11c9Fl84yuJXsejOmz73HBJ1eja6kmSLWOYEGD8hF9brnWK3rg5kv1x2fddTQsrWVe/xtYfwC0nS+oDOtKESN/wF2qkU/Ew7cKpz83cOmeZXoVUonk8DDReCkLUtkeJmbMONxyWO08pmMulQ/+yjTZb9yuNuhQrEv/a7n4s1QpBRyhzppwZpLDYyYeWHJWGrTsY3A58632UWrvVrE2IOrS9OBOuyAujiCUteOyKIhCzuvEX3Ctn6K81Jp5XkuXji4jQyYxYUADZpJqbCRgRHnPYKUeGPbyo0oajNT3aSrrl+YinbDmQ3CPi6Ey4M7QMupOk0PPxhNSTu6qfaZCsGD5ja+/Pkl7K+RovRq7IX8qgeYkrD0PfTSjquaYEuJWxaMNRkKTI7GzJlIqThdLrF25W/0uZz+9o="
            );

            @Override
            public boolean isActive() {
                final LocalDate date = LocalDate.now();

                final Month month = date.getMonth();
                final int dayOfMonth = date.getDayOfMonth();

                return month == Month.APRIL && dayOfMonth <= 7;
            }

            @Override
            public void onJoin(@Nonnull PlayerProfile profile) {
                profile.setOriginalSkin(theSkin);
                profile.applyOriginalSkin();

                Achievements.APRIL_FOOLS.complete(profile.getPlayer());
            }
        });

    }

    @Nonnull
    public static Set<ServerEvent> getActiveEvents() {
        return values
                .stream()
                .filter(ServerEvent::isActive)
                .collect(Collectors.toSet());
    }

    private static ServerEvent new0(ServerEvent event) {
        values.add(event);

        return event;
    }

}
