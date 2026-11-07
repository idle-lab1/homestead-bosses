package net.homestead.bosses.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Disposable reinforcements summoned by the Pumpkin King. Deliberately NOT a
 * BaseHomesteadBoss: no boss bar, no despawn immunity, no leash/fall-damage immunity -
 * these are meant to behave like an ordinary hostile mob, not a second boss.
 */
public class PumpkinMinionEntity extends Monster {
    public PumpkinMinionEntity(EntityType<? extends PumpkinMinionEntity> type, Level level) { super(type, level); }

    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24)
            .add(Attributes.MOVEMENT_SPEED, .29)
            .add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ZOMBIE_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ZOMBIE_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ZOMBIE_DEATH; }
    @Override protected float getVoicePitch() { return 1.5F; }
}
