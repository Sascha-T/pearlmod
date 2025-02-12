package tako.sucks.pearlmod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.UUID;

@Mixin(ThrownEnderpearl.class)
public class EnderPearlMixin {
    @Unique
    public UUID savedUUID;

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
    public void inject$ctor(Level level, LivingEntity livingEntity, CallbackInfo ci) {
        this.savedUUID = livingEntity.getUUID();
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;level()Lnet/minecraft/world/level/Level;", ordinal = 0))
    private Level redirect$level(ServerPlayer instance) {
        return ((ThrownEnderpearl)(Object)(this)).level();
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(DDD)V"))
    private void redirect$teleportTo(Entity instance, double d, double e, double f) {
        Level to = ((ThrownEnderpearl)(Object)(this)).level(); // client side check is passed atp but whatever
        if(to instanceof ServerLevel l) {
            instance.teleportTo(l, d, e, f, new HashSet<>(), instance.yRotO, instance.xRotO);
        }
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownEnderpearl;getOwner()Lnet/minecraft/world/entity/Entity;"))
    public Entity redirect$getOwner(ThrownEnderpearl instance) {
        var x = instance.getOwner();
        if(x != null)
            return x;
        if(instance.level() instanceof ServerLevel level) {
            return level.getServer().getPlayerList().getPlayer(savedUUID);
        }
        return null;
    }
}
