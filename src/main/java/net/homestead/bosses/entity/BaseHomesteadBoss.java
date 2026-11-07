package net.homestead.bosses.entity;

import net.homestead.bosses.HomesteadBosses;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Shared behaviour for every named Homestead boss: a tracked boss bar, and the handful of
 * conventions that separate a "boss" from a reskinned hostile mob in vanilla Minecraft -
 * it won't despawn mid-fight, can't be shoved around or leashed away, shrugs off fall damage
 * and Wither, and announces itself to the server when it shows up and when it falls.
 * Pumpkin Minions do NOT extend this class: they're meant to behave like ordinary disposable
 * reinforcements, not boss-tier mobs.
 */
@Mod.EventBusSubscriber(modid = HomesteadBosses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class BaseHomesteadBoss extends Monster {
    protected final ServerBossEvent bossEvent;
    private boolean announcedSpawn = false;

    protected BaseHomesteadBoss(EntityType<? extends Monster> type, Level level, String name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        super(type, level);
        this.bossEvent = new ServerBossEvent(Component.translatable("entity.homesteadbosses." + name), color, overlay);
        this.setPersistenceRequired();
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.05D, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) bossEvent.setProgress(getHealth() / getMaxHealth());
    }

    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossEvent.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossEvent.removePlayer(player); }

    protected boolean isBelowHalfHealth() { return getHealth() <= getMaxHealth() * 0.5f; }

    // A boss that wanders out of render distance and quietly disappears isn't a boss fight,
    // it's a bug report. This is the same trick the Wither and Ender Dragon use: skip the
    // distance/peaceful-difficulty despawn checks entirely.
    @Override public void checkDespawn() { /* intentionally empty: bosses never auto-despawn */ }

    // Can't be bumped out of position by a horde of zombies, can't be walked onto a leash
    // and dragged into a hole, and doesn't eat fall damage if it gets knocked off a ledge.
    @Override public boolean isPushable() { return false; }
    @Override public boolean canBeLeashed(Player player) { return false; }
    @Override public boolean causeFallDamage(float distance, float multiplier, DamageSource source) { return false; }

    // Immune to Wither, same as the boss that effect is named after - keeps the fight from
    // being trivialized by stacking debuffs instead of actually fighting the thing.
    @Override public boolean canBeAffected(MobEffectInstance effectInstance) {
        return effectInstance.getEffect() != MobEffects.WITHER && super.canBeAffected(effectInstance);
    }

    // There's no overridable "I was just added to the level" method on Entity in vanilla,
    // so this is done the Forge way: listen for the event every entity fires when it joins
    // a level, and only act on it when the entity involved is one of our bosses.
    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof BaseHomesteadBoss boss) {
            boss.announceSpawnOnce();
        }
    }

    private void announceSpawnOnce() {
        if (!announcedSpawn) {
            announcedSpawn = true;
            broadcast(Component.translatable("message.homesteadbosses.spawn", getDisplayName()));
        }
    }

    @Override public void die(DamageSource source) {
        if (!level().isClientSide) {
            broadcast(Component.translatable("message.homesteadbosses.defeat", getDisplayName()));
        }
        super.die(source);
    }

    protected void broadcast(Component message) {
        if (level() instanceof ServerLevel server) {
            server.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("AnnouncedSpawn", announcedSpawn);
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AnnouncedSpawn")) announcedSpawn = tag.getBoolean("AnnouncedSpawn");
    }
}
