package dev.idank.data;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class MongoServiceTest<T> {

    private MongoDatabase database;

    private MongoService<DummyGameModel> validMongoService;
    private DummyGameModel validDuels;

    @BeforeEach
    public void setUp() {
        this.database = MongoClients.create("mongodb://localhost:27017").getDatabase("game_unit_testing");

        this.validDuels = new DummyGameModel(UUID.randomUUID(), "duels", 0, List.of(
                "Merkava 4",
                "Merkava 3"
        ));

        this.validMongoService = new MongoService<>(this.database, DummyGameModel.class);
    }

    @AfterEach
    public void tearDown() {
        this.validMongoService.getDatabase().drop();
    }

    @Test
    public void testInvalidGameModels() throws ExecutionException, InterruptedException {
        InvalidNameDummyGameModel invalidName = new InvalidNameDummyGameModel(UUID.randomUUID(), "");
        MongoService<InvalidNameDummyGameModel> invalidNameMongoService = new MongoService<>(this.database, InvalidNameDummyGameModel.class);

        assertThrows(ExecutionException.class, () -> invalidNameMongoService.insert(invalidName).get());
        assertThrows(UnsupportedGameModelException.class, () -> invalidNameMongoService.insertSync(invalidName));

        MissingAnnotationDummyGameModel missingAnnotation = new MissingAnnotationDummyGameModel(UUID.randomUUID(), "");
        MongoService<MissingAnnotationDummyGameModel> missingAnnotationMongoService = new MongoService<>(this.database, MissingAnnotationDummyGameModel.class);

        assertThrows(ExecutionException.class, () -> missingAnnotationMongoService.insert(missingAnnotation).get());
        assertThrows(UnsupportedGameModelException.class, () -> missingAnnotationMongoService.insertSync(missingAnnotation));
    }

    @Test
    public void testValidCollectionInsertAsync() throws ExecutionException, InterruptedException {
        this.validMongoService.insert(validDuels).get();

        CompletableFuture<DummyGameModel> getFuture = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel data = getFuture.get();

        assertEquals(data.name(), validDuels.name());
        assertEquals(data.uuid(), validDuels.uuid());
    }

    @Test
    public void testValidCollectionUpdateAsync() throws ExecutionException, InterruptedException {
        this.validMongoService.insert(validDuels).get();

        DummyGameModel updatedData = new DummyGameModel(validDuels.uuid(), "sheepwars", 0, List.of(
                "Merkava 4",
                "Merkava 3"
        ));
        this.validMongoService.update(validDuels.uuid(), updatedData).get();

        CompletableFuture<DummyGameModel> getFuture = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel data2 = getFuture.get();

        assertEquals(data2.name(), updatedData.name());
        assertEquals(data2.uuid(), updatedData.uuid());
    }

    @Test
    public void testIncrementFieldAsync() throws ExecutionException, InterruptedException {
        this.validMongoService.insert(validDuels).get();

        CompletableFuture<DummyGameModel> getFuture = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel data = getFuture.get();
        assertEquals(data.kills(), 0);

        this.validMongoService.incrementField(validDuels.uuid(), "kills", 1).get();
        CompletableFuture<DummyGameModel> updatedDataFuture = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel updatedData = updatedDataFuture.get();

        assertEquals(updatedData.kills(), 1);
    }

    @Test
    public void testRemoveDocumentAsync() throws ExecutionException, InterruptedException {
        this.validMongoService.insert(validDuels).get();
        this.validMongoService.remove(validDuels.uuid()).get();

        CompletableFuture<DummyGameModel> getFuture = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel data = getFuture.get();

        assertNull(data);
    }

    @Test
    public void testCollectionModificationAsync() throws ExecutionException, InterruptedException {
        this.validMongoService.insert(validDuels).get();

        String merkava2 = "Merkava 2";
        this.validMongoService.addToCollection(validDuels.uuid(), "ownedTanks", merkava2).get();

        CompletableFuture<DummyGameModel> getFuture = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel data = getFuture.get();

        List<String> tanks = data.ownedTanks();
        assertEquals(tanks.size(), 3);
        assertTrue(tanks.contains(merkava2));

        this.validMongoService.removeFromCollection(validDuels.uuid(), "ownedTanks", merkava2).get();
        CompletableFuture<DummyGameModel> getFuture2 = this.validMongoService.get(validDuels.uuid(), DummyGameModel.class);
        DummyGameModel data2 = getFuture2.get();

        List<String> tanks2 = data2.ownedTanks();
        assertEquals(tanks2.size(), 2);
        assertFalse(tanks2.contains(merkava2));
    }
}
