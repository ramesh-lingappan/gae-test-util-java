package com.github.rameshl.appengine.testing;

import com.github.rameshl.appengine.testing.helper.TestEntity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.rameshl.appengine.testing.model.GaeTestConfigType.datastoreConfig;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertNotNull;

public class DatastoreTest {

    @Rule
    public final AppEngineRule rule = AppEngineRule.builder()
            .withDatastore(datastoreConfig().setNoIndexAutoGen(false))
            .build();

    @Before
    public void setUp() {
        ofy().factory().register(TestEntity.class);
    }

    @Test
    public void testGet() {

        TestEntity testEntity = new TestEntity();
        ofy().save().entity(testEntity).now();

        System.out.println(testEntity);

        TestEntity entity = ofy().load().type(TestEntity.class).id(testEntity.getId()).now();
        assertNotNull(entity);
    }
}