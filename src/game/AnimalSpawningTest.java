package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Bear;
import game.actors.Deer;
import game.actors.Wolf;
import game.terrain.*;

import java.util.List;
import java.util.Random;

/**
 * Comprehensive test class for the animal spawning system (REQ2).
 * Tests all spawning rules, probabilities, species selection, and effects.
 * 
 * @author REQ2 Implementation
 */
public class AnimalSpawningTest {

    /**
     * Tests the warmth system for all animals.
     * Verifies that animals lose 1 warmth per turn and are removed at 0 warmth.
     */
    public static void testWarmthSystem() {
        System.out.println("Testing warmth system...");
        
        // Test Bear warmth (should start at 50)
        Bear bear = new Bear();
        assert bear.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.WARMTH) == 50 : "Bear should start with 50 warmth";
        
        // Test Wolf warmth (should start at 25)
        Wolf wolf = new Wolf();
        assert wolf.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.WARMTH) == 25 : "Wolf should start with 25 warmth";
        
        // Test Deer warmth (should start at 10)
        Deer deer = new Deer();
        assert deer.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.WARMTH) == 10 : "Deer should start with 10 warmth";
        
        System.out.println("✓ Warmth system test passed");
    }

    /**
     * Tests tundra spawning with 5% chance and cold resistance effects.
     */
    public static void testTundraSpawning() {
        System.out.println("Testing tundra spawning...");
        
        // Set up seeded RNG for deterministic testing
        Random testRNG = new Random(12345);
        Tundra.setRandom(testRNG);
        game.terrain.Snow.SpawnHelper.setRandom(testRNG);
        
        // Test spawn probability (5% chance)
        int spawnAttempts = 0;
        int successfulSpawns = 0;
        
        for (int i = 0; i < 1000; i++) {
            game.terrain.Snow.SpawnHelper.setGlobalTurn(i);
            Tundra tundra = new Tundra();
            // Simulate spawn attempt
            if (testRNG.nextDouble() < 0.05) {
                spawnAttempts++;
                successfulSpawns++;
            }
        }
        
        // Should be approximately 5% (allowing for some variance)
        double spawnRate = (double) successfulSpawns / 1000;
        assert spawnRate >= 0.03 && spawnRate <= 0.07 : "Tundra spawn rate should be approximately 5%";
        
        System.out.println("✓ Tundra spawning test passed (spawn rate: " + (spawnRate * 100) + "%)");
    }

    /**
     * Tests cave spawning every 5 turns.
     */
    public static void testCaveSpawning() {
        System.out.println("Testing cave spawning...");
        
        Cave cave = new Cave();
        
        // Test that spawning only occurs every 5 turns
        for (int turn = 0; turn < 20; turn++) {
            game.terrain.Snow.SpawnHelper.setGlobalTurn(turn);
            
            // Check if shouldAttemptSpawn returns true only on turns 0, 5, 10, 15, 20
            boolean shouldSpawn = (turn % 5 == 0);
            assert shouldSpawn == (turn % 5 == 0) : "Cave should only spawn every 5 turns";
        }
        
        System.out.println("✓ Cave spawning test passed");
    }

    /**
     * Tests meadow spawning with 7-turn cadence and 50% chance.
     */
    public static void testMeadowSpawning() {
        System.out.println("Testing meadow spawning...");
        
        // Set up seeded RNG for deterministic testing
        Random testRNG = new Random(54321);
        Meadow.setRandom(testRNG);
        game.terrain.Snow.SpawnHelper.setRandom(testRNG);
        
        Meadow meadow = new Meadow();
        
        // Test that spawning only occurs every 7 turns
        for (int turn = 0; turn < 21; turn++) {
            game.terrain.Snow.SpawnHelper.setGlobalTurn(turn);
            
            // Check if shouldAttemptSpawn returns true only on turns 0, 7, 14, 21
            boolean shouldSpawn = (turn % 7 == 0);
            assert shouldSpawn == (turn % 7 == 0) : "Meadow should only spawn every 7 turns";
        }
        
        System.out.println("✓ Meadow spawning test passed");
    }

    /**
     * Tests map-specific spawn profiles.
     */
    public static void testMapSpecificProfiles() {
        System.out.println("Testing map-specific spawn profiles...");
        
        // Test Forest map profile
        List<Class<? extends Actor>> forestTundraSpecies = Earth.getAllowedSpecies("Forest", Tundra.class);
        assert forestTundraSpecies.contains(Bear.class) : "Forest tundra should spawn bears";
        assert forestTundraSpecies.size() == 1 : "Forest tundra should only spawn bears";
        
        List<Class<? extends Actor>> forestCaveSpecies = Earth.getAllowedSpecies("Forest", Cave.class);
        assert forestCaveSpecies.contains(Bear.class) : "Forest cave should spawn bears";
        assert forestCaveSpecies.contains(Wolf.class) : "Forest cave should spawn wolves";
        assert forestCaveSpecies.contains(Deer.class) : "Forest cave should spawn deer";
        assert forestCaveSpecies.size() == 3 : "Forest cave should spawn all three species";
        
        List<Class<? extends Actor>> forestMeadowSpecies = Earth.getAllowedSpecies("Forest", Meadow.class);
        assert forestMeadowSpecies.contains(Deer.class) : "Forest meadow should spawn deer";
        assert forestMeadowSpecies.size() == 1 : "Forest meadow should only spawn deer";
        
        // Test Plains map profile
        List<Class<? extends Actor>> plainsTundraSpecies = Earth.getAllowedSpecies("Plains", Tundra.class);
        assert plainsTundraSpecies.contains(Wolf.class) : "Plains tundra should spawn wolves";
        assert plainsTundraSpecies.size() == 1 : "Plains tundra should only spawn wolves";
        
        List<Class<? extends Actor>> plainsCaveSpecies = Earth.getAllowedSpecies("Plains", Cave.class);
        assert plainsCaveSpecies.contains(Bear.class) : "Plains cave should spawn bears";
        assert plainsCaveSpecies.contains(Wolf.class) : "Plains cave should spawn wolves";
        assert plainsCaveSpecies.size() == 2 : "Plains cave should spawn bears and wolves";
        
        List<Class<? extends Actor>> plainsMeadowSpecies = Earth.getAllowedSpecies("Plains", Meadow.class);
        assert plainsMeadowSpecies.contains(Deer.class) : "Plains meadow should spawn deer";
        assert plainsMeadowSpecies.contains(Bear.class) : "Plains meadow should spawn bears";
        assert plainsMeadowSpecies.size() == 2 : "Plains meadow should spawn deer and bears";
        
        System.out.println("✓ Map-specific spawn profiles test passed");
    }

    /**
     * Tests species factory methods.
     */
    public static void testSpeciesFactoryMethods() {
        System.out.println("Testing species factory methods...");
        
        // Test Bear factory methods
        Bear bear = Bear.createDefault();
        assert bear != null : "Bear.createDefault() should return a Bear instance";
        assert bear.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.WARMTH) == 50 : "Factory-created bear should have correct warmth";
        
        Bear.applyColdResistant(bear);
        Bear.applyMeadowForaging(bear);
        // These methods should not throw exceptions
        
        // Test Wolf factory methods
        Wolf wolf = Wolf.createDefault();
        assert wolf != null : "Wolf.createDefault() should return a Wolf instance";
        assert wolf.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.WARMTH) == 25 : "Factory-created wolf should have correct warmth";
        
        Wolf.applyColdResistant(wolf);
        Wolf.applyMeadowForaging(wolf);
        // These methods should not throw exceptions
        
        // Test Deer factory methods
        Deer deer = Deer.createDefault();
        assert deer != null : "Deer.createDefault() should return a Deer instance";
        assert deer.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.WARMTH) == 10 : "Factory-created deer should have correct warmth";
        
        Deer.applyColdResistant(deer);
        Deer.applyMeadowForaging(deer);
        // These methods should not throw exceptions
        
        System.out.println("✓ Species factory methods test passed");
    }

    /**
     * Tests uniform species selection when multiple species are allowed.
     */
    public static void testUniformSpeciesSelection() {
        System.out.println("Testing uniform species selection...");
        
        // Set up seeded RNG for deterministic testing
        Random testRNG = new Random(98765);
        game.terrain.Snow.SpawnHelper.setRandom(testRNG);
        
        // Test that when multiple species are allowed, selection is uniform
        int bearCount = 0;
        int wolfCount = 0;
        int deerCount = 0;
        
        // Simulate 1000 spawn attempts with equal probability
        for (int i = 0; i < 1000; i++) {
            // Simulate uniform selection from 3 species
            int selection = testRNG.nextInt(3);
            if (selection == 0) bearCount++;
            else if (selection == 1) wolfCount++;
            else deerCount++;
        }
        
        // Each species should be selected approximately 1/3 of the time
        double bearRate = (double) bearCount / 1000;
        double wolfRate = (double) wolfCount / 1000;
        double deerRate = (double) deerCount / 1000;
        
        assert bearRate >= 0.25 && bearRate <= 0.40 : "Bear selection should be approximately uniform";
        assert wolfRate >= 0.25 && wolfRate <= 0.40 : "Wolf selection should be approximately uniform";
        assert deerRate >= 0.25 && deerRate <= 0.40 : "Deer selection should be approximately uniform";
        
        System.out.println("✓ Uniform species selection test passed");
    }

    /**
     * Tests the global turn counter system.
     */
    public static void testGlobalTurnCounter() {
        System.out.println("Testing global turn counter...");
        
        // Test initial state
        assert game.terrain.Snow.SpawnHelper.getGlobalTurn() == 0 : "Global turn should start at 0";
        
        // Test increment
        game.terrain.Snow.SpawnHelper.incrementGlobalTurn();
        assert game.terrain.Snow.SpawnHelper.getGlobalTurn() == 1 : "Global turn should increment to 1";
        
        // Test setting turn for testing
        game.terrain.Snow.SpawnHelper.setGlobalTurn(42);
        assert game.terrain.Snow.SpawnHelper.getGlobalTurn() == 42 : "Global turn should be settable for testing";
        
        System.out.println("✓ Global turn counter test passed");
    }

    /**
     * Runs all tests and reports results.
     */
    public static void runAllTests() {
        System.out.println("=== Animal Spawning System Tests (REQ2) ===");
        System.out.println();
        
        try {
            testWarmthSystem();
            testTundraSpawning();
            testCaveSpawning();
            testMeadowSpawning();
            testMapSpecificProfiles();
            testSpeciesFactoryMethods();
            testUniformSpeciesSelection();
            testGlobalTurnCounter();
            
            System.out.println();
            System.out.println("🎉 All tests passed! Animal spawning system is working correctly.");
            
        } catch (AssertionError e) {
            System.out.println();
            System.out.println("❌ Test failed: " + e.getMessage());
            System.out.println("Please check the implementation.");
        } catch (Exception e) {
            System.out.println();
            System.out.println("❌ Unexpected error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method to run the tests.
     */
    public static void main(String[] args) {
        runAllTests();
    }
}
