package me.hapyl.fight.database;

import com.mongodb.client.MongoCollection;

import javax.annotation.Nonnull;

public interface MongoCallback<T> {

    /**
     * Called async.
     *
     * @param collection collection to use.
     */
    void async(@Nonnull MongoCollection<T> collection);

    /**
     * Called sync after {@link #async(MongoCollection)} is done.
     *
     * @param collection collection to use.
     */
    void then(@Nonnull MongoCollection<T> collection);

    /**
     * Called when an error occurs.
     *
     * @param collection collection to use.
     * @param error      error that occurred.
     * @param <E>        type of error.
     */
    <E extends RuntimeException> void error(@Nonnull MongoCollection<T> collection, @Nonnull E error) throws RuntimeException;

}
