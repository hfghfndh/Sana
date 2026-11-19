package com.sansmod.gui;

import com.sansmod.Keybinds;
import com.sansmod.config.ConfigManager;
import com.sansmod.config.ModConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MainScreen extends Screen {
    private static final int W = 380, H = 230, SIDE = 92, ROW = 24;
    private static final int PANEL = 0xFF0F1626, SIDEBG = 0xFF0A0F1C, ROWBG = 0xFF151D2E, ROWHOVER = 0xFF1B2740;
    private static final int CYAN = 0xFF22D3EE, BLUE = 0xFF3B82F6, TEXT = 0xFFE5E7EB, DIM = 0xFF9CA3AF, OFF = 0xFF374151;

    private final String[] cats = {"Combat", "Dodge", "Abilities", "Visuals", "Targeting", "Settings"};
    private final float[] catAnim = new float[cats.length];
    private final List<List<Row>> pages = new ArrayList<>();
    private int cat = 0;
    private float open = 0f;
    private Row dragging;
    private KeyBinding rebinding;

    public MainScreen() {
        super(Text.literal("Sans Mod"));
        ModConfig c = ConfigManager.get();

        List<Row> combat = new ArrayList<>();
        combat.add(new Toggle("Show attack cooldown", () -> c.showAttackCooldown, v -> c.showAttackCooldown = v));
        combat.add(new Toggle("Mace damage indicator", () -> c.maceIndicator, v -> c.maceIndicator = v));
        pages.add(combat);

        List<Row> dodge = new ArrayList<>();
        dodge.add(new Toggle("Auto dodge", () -> c.autoDodge, v -> c.autoDodge = v));
        dodge.add(new Slider("Threat detect range", 4, 32, 1, () -> c.dodgeDetectRange, v -> c.dodgeDetectRange = v, "%.0f blocks"));
        dodge.add(new Slider("Max teleport distance", 1, 8, 0.5f, () -> c.maxTeleportDistance, v -> c.maxTeleportDistance = v, "%.1f blocks"));
        dodge.add(new Slider("Dodge cooldown", 0, 40, 1, () -> c.dodgeCooldownTicks, v -> c.dodgeCooldownTicks = v, "%.0f ticks"));
        pages.add(dodge);

        List<Row> abil = new ArrayList<>();
        abil.add(new Toggle("Gaster Blaster", () -> c.gasterBlasterEnabled, v -> c.gasterBlasterEnabled = v));
        abil.add(new Toggle("Bone attack", () -> c.boneAttackEnabled, v -> c.boneAttackEnabled = v));
        abil.add(new Toggle("Teleport effect", () -> c.teleportEffectEnabled, v -> c.teleportEffectEnabled = v));
        abil.add(new Slider("Blaster range", 3, 20, 1, () -> c.gasterRange, v -> c.gasterRange = v, "%.0f blocks"));
        abil.add(new Slider("Blaster damage (custom servers)", 1, 40, 1, () -> c.gasterDamage, v -> c.gasterDamage = v, "%.0f"));
        abil.add(new Slider("Bone speed", 0.25f, 3, 0.25f, () -> c.boneSpeed, v -> c.boneSpeed = v, "%.2fx"));
        pages.add(abil);

        List<Row> vis = new ArrayList<>();
        vis.add(new Toggle("Teleport particles", () -> c.teleportParticles, v -> c.teleportParticles = v));
        vis.add(new Toggle("Afterimages", () -> c.afterimages, v -> c.afterimages = v));
        vis.add(new Cycle("Teleport style", new String[]{"Classic", "Glitch", "Soul"}, () -> c.teleportStyle, v -> c.teleportStyle = v));
        vis.add(new Slider("Animation duration", 2, 40, 1, () -> c.animationTicks, v -> c.animationTicks = v, "%.0f ticks"));
        vis.add(new Toggle("Show target HUD", () -> c.showTargetHud, v -> c.showTargetHud = v));
        pages.add(vis);

        List<Row> tgt = new ArrayList<>();
        tgt.add(new Toggle("Target players", () -> c.targetPlayers, v -> c.targetPlayers = v));
        tgt.add(new Toggle("Target mobs", () -> c.targetMobs, v -> c.targetMobs = v));
        tgt.add(new Cycle("Priority", new String[]{"Closest", "Players first", "Mobs first"}, () -> c.priorityMode, v -> c.priorityMode = v));
        tgt.add(new Slider("Target range", 8, 64, 1, () -> c.targetRange, v -> c.targetRange = v, "%.0f blocks"));
        tgt.add(new Toggle("Predict movement", () -> c.predictMovement, v -> c.predictMovement = v));
        pages.add(tgt);

        List<Row> set = new ArrayList<>();
        set.add(new Toggle("GUI animations", () -> c.guiAnimations, v -> c.guiAnimations = v));
        set.add(new Key("Open GUI", Keybinds.OPEN_GUI));
        set.add(new Key("Gaster Blaster", Keybinds.GASTER));
        set.add(new Key("Bone attack", Keybinds.BONE));
        set.add(new Key("Teleport effect", Keybinds.TELEPORT));
        set.add(new Key("Cycle target", Keybinds.CYCLE_TARGET));
        set.add(new Key("Toggle auto dodge", Keybinds.TOGGLE_DODGE));
        pages.add(set);
    }

    // ---- layout helpers ----
    private int pw() { return Math.min(W, width - 8); }
    private int left() { return (width - pw()) / 2; }
    private int top() { return (height - H) / 2 + (int) ((1f - ease()) * 12f); }
    private int cx() { return left() + SIDE + 10; }
    private int cw() { return pw() - SIDE - 20; }
    private int rowY(int i) { return top() + 38 + i * ROW; }
    private float ease() { float t = 1f - open; return 1f - t * t * t; }

    private static int a(int color, float alpha) {
        int base = (color >>> 24) & 0xFF;
        int na = Math.max(0, Math.min(255, (int) (base * alpha)));
        return (na << 24) | (color & 0xFFFFFF);
    }

    private static int mix(int c1, int c2, float t) {
        int r = (int) (((c1 >> 16) & 0xFF) * (1 - t) + ((c2 >> 16) & 0xFF) * t);
        int g = (int) (((c1 >> 8) & 0xFF) * (1 - t) + ((c2 >> 8) & 0xFF) * t);
        int b = (int) ((c1 & 0xFF) * (1 - t) + (c2 & 0xFF) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ---- rendering ----
    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ModConfig cfg = ConfigManager.get();
        open = cfg.guiAnimations ? Math.min(1f, open + delta * 0.2f) : 1f;
        float e = ease();
        int l = left(), t = top(), w = pw();

        ctx.fill(0, 0, width, height, a(0xAA000000, e));
        ctx.fill(l - 1, t - 1, l + w + 1, t + H + 1, a(CYAN, e * 0.5f));
        ctx.fill(l, t, l + w, t + H, a(PANEL, e));
        ctx.fill(l, t, l + SIDE, t + H, a(SIDEBG, e));
        ctx.fill(l, t + 30, l + w, t + 31, a(BLUE, e * 0.6f));

        ctx.drawTextWithShadow(textRenderer, "SANS MOD", l + 10, t + 8, a(CYAN, e));
        ctx.drawTextWithShadow(textRenderer, "* you're gonna have a bad time.", l + SIDE + 10, t + 11, a(DIM, e));

        for (int i = 0; i < cats.length; i++) {
            int y = t + 38 + i * 26;
            boolean sel = i == cat;
            boolean hov = in(mx, my, l, y, SIDE, 22);
            float target = sel ? 1f : (hov ? 0.4f : 0f);
            catAnim[i] += (target - catAnim[i]) * Math.min(1f, delta * 0.5f);
            ctx.fill(l, y, l + SIDE, y + 22, a(mix(SIDEBG, 0xFF16304A, catAnim[i]), e));
            if (sel) ctx.fill(l, y, l + 2, y + 22, a(CYAN, e));
            ctx.drawTextWithShadow(textRenderer, cats[i], l + 12, y + 7, a(mix(DIM, CYAN, catAnim[i]), e));
        }

        List<Row> rows = pages.get(cat);
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).draw(ctx, cx(), rowY(i), cw(), mx, my, e, delta);
        }

        ctx.drawTextWithShadow(textRenderer, "Settings save automatically", l + 10, t + H - 14, a(DIM, e));
    }

    // ---- input ----
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        int mx = (int) click.x(), my = (int) click.y();
        for (int i = 0; i < cats.length; i++) {
            if (in(mx, my, left(), top() + 38 + i * 26, SIDE, 22)) {
                cat = i;
                rebinding = null;
                return true;
            }
        }
        List<Row> rows = pages.get(cat);
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).click(cx(), rowY(i), cw(), mx, my)) {
                ConfigManager.save();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging != null) {
            dragging.drag(cx(), cw(), (int) click.x());
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging != null) {
            dragging = null;
            ConfigManager.save();
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (rebinding != null) {
            if (key != GLFW.GLFW_KEY_ESCAPE) {
                rebinding.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(key));
                KeyBinding.updateKeysByCode();
                if (client != null) client.options.write();
            }
            rebinding = null;
            return true;
        }
        int openCode = InputUtil.fromTranslationKey(Keybinds.OPEN_GUI.getBoundKeyTranslationKey()).getCode();
        if (key == openCode || key == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public void removed() {
        ConfigManager.save();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ---- rows ----
    private abstract class Row {
        final String label;

        Row(String label) { this.label = label; }

        abstract void draw(DrawContext c, int x, int y, int w, int mx, int my, float e, float delta);

        boolean click(int x, int y, int w, int mx, int my) { return false; }

        void drag(int x, int w, int mx) { }

        void base(DrawContext c, int x, int y, int w, int mx, int my, float e) {
            boolean hov = in(mx, my, x, y, w, ROW - 2);
            c.fill(x, y, x + w, y + ROW - 2, a(hov ? ROWHOVER : ROWBG, e));
            c.drawTextWithShadow(textRenderer, label, x + 8, y + 8, a(TEXT, e));
        }
    }

    private class Toggle extends Row {
        final BooleanSupplier get;
        final Consumer<Boolean> set;
        float anim = -1f;

        Toggle(String label, BooleanSupplier get, Consumer<Boolean> set) {
            super(label);
            this.get = get;
            this.set = set;
        }

        @Override
        void draw(DrawContext c, int x, int y, int w, int mx, int my, float e, float delta) {
            base(c, x, y, w, mx, my, e);
            float target = get.getAsBoolean() ? 1f : 0f;
            if (anim < 0f) anim = target;
            anim += (target - anim) * Math.min(1f, delta * 0.6f);
            int tx = x + w - 34, ty = y + 5;
            c.fill(tx, ty, tx + 26, ty + 12, a(mix(OFF, CYAN, anim), e));
            int kx = tx + 1 + (int) (anim * 14f);
            c.fill(kx, ty + 1, kx + 10, ty + 11, a(0xFFFFFFFF, e));
        }

        @Override
        boolean click(int x, int y, int w, int mx, int my) {
            if (!in(mx, my, x, y, w, ROW - 2)) return false;
            set.accept(!get.getAsBoolean());
            return true;
        }
    }

    private class Slider extends Row {
        final float min, max, step;
        final Supplier<Float> get;
        final Consumer<Float> set;
        final String fmt;

        Slider(String label, float min, float max, float step, Supplier<Float> get, Consumer<Float> set, String fmt) {
            super(label);
            this.min = min;
            this.max = max;
            this.step = step;
            this.get = get;
            this.set = set;
            this.fmt = fmt;
        }

        @Override
        void draw(DrawContext c, int x, int y, int w, int mx, int my, float e, float delta) {
            base(c, x, y, w, mx, my, e);
            int tx = x + w - 84, tw = 76;
            float f = (get.get() - min) / (max - min);
            f = Math.max(0f, Math.min(1f, f));
            c.fill(tx, y + 9, tx + tw, y + 13, a(OFF, e));
            c.fill(tx, y + 9, tx + (int) (tw * f), y + 13, a(CYAN, e));
            int kx = tx + (int) (tw * f) - 2;
            c.fill(kx, y + 6, kx + 4, y + 16, a(0xFFFFFFFF, e));
            String v = String.format(Locale.ROOT, fmt, get.get());
            c.drawTextWithShadow(textRenderer, v, tx - 8 - textRenderer.getWidth(v), y + 8, a(CYAN, e));
        }

        @Override
        boolean click(int x, int y, int w, int mx, int my) {
            int tx = x + w - 84;
            if (in(mx, my, tx - 4, y, 84, ROW - 2)) {
                dragging = this;
                drag(x, w, mx);
                return true;
            }
            return false;
        }

        @Override
        void drag(int x, int w, int mx) {
            int tx = x + w - 84, tw = 76;
            float f = Math.max(0f, Math.min(1f, (mx - tx) / (float) tw));
            float v = min + f * (max - min);
            v = Math.round(v / step) * step;
            set.accept(Math.max(min, Math.min(max, v)));
        }
    }

    private class Cycle extends Row {
        final String[] options;
        final Supplier<String> get;
        final Consumer<String> set;

        Cycle(String label, String[] options, Supplier<String> get, Consumer<String> set) {
            super(label);
            this.options = options;
            this.get = get;
            this.set = set;
        }

        @Override
        void draw(DrawContext c, int x, int y, int w, int mx, int my, float e, float delta) {
            base(c, x, y, w, mx, my, e);
            String v = "< " + get.get() + " >";
            int vw = textRenderer.getWidth(v);
            c.fill(x + w - vw - 16, y + 3, x + w - 4, y + ROW - 5, a(0xFF0B3A4A, e));
            c.drawTextWithShadow(textRenderer, v, x + w - vw - 10, y + 8, a(CYAN, e));
        }

        @Override
        boolean click(int x, int y, int w, int mx, int my) {
            if (!in(mx, my, x, y, w, ROW - 2)) return false;
            int idx = 0;
            for (int i = 0; i < options.length; i++) if (options[i].equals(get.get())) idx = i;
            set.accept(options[(idx + 1) % options.length]);
            return true;
        }
    }

    private class Key extends Row {
        final KeyBinding binding;

        Key(String label, KeyBinding binding) {
            super(label);
            this.binding = binding;
        }

        @Override
        void draw(DrawContext c, int x, int y, int w, int mx, int my, float e, float delta) {
            base(c, x, y, w, mx, my, e);
            boolean waiting = rebinding == binding;
            String v = waiting ? "Press a key..." : binding.getBoundKeyLocalizedText().getString();
            int vw = textRenderer.getWidth(v);
            c.fill(x + w - vw - 16, y + 3, x + w - 4, y + ROW - 5, a(waiting ? 0xFF1D4ED8 : 0xFF0B3A4A, e));
            c.drawTextWithShadow(textRenderer, v, x + w - vw - 10, y + 8, a(waiting ? TEXT : CYAN, e));
        }

        @Override
        boolean click(int x, int y, int w, int mx, int my) {
            if (!in(mx, my, x, y, w, ROW - 2)) return false;
            rebinding = rebinding == binding ? null : binding;
            return true;
        }
    }
}
