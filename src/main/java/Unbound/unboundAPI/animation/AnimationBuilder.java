package Unbound.unboundAPI.animation;

import Unbound.unboundAPI.item.ItemBuilder;
import Unbound.unboundAPI.menu.Menu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class AnimationBuilder {

    private final AnimationManager manager;
    private final List<Frame> frames = new ArrayList<>();
    private int repetitions = 1;
    private boolean infinite;
    private Consumer<AnimationContext> onComplete;

    AnimationBuilder(AnimationManager manager) {
        this.manager = manager;
    }

    public AnimationBuilder frame(long durationTicks, Consumer<AnimationContext> action) {
        if (action == null) {
            throw new IllegalArgumentException("Frame action cannot be null.");
        }
        frames.add(new Frame(Math.max(1L, durationTicks), action));
        return this;
    }

    public AnimationBuilder actionBar(long durationTicks, String message) {
        return frame(durationTicks, context -> context.actionBar(message));
    }

    public AnimationBuilder message(long durationTicks, String message) {
        return frame(durationTicks, context -> context.send(message));
    }

    public AnimationBuilder title(long durationTicks, String title, String subtitle) {
        return frame(durationTicks, context -> context.title(title, subtitle));
    }

    public AnimationBuilder sound(long durationTicks, Sound sound, float volume, float pitch) {
        return frame(durationTicks, context -> context.sound(sound, volume, pitch));
    }

    public AnimationBuilder item(long durationTicks, int slot, ItemStack item) {
        ItemStack copy = item == null ? null : item.clone();
        return frame(durationTicks, context -> context.setItem(slot, copy));
    }

    public AnimationBuilder item(long durationTicks, int slot, ItemBuilder item) {
        return item(durationTicks, slot, item.build());
    }

    public AnimationBuilder repeat(int times) {
        this.repetitions = Math.max(1, times);
        this.infinite = false;
        return this;
    }

    public AnimationBuilder repeatForever() {
        this.infinite = true;
        return this;
    }

    public AnimationBuilder onComplete(Consumer<AnimationContext> action) {
        this.onComplete = action;
        return this;
    }

    public Animation play(Player player) {
        return manager.play(player, null, this);
    }

    public Animation play(Player player, Menu menu) {
        return manager.play(player, menu, this);
    }

    List<Frame> frames() {
        return List.copyOf(frames);
    }

    int repetitions() {
        return repetitions;
    }

    boolean infinite() {
        return infinite;
    }

    Consumer<AnimationContext> completionAction() {
        return onComplete;
    }

    static final class Frame {
        private final long durationTicks;
        private final Consumer<AnimationContext> action;

        private Frame(long durationTicks, Consumer<AnimationContext> action) {
            this.durationTicks = durationTicks;
            this.action = action;
        }

        long durationTicks() {
            return durationTicks;
        }

        Consumer<AnimationContext> action() {
            return action;
        }
    }
}
