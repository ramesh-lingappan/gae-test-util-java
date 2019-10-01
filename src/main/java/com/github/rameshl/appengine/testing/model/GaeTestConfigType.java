package com.github.rameshl.appengine.testing.model;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;

/**
 * Enum Type for appengine test services with default configurations
 */
public enum GaeTestConfigType {

    /**
     * Datastore gae test config type.
     */
    Datastore,
    /**
     * Queue gae test config type.
     */
    Queue,
    /**
     * Memcache gae test config type.
     */
    Memcache,
    /**
     * Url fetch gae test config type.
     */
    UrlFetch;

    /**
     * Datastore config local datastore service test config.
     *
     * @return the local datastore service test config
     */
    public static LocalDatastoreServiceTestConfig datastoreConfig() {
        return new LocalDatastoreServiceTestConfig()
                .setApplyAllHighRepJobPolicy()
                .setNoIndexAutoGen(false);
    }

    /**
     * Memcache config local memcache service test config.
     *
     * @return the local memcache service test config
     */
    public static LocalMemcacheServiceTestConfig memcacheConfig() {
        return new LocalMemcacheServiceTestConfig();
    }

    /**
     * Queue config local task queue test config.
     *
     * @return the local task queue test config
     */
    public static LocalTaskQueueTestConfig queueConfig() {
        return new LocalTaskQueueTestConfig();
    }

    /**
     * Url fetch config local url fetch service test config.
     *
     * @return the local url fetch service test config
     */
    public static LocalURLFetchServiceTestConfig urlFetchConfig() {
        return new LocalURLFetchServiceTestConfig();
    }

    /**
     * All config local service test config [ ].
     *
     * @return the local service test config [ ]
     */
    public static LocalServiceTestConfig[] allConfig() {
        return new LocalServiceTestConfig[]{datastoreConfig(), memcacheConfig(), queueConfig()};
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public LocalServiceTestConfig getConfig() {
        switch (this) {
            case Datastore:
                return datastoreConfig();
            case Memcache:
                return memcacheConfig();
            case Queue:
                return queueConfig();
            case UrlFetch:
                return urlFetchConfig();
        }

        return null;
    }
}
