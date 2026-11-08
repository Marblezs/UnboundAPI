package Unbound.unboundAPI.animation;

import Unbound.unboundAPI.UnboundAPI;
import Unbound.unboundAPI.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class Animation {

    private final UnboundAPI plugin;
    private final AnimationManager manager;
    private final Player player;
    private final Menu menu;
    private final List<AnimationBuilder.Frame> frames;
    private final int repetitions;
    private final boolean infinite;
    private final Consumer<AnimationContext> onComplete;
    private final AnimationContext context;

    private BukkitTask task;
    private int frameIndex;
    private int completedLoops;
    private long remainingTicks;
    private boolean running;

    Animation(UnboundAPI plugin, AnimationManager manager, Player player, Menu menu, AnimationBuilder builder) {
        this.plugin = plugin;
        this.manager = manager;
        this.player = player;
        this.menu = menu;
        this.frames = builder.frames();
        this.repetitions = builder.repetitions();
        this.infinite = builder.infinite();
        this.onComplete = builder.completionAction();
        this.context = new AnimationContext(player, menu);
    }

    void start() {
        if (frames.isEmpty()) {
            throw new IllegalStateException("Cannot play an animation without frames.");
        }
        if (!player.isOnline()) {
            manager.remove(this);
            return;
        }

        running = true;
        applyFrame();
        if (running) {
            task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
        }
    }

    private void tick() {
        if (!running) {
            return;
        }
        if (!player.isOnline()) {
            cancel();
            return;
        }

        remainingTicks--;
        if (remainingTicks > 0) {
            return;
        }

        frameIndex++;
        if (frameIndex >= frames.size()) {
            completedLoops++;
            if (!infinite && completedLoops >= repetitions) {
                finish();
                return;
            }
            frameIndex = 0;
        }
        applyFrame();
    }

    private void applyFrame() {
        AnimationBuilder.Frame frame = frames.get(frameIndex);
        remainingTicks = frame.durationTicks();
        try {
            frame.action().accept(context);
        } catch (Throwable throwable) {
            plugin.getLogger().log(Level.SEVERE, "An UnboundAPI animation frame failed for " + player.getName() + ".", throwable);
            cancel();
        }
    }

    private void finish() {
        if (!running) {
            return;
        }
        running = false;
        if (task != null) {
            task.cancel();
            task = null;
        }
        manager.remove(this);
        if (onComplete != null && player.isOnline()) {
            try {
                onComplete.accept(context);
            } catch (Throwable throwable) {
                plugin.getLogger().log(Level.SEVERE, "An UnboundAPI animation completion action failed for " + player.getName() + ".", throwable);
            }
        }
    }

    public void cancel() {
        if (!running && task == null) {
            manager.remove(this);
            return;
        }
        running = false;
        if (task != null) {
            task.cancel();
            task = null;
        }
        manager.remove(this);
    }

    public boolean isRunning() {
        return running;
    }

    public Player player() {
        return player;
    }

    public Menu menu() {
        return menu;
    }
}
