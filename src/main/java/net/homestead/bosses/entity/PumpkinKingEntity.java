package net.homestead.bosses.entity;

import net.homestead.bosses.HomesteadBosses;
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
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PumpkinKingEntity extends BaseHomesteadBoss {
    private int fireballCooldown = 100, minionCooldown = 0;

    public PumpkinKingEntity(EntityType<? extends PumpkinKingEntity> type, Level level) {
        super(type, level, "pumpkin_king", BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 350)
            .add(Attributes.MOVEMENT_SPEED, .26)
            .add(Attributes.ATTACK_DAMAGE, 14)
            .add(Attributes.ARMOR, 12)
            .add(Attributes.KNOCKBACK_RESISTANCE, .7)
            .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide || getTarget() == null) return;
        if (--fireballCooldown <= 0) {
            LivingEntity target = getTarget();
            Vec3 aim = target.getEyePosition().subtract(position().add(0, 2.1, 0)).normalize();
            LargeFireball ball = new LargeFireball(level(), this, aim.x, aim.y, aim.z, 1);
            ball.setPos(getX() + aim.x * 3.0, getY() + 2.1, getZ() + aim.z * 3.0);
            level().addFreshEntity(ball);
            level().playSound(null, this, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5F, 0.85F);
            fireballCooldown = 160;
        }
        if (isBelowHalfHealth() && --minionCooldown <= 0 && level() instanceof ServerLevel server) {
            level().playSound(null, this, SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 1.3F, 0.9F);
            for (int i = 0; i < 2; i++) {
                PumpkinMinionEntity minion = HomesteadBosses.PUMPKIN_MINION.get().create(server);
                if (minion != null) {
                    minion.moveTo(getX() + (random.nextDouble() - 0.5) * 5, getY(), getZ() + (random.nextDouble() - 0.5) * 5, random.nextFloat() * 360, 0);
                    server.addFreshEntity(minion);
                }
            }
            minionCooldown = 360;
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.BLAZE_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.BLAZE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLAZE_DEATH; }
    @Override public float getVoicePitch() { return 0.75F; }
}
