package game.spawning;

import game.actors.Crocodile;
import game.actors.Deer;
import game.actors.Bear;
import game.actors.Wolf;

/**
 * Simple test to verify post-spawn effects are registered correctly.
 * Run this to check that all animals have their effects registered.
 */
public class PostSpawnEffectTest {
    public static void main(String[] args) {
        System.out.println("Testing Post-Spawn Effect Registry...");
        
        // Trigger Earth static initialization (this registers all effects)
        try {
            Class.forName("game.Earth");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Could not load Earth class");
            return;
        }
        
        // Test that Crocodile effect is registered
        PostSpawnEffect crocEffect = PostSpawnEffectRegistry.getFor(Crocodile.class);
        if (crocEffect == null) {
            System.err.println("❌ FAIL: Crocodile post-spawn effect NOT registered!");
        } else {
            System.out.println("✅ PASS: Crocodile post-spawn effect registered (" + crocEffect.getClass().getSimpleName() + ")");
        }
        
        // Test Deer
        PostSpawnEffect deerEffect = PostSpawnEffectRegistry.getFor(Deer.class);
        if (deerEffect == null) {
            System.err.println("❌ FAIL: Deer post-spawn effect NOT registered!");
        } else {
            System.out.println("✅ PASS: Deer post-spawn effect registered (" + deerEffect.getClass().getSimpleName() + ")");
        }
        
        // Test Bear
        PostSpawnEffect bearEffect = PostSpawnEffectRegistry.getFor(Bear.class);
        if (bearEffect == null) {
            System.err.println("❌ FAIL: Bear post-spawn effect NOT registered!");
        } else {
            System.out.println("✅ PASS: Bear post-spawn effect registered (" + bearEffect.getClass().getSimpleName() + ")");
        }
        
        // Test Wolf
        PostSpawnEffect wolfEffect = PostSpawnEffectRegistry.getFor(Wolf.class);
        if (wolfEffect == null) {
            System.err.println("❌ FAIL: Wolf post-spawn effect NOT registered!");
        } else {
            System.out.println("✅ PASS: Wolf post-spawn effect registered (" + wolfEffect.getClass().getSimpleName() + ")");
        }
        
        // Verify Crocodile effect is specifically the poison pulse
        if (crocEffect != null && crocEffect instanceof CrocodilePoisonPulseEffect) {
            System.out.println("✅ PASS: Crocodile effect is correct type (CrocodilePoisonPulseEffect)");
        } else if (crocEffect != null) {
            System.err.println("⚠️  WARNING: Crocodile effect is wrong type: " + crocEffect.getClass().getName());
        }
        
        System.out.println("\nTest Summary: All post-spawn effects should be registered during Earth static initialization.");
        System.out.println("If all effects show ✅ PASS, the registry is working correctly.");
        System.out.println("The poison pulse should now work from ALL spawners (Swamp, Tundra, Meadow, Cave).");
    }
}

