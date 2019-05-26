import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import me.schlaubi.mongoutils.MongoEntity;
import me.schlaubi.mongoutils.MongoUtils;
import me.schlaubi.mongoutils.annotations.Collection;
import me.schlaubi.mongoutils.providers.Accessor;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MongoTest {

    // constants
    private final String ip = "localhost";
    private final int port = 1337;

    // Client
    private MongoClient client;
    private MongodExecutable mongodExecutable;
    private MongoUtils utils;

    @Before
    @SuppressWarnings("unused")
    public void prepare() throws IOException {
        // Launch embedded MongoDB instance
        var starter = MongodStarter.getDefaultInstance();
        var config = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build();
        mongodExecutable = starter.prepare(config);
        mongodExecutable.start();

        // Connect to the server
        client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder -> builder.hosts(List.of(new ServerAddress(ip, port))))
                        .build()
        );

        // Add some default data
        var collection = client.getDatabase("test").getCollection("users");
        var users = List.of(
                new User(1, "Frant", "van Harald", 12, 2000).toDocument(),
                new User(2, "Tobias", "van Harald", 3, 20).toDocument(),
                new User(3, "Newt", "van Harald", 19, 1337).toDocument()
        );
        collection.insertMany(users);
    }

    @Test
    @SuppressWarnings("unused")
    public void test() {

        // Create an instance of MongoUtils
        utils = MongoUtils.builder(client).build();

        // Create an accessor
        Accessor<User, Integer> accessor = utils.createAccessor(User.class);
        // Print all users
        accessor.forEach(System.out::println);

        // Delete user with ID 4 first
        accessor.delete(4);
        // Create new user
        var user = new User(4, "Joachim", "von under the Berg", 20, 1123);
        // Insert him to the db
        accessor.insertAsync(user)
                /// Print saved when done
                .thenRun(() -> System.out.println("SAVED!"))
                // Listen for errors
                .exceptionally(it -> {
                    it.printStackTrace();
                    return null;
                });

        // Find user with id 3
        var userThree = accessor.get(3);
        // Lets give him a bit more brain!
        userThree.iq += 100;
        // Save what we changed
        userThree.update();
    }

    @After
    @SuppressWarnings("unused")
    public void shutdown() {
        if (client != null) {
            client.close();
        }
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
        if (utils != null) {
            utils.close();
        }

    }

    @Collection(database = "test", collection = "users")
    @SuppressWarnings({"unused"})
    public static class User extends MongoEntity<User> {

        private final int userId;
        private String name;
        private String surname;
        private int age;
        private int iq;

        @BsonCreator
        @SuppressWarnings("WeakerAccess") // BSON needs to access it
        public User(@BsonId int id, @BsonProperty("name") String name, @BsonProperty("surname") String surname, @BsonProperty("age") int age, @BsonProperty("iq") int iq) {
            this.userId = id;
            this.name = name;
            this.surname = surname;
            this.age = age;
            this.iq = iq;
        }

        // For initial insert
        Document toDocument() {
            return new Document()
                    .append("_id", userId)
                    .append("name", name)
                    .append("surname", surname)
                    .append("age", age)
                    .append("iq", iq);
        }

        @BsonId // For some reasons BSON only likes accessors
        public int getId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getIq() {
            return iq;
        }

        public void setIq(int iq) {
            this.iq = iq;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + userId +
                    ", name='" + name + '\'' +
                    ", surname='" + surname + '\'' +
                    ", age=" + age +
                    ", iq=" + iq +
                    '}';
        }
    }

}
