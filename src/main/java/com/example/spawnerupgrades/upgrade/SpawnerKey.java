package com.example.spawnerupgrades.upgrade;

import org.bukkit.Location;

import java.util.Objects;

/**
 * A simple, serialization-friendly identity for a spawner block: its world
 * name and integer coordinates. Used as the key for stored upgrade levels.
 */
public class SpawnerKey {
    private final String world;
    private final int x, y, z;

    public SpawnerKey(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SpawnerKey of(Location loc) {
        return new SpawnerKey(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /** Stable string form used as a YAML config key. */
    public String serialize() {
        return world + ";" + x + ";" + y + ";" + z;
    }

    public static SpawnerKey deserialize(String s) {
        String[] parts = s.split(";");
        return new SpawnerKey(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpawnerKey)) return false;
        SpawnerKey key = (SpawnerKey) o;
        return x == key.x && y == key.y && z == key.z && world.equals(key.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }
}
