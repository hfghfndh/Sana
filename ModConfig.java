package com.sansmod.config;

/** All saved settings. Public fields so Gson can read and write them. */
public class ModConfig {
    // Targeting
    public boolean targetPlayers = true;
    public boolean targetMobs = true;
    public String priorityMode = "Closest";
    public float targetRange = 32f;
    public boolean predictMovement = true;

    // Dodge
    public boolean autoDodge = false;
    public float dodgeDetectRange = 12f;
    public float maxTeleportDistance = 4f;
    public float dodgeCooldownTicks = 5f;

    // Abilities
    public boolean gasterBlasterEnabled = true;
    public boolean boneAttackEnabled = true;
    public boolean teleportEffectEnabled = true;
    public float gasterRange = 10f;
    public float gasterDamage = 6f;
    public float boneSpeed = 1f;

    // Visuals
    public boolean teleportParticles = true;
    public boolean afterimages = true;
    public String teleportStyle = "Classic";
    public float animationTicks = 10f;
    public boolean showTargetHud = true;

    // Combat
    public boolean showAttackCooldown = true;
    public boolean maceIndicator = false;

    // Settings
    public boolean guiAnimations = true;
}
