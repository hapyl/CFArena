package me.hapyl.fight.game.attribute;

public enum AttributeType {

    HEALTH("Health", "Maximum health hero has.", 100.0d),
    ATTACK("Attack", "The more attack you have, the more damage you will deal.", 1.0d),
    DEFENSE("Defense", "The more defense you have, the less damage you will take.", 1.0d),
    SPEED("Speed", "Movement speed of the hero.", 0.2d),
    CRIT_CHANCE("Critical Chance", "Chance for attack to deal critical hit.", 0.1d),
    CRIT_DAMAGE("Critical Damage", "The damage increase modifier for critical hit.", 0.5d);

    private final String name;
    private final String description;
    private final double defaultValue;

    AttributeType(String name, String description, double defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double get(Attributes attributes) {
        return attributes.get(this);
    }

    // TODO (hapyl): 022, May 22, 2023: Maybe make cool formatter or maybe fuck you
    public String getFormatted(Attributes attributes) {
        final double value = get(attributes);

        return String.valueOf(value * 100);
    }
}

