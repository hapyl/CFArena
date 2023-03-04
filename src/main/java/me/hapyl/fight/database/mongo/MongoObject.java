package me.hapyl.fight.database.mongo;

public class MongoObject {

    private final String path;

    public MongoObject(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
