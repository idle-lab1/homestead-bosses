package net.homestead.bosses.entity;

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

public class BakersBaneEntity extends BaseHomesteadBoss {
    private int slamCooldown = 140;

    public BakersBaneEntity(EntityType<? extends BakersBaneEntity> type, Level level) {
        super(type, level, "bakers_bane", BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.NOTCHED_12);
        this.xpReward = 75;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 450)
            .add(Attributes.MOVEMENT_SPEED, .23)
            .add(Attributes.ATTACK_DAMAGE, 18)
            .add(Attributes.ARMOR, 16)
            .add(Attributes.KNOCKBACK_RESISTANCE, .85)
            .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide || getTarget() == null) return;
        if (--slamCooldown <= 0 && level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CLOUD, getX(), getY() + .5, getZ(), 90, 2.8, .5, 2.8, .08);
            server.sendParticles(ParticleTypes.CRIT, getX(), getY() + .8, getZ(), 45, 2, .8, 2, .12);
            level().playSound(null, this, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.4F, 0.7F);
            for (LivingEntity victim : server.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(5.5))) {
                if (victim == this || victim instanceof BakersBaneEntity) continue;
                victim.hurt(damageSources().mobAttack(this), 14.0F);
                Vec3 push = victim.position().subtract(position()).normalize().scale(1.5).add(0, .45, 0);
                victim.setDeltaMovement(push);
                victim.hurtMarked = true;
                victim.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 70, 0));
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            }
            slamCooldown = 200;
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.RAVAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
}
