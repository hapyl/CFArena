package me.hapyl.fight.github;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Contributor {

    private final String login;
    private final String name;

    public Contributor(@Nonnull String login, @Nullable String name) {
        this.login = login;
        this.name = name != null ? name : login;
    }

    @Nonnull
    public String getLogin() {
        return login;
    }

    @Nonnull
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
