# AppEngine Local Testing Helper 

A small test utility library to help with appengine local unit testing. 

Checkou the medium article [AppEngine unit testing made easy with JUnit Rules](https://medium.com/@rameshlingappa/appengine-unit-testing-made-easy-with-junit-rules-97c2127a161a)



### Setup
`AppEngineRule` class helps to configure the necessary appengine local test stubs for unit testing. You can able to quickly setup using the default configurations or provide custom configurations

```java
@Rule 
pubilc final AppEngineRule appengineRule = AppEngineRule.builder()
             .withAll() // all will setup all the appengine services like Datastore, Queue, Memcache etc
            .build();


@Rule 
pubilc final AppEngineRule appengineRule = AppEngineRule.builder()
            .withTypes(Datastore, Memcache, Queue) //or configure using selective service 
            .build();  

// or go for complex one 
@Rule
public final AppEngineRule appEngineRule = AppEngineRule.builder()

        // datastore & generate auto indexes xml file 
        .withDatastore(datastoreConfig().setNoIndexAutoGen(false))

        // with memecache 
        .withMemcache()

        // queue with specific queue.xml file 
        .withQueue("{PROJECT_DIR}/src/main/webapp/WEB-INF/queue.xml")

        // UserService with an admin user loggedIn
        .withUserService(UserInfo.createAdmin("admin@testing.com", "admin_123"))

        // with url fetch service
        .withUrlFetcher()

        .build();
```

## Installation 

Checkout the releases for binaries [Releases](/releases)

### Usage


### Datastore Setup

```java
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
```

