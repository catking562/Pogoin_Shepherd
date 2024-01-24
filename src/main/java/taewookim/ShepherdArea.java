package taewookim;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public class ShepherdArea {

    public final Location mid;
    public final Vector size;

    public ShepherdArea(Location mid, Vector size) {
        this.mid = mid;
        this.size = size;
    }

    public ShepherdArea(YamlConfiguration config) {
        mid = config.getLocation("mid");
        size = config.getVector("size");
    }

    public int getSheeps() {
        int count = 0;
        for(Entity en : mid.getWorld().getNearbyEntities(mid, size.getX(), size.getY(), size.getZ())) {
            if(en instanceof Sheep) {
                count++;
            }
        }
        return count;
    }

    public void save(YamlConfiguration config) {
        config.set("mid", mid);
        config.set("size", size);
    }

}
