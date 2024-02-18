package me.hapyl.fight.github;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;

public class Contributor {

    @SerializedName("login")
    public final String login;

    @SerializedName("contributions")
    public final int contributions;

    private String name;

    public Contributor(String login, int contributions) {
        this.login = login;
        this.contributions = contributions;
        this.name = login;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return login + "(" + name + ") x " + contributions;
    }
}
