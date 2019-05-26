package me.schlaubi.mongoutils;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import me.schlaubi.mongoutils.internal.MongoUtilsImpl;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Convention;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Builder for {@link MongoUtils}
 *
 * @author Michael Rittmeister
 * @since 1.0
 */
@SuppressWarnings("unused")
public class MongoUtilsBuilder {

    private final MongoClient client;
    private CodecRegistry codecRegistry = MongoClientSettings.getDefaultCodecRegistry();
    private List<Convention> pojoConventions = null;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Constructs a new {@link MongoUtilsBuilder}.
     *
     * @param client the {@link MongoClient} you used to connect to your instance
     */
    public MongoUtilsBuilder(@NotNull MongoClient client) {
        this.client = client;
    }

    private void checkConventions() {
        if (pojoConventions == null) {
            setPojoConventions(new ArrayList<>());
        }
    }

    /**
     * Returns the MongoDB which is currently selected.
     *
     * @return the {@link MongoClient}
     */
    @NotNull
    public MongoClient getClient() {
        return client;
    }

    /**
     * Returns the CodecRegistry which gets passed into every {@link com.mongodb.client.MongoCollection} used by
     * {@link MongoUtils} using {@link CodecRegistries#fromRegistries(CodecRegistry...)}.
     *
     * @return the {@link CodecRegistry}
     */
    @NotNull
    public CodecRegistry getCodecRegistry() {
        return codecRegistry;
    }

    /**
     * Sets the CodecRegistry which gets passed into every {@link com.mongodb.client.MongoCollection} used by
     * {@link MongoUtils} using {@link CodecRegistries#fromRegistries(CodecRegistry...)}.
     *
     * @param codecRegistry the {@link CodecRegistry}
     *
     * @return the {@link MongoUtilsBuilder}
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public MongoUtilsBuilder setCodecRegistry(@NotNull CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
        return this;
    }

    /**
     * Adds another {@link CodecRegistry} to {@link MongoUtilsBuilder#codecRegistry}.
     *
     * @param codecRegistry the {@link CodecRegistry} to add
     * @return the {@link MongoUtilsBuilder}
     * @see CodecRegistries#fromRegistries(CodecRegistry...)
     */
    @NotNull
    public MongoUtilsBuilder addCodecRegistry(@NotNull CodecRegistry codecRegistry) {
        return setCodecRegistry(CodecRegistries.fromRegistries(this.codecRegistry, codecRegistry));
    }

    /**
     * Adds another {@link CodecProvider} to {@link MongoUtilsBuilder#codecRegistry}.
     *
     * @param codecProvider the {@link CodecProvider} to add
     * @return the {@link MongoUtilsBuilder}
     * @see CodecRegistries#fromProviders(CodecProvider...)
     */
    @NotNull
    public MongoUtilsBuilder addCodecProvider(@NotNull CodecProvider codecProvider) {
        return setCodecRegistry(CodecRegistries.fromRegistries(this.codecRegistry, CodecRegistries.fromProviders(codecProvider)));
    }

    /**
     * Returns the {@link Convention}s that are getting passed to every {@link com.mongodb.client.MongoCollection} used
     * by {@link MongoUtils}.
     *
     * @return a {@link List} containing all the {@link Convention}s
     */
    @Nullable
    public List<Convention> getPojoConventions() {
        return pojoConventions;
    }

    /**
     * Sets the {@link Convention}s that are getting passed to every {@link com.mongodb.client.MongoCollection} used
     * by {@link MongoUtils}.
     *
     * @param pojoConventions A {@link List} of {@link Convention}s
     * @return the {@link MongoUtilsBuilder}
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    @NotNull
    public MongoUtilsBuilder setPojoConventions(@Nullable List<Convention> pojoConventions) {
        this.pojoConventions = pojoConventions;
        return this;
    }

    /**
     * Adds a {@link Convention} to {@link MongoUtilsBuilder#pojoConventions}.
     *
     * @param pojoConventions a {@link Collection} of {@link Convention}s to add
     * @return the {@link MongoUtilsBuilder}
     */
    @NotNull
    public MongoUtilsBuilder addPojoConventions(@NotNull Collection<Convention> pojoConventions) {
        checkConventions();
        this.pojoConventions.addAll(pojoConventions);
        return this;
    }

    /**
     * Adds a {@link Convention} to {@link MongoUtilsBuilder#pojoConventions}.
     *
     * @param pojoConventions the {@link Convention}(s) to add
     * @return the {@link MongoUtilsBuilder}
     */
    @NotNull
    public MongoUtilsBuilder addPojoConventions(@NotNull Convention... pojoConventions) {
        checkConventions();
        Collections.addAll(this.pojoConventions, pojoConventions);
        return this;
    }

    /**
     * Returns the {@link ExecutorService} used by all MongoDB actions.
     *
     * @return the {@link ExecutorService}
     */
    @NotNull
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Sets the {@link ExecutorService} used by all MongoDB actions.
     *
     * @param executorService the {@link ExecutorService}
     * @return the {@link MongoUtilsBuilder}
     */
    @NotNull
    public MongoUtilsBuilder setExecutorService(@NotNull ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    /**
     * Builds a new {@link MongoUtils} instance.
     *
     * @return the {@link MongoUtils} instance
     */
    @NotNull
    public MongoUtils build() {
        return new MongoUtilsImpl(
                client,
                codecRegistry,
                pojoConventions,
                executorService
        );
    }
}
