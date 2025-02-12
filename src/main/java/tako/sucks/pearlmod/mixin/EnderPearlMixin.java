package tako.sucks.pearlmod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;

@Mixin(ThrownEnderpearl.class)
public class EnderPearlMixin {
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;level()Lnet/minecraft/world/level/Level;", ordinal = 0))
    private Level redirect$level(ServerPlayer instance) {
        return ((ThrownEnderpearl)(Object)(this)).level();
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(DDD)V"))
    private void redirect$tp(Entity instance, double d, double e, double f) {
        Level to = ((ThrownEnderpearl)(Object)(this)).level(); // client side check is passed atp but whatever
        if(to instanceof ServerLevel l) {
            instance.teleportTo(l, d, e, f, new HashSet<>(), instance.xRotO, instance.yRotO);
        }
    }
}
