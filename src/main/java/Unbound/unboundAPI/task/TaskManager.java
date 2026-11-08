package Unbound.unboundAPI.task;

import Unbound.unboundAPI.UnboundAPI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class TaskManager {

    private final UnboundAPI plugin;

    public TaskManager(UnboundAPI plugin) {
        this.plugin = plugin;
    }

    public BukkitTask sync(Runnable action) {
        return Bukkit.getScheduler().runTask(plugin, action);
    }

    public BukkitTask executeSync(Runnable action) {
        if (Bukkit.isPrimaryThread()) {
            action.run();
            return null;
        }
        return sync(action);
    }

    public BukkitTask later(long delayTicks, Runnable action) {
        return Bukkit.getScheduler().runTaskLater(plugin, action, Math.max(0L, delayTicks));
    }

    public BukkitTask repeat(long delayTicks, long periodTicks, Runnable action) {
        if (periodTicks < 1L) {
            throw new IllegalArgumentException("periodTicks must be at least 1.");
        }
        return Bukkit.getScheduler().runTaskTimer(plugin, action, Math.max(0L, delayTicks), periodTicks);
    }

    public BukkitTask async(Runnable action) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, action);
    }

    public BukkitTask asyncLater(long delayTicks, Runnable action) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, action, Math.max(0L, delayTicks));
    }

    public BukkitTask asyncRepeat(long delayTicks, long periodTicks, Runnable action) {
        if (periodTicks < 1L) {
            throw new IllegalArgumentException("periodTicks must be at least 1.");
        }
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, action,
                Math.max(0L, delayTicks), periodTicks);
    }

    public void cancelAll() {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}
