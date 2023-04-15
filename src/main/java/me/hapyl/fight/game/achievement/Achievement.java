package me.hapyl.fight.game.achievement;

/**
 * Base achievement class.
 */
public class Achievement {

    private final String name;
    private final String description;

    private Category category;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.category = Category.GAMEPLAY;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
