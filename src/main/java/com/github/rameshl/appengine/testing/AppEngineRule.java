package com.github.rameshl.appengine.testing;

import com.google.appengine.tools.development.Clock;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

import com.github.rameshl.appengine.testing.model.GaeTestConfigType;
import com.github.rameshl.appengine.testing.model.UserInfo;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.google.common.base.Charsets.UTF_8;

/**
 * JUnit Rule for managing the App Engine testing environment.
 *
 * <p>Generally you'll want to configure the environment using only the services you need (because
 * each service is expensive to create).
 *
 * <p>This rule also resets global Objectify for the current thread.
 *
 * @see org.junit.rules.ExternalResource
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppEngineRule extends ExternalResource {

    private final Map<GaeTestConfigType, LocalServiceTestConfig> configs;
    /**
     * A temporary folder for temp files.
     */
    private final TemporaryFolder temporaryFolder = new TemporaryFolder();
    private LocalServiceTestHelper helper;
    private UserInfo userInfo;
    private Clock clock;
    private String generatedDirPath;
    /**
     * The Objectify Session.
     */
    private Closeable session;

    /**
     * Instantiates a new App engine rule.
     *
     * @param configs the configs
     */
    public AppEngineRule(Map<GaeTestConfigType, LocalServiceTestConfig> configs) {
        this.configs = configs;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return RuleChain.outerRule(temporaryFolder)
                .around(AppEngineRule.super::apply)
                .apply(base, description);
    }

    @Override
    protected void before() {

        this.helper = new LocalServiceTestHelper(configs.values().toArray(new LocalServiceTestConfig[]{}));

        if (this.userInfo != null) {
            helper.setEnvIsLoggedIn(userInfo.isLoggedIn())
                    .setEnvIsAdmin(userInfo.isAdmin())
                    .setEnvAuthDomain(userInfo.getAuthDomain())
                    .setEnvEmail(userInfo.getEmail())
                    // This envAttributes thing is the only way to set userId.
                    // see https://code.google.com/p/googleappengine/issues/detail?id=3579
                    .setEnvAttributes(ImmutableMap.of("com.google.appengine.api.users.UserService.user_id_key", userInfo.getUserId()));
        }

        this.helper.setClock(clock);

        this.helper.setUp();

        if (configs.containsKey(GaeTestConfigType.Datastore)) {

            if (generatedDirPath == null)
                generatedDirPath = temporaryFolder.getRoot().getAbsolutePath();

            System.setProperty("appengine.generated.dir", generatedDirPath);

            this.session = ObjectifyService.begin();
        }
    }

    @Override
    protected void after() {
        this.helper.tearDown();
        this.helper = null;

        if (this.session != null) {
            this.session.close();
            this.session = null;
        }
    }

    /**
     * Register objectify entities.
     *
     * @param classes the classes
     */
    public void registerEntity(Class<?>... classes) {

        if (!configs.containsKey(GaeTestConfigType.Datastore))
            throw new IllegalArgumentException("datastore service not initialized");

        ObjectifyFactory factory = ObjectifyService.factory();
        for (Class<?> clazz : classes) {
            factory.register(clazz);
        }
    }

    /**
     * Gets generated auto indexes.
     *
     * @return the generated auto indexes
     */
    public File getGeneratedAutoIndexes() {
        File file = new File(generatedDirPath, "datastore-indexes-auto.xml");
        return file.exists() ? file : null;
    }

    public void setupLogging(Class<?> context, String fileName) throws IOException {

        URL url = context == null ? Resources.getResource(fileName) : Resources.getResource(context, "logging.properties");
        String logging = Resources.toString(url, UTF_8);

        LogManager.getLogManager()
                .readConfiguration(new ByteArrayInputStream(logging.getBytes(UTF_8)));
    }

    /**
     * The type Builder.
     */
    public static class Builder {

        private Map<GaeTestConfigType, LocalServiceTestConfig> configs = new HashMap<>();

        private UserInfo userInfo;

        private Clock clock;

        private String generatedDirPath;

        /**
         * With all builder.
         *
         * @return the builder
         */
        public Builder withAll() {
            return withTypes(GaeTestConfigType.values());
        }

        /**
         * With types builder.
         *
         * @param types the types
         * @return the builder
         */
        public Builder withTypes(GaeTestConfigType... types) {

            for (GaeTestConfigType type : types) {
                withType(type);
            }

            return this;
        }

        /**
         * With type builder.
         *
         * @param type the type
         * @return the builder
         */
        public Builder withType(GaeTestConfigType type) {
            configs.put(type, type.getConfig());
            return this;
        }

        /**
         * With datastore builder.
         *
         * @return the builder
         */
        public Builder withDatastore() {
            return withTypes(GaeTestConfigType.Datastore);
        }

        /**
         * With datastore builder.
         *
         * @param config the config
         * @return the builder
         */
        public Builder withDatastore(LocalDatastoreServiceTestConfig config) {
            configs.put(GaeTestConfigType.Datastore, config);
            return this;
        }

        /**
         * With queue builder.
         *
         * @return the builder
         */
        public Builder withQueue() {
            return withTypes(GaeTestConfigType.Queue);
        }

        /**
         * With queue builder.
         *
         * @param queueXmlPath the queue xml path
         * @return the builder
         */
        public Builder withQueue(String queueXmlPath) {
            LocalTaskQueueTestConfig config = GaeTestConfigType.queueConfig();
            config.setQueueXmlPath(queueXmlPath);

            return withQueue(config);
        }

        /**
         * With queue builder.
         *
         * @param config the config
         * @return the builder
         */
        public Builder withQueue(LocalTaskQueueTestConfig config) {
            configs.put(GaeTestConfigType.Queue, config);
            return this;
        }

        /**
         * With memcache builder.
         *
         * @return the builder
         */
        public Builder withMemcache() {
            return withTypes(GaeTestConfigType.Memcache);
        }

        public Builder withMemcache(LocalMemcacheServiceTestConfig config) {
            configs.put(GaeTestConfigType.Memcache, config);
            return this;
        }

        /**
         * With url fetcher builder.
         *
         * @return the builder
         */
        public Builder withUrlFetcher() {
            return withTypes(GaeTestConfigType.UrlFetch);
        }

        /**
         * With user service builder.
         *
         * @param userInfo the user info
         * @return the builder
         */
        public Builder withUserService(UserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public Builder withClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder withGeneratedDirPath(String dirPath) {
            this.generatedDirPath = dirPath;
            return this;
        }

        /**
         * Build app engine rule.
         *
         * @return the app engine rule
         */
        public AppEngineRule build() {
            AppEngineRule rule = new AppEngineRule(configs);

            rule.setClock(clock);

            rule.setUserInfo(userInfo);

            rule.setGeneratedDirPath(generatedDirPath);

            return rule;
        }
    }
}