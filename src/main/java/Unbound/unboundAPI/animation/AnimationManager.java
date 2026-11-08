package Unbound.unboundAPI.animation;

import Unbound.unboundAPI.UnboundAPI;
import Unbound.unboundAPI.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class AnimationManager {

    private final UnboundAPI plugin;
    private final Set<Animation> active = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public AnimationManager(UnboundAPI plugin) {
        this.plugin = plugin;
    }

    public AnimationBuilder create() {
        return new AnimationBuilder(this);
    }

    Animation play(Player player, Menu menu, AnimationBuilder builder) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (!org.bukkit.Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Animations must be started from the server thread.");
        }
        if (builder.frames().isEmpty()) {
            throw new IllegalStateException("Cannot play an animation without frames.");
        }
        Animation animation = new Animation(plugin, this, player, menu, builder);
        active.add(animation);
        animation.start();
        return animation;
    }

    void remove(Animation animation) {
        active.remove(animation);
    }

    public void cancelAll() {
        for (Animation animation : Set.copyOf(active)) {
            animation.cancel();
        }
    }

    public int activeCount() {
        return active.size();
    }
}
