package net.homestead.bosses.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WheatHarvesterEntity extends BaseHomesteadBoss {
    private int rootCooldown = 120, spinCooldown = 240;

    public WheatHarvesterEntity(EntityType<? extends WheatHarvesterEntity> type, Level level) {
        super(type, level, "wheat_harvester", BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
        this.xpReward = 100;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 600)
            .add(Attributes.MOVEMENT_SPEED, .22)
            .add(Attributes.ATTACK_DAMAGE, 22)
            .add(Attributes.ARMOR, 20)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1)
            .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide || getTarget() == null) return;
        LivingEntity target = getTarget();
        if (--rootCooldown <= 0) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
            if (level() instanceof ServerLevel server) {
                BlockPos p = target.blockPosition();
                // Cosmetic particles only: does not alter the player's terrain.
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, p.getX() + .5, p.getY() + .2, p.getZ() + .5, 24, .8, .2, .8, .02);
            }
            rootCooldown = 220;
        }
        if (--spinCooldown <= 0 && level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, getX(), getY() + 1, getZ(), 16, 2, .6, 2, 0);
            level().playSound(null, this, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.3F, 0.6F);
            for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(4.5))) {
                if (victim == this || victim instanceof WheatHarvesterEntity) continue;
                victim.hurt(damageSources().mobAttack(this), 12.0F);
                Vec3 push = victim.position().subtract(position()).normalize().scale(1.25).add(0, .35, 0);
                victim.setDeltaMovement(push);
                victim.hurtMarked = true;
            }
            spinCooldown = 300;
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.HUSK_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.HUSK_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.HUSK_DEATH; }
    @Override protected float getVoicePitch() { return 0.65F; }
}
