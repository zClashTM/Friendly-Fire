package net.darkhax.friendlyfire.mixin;

import net.darkhax.friendlyfire.FriendlyFireCommon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void onLivingHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cbi) {

        LivingEntity self = (LivingEntity) (Object) this;

        // Verifica se a fonte de dano é uma entidade viva
        if (source.getEntity() instanceof LivingEntity trueSource) {
            // Se a entidade que causa dano for um jogador
            if (trueSource instanceof Player) {
                Player player = (Player) trueSource;

                // Obter a equipe do jogador e da entidade alvo
                Team playerTeam = player.getTeam();
                Team targetTeam = self.getTeam();

                // Se ambos estão na mesma equipe, cancela o ataque
                if (playerTeam != null && playerTeam == targetTeam) {
                    self.setLastHurtByMob(null);
                    trueSource.setLastHurtByMob(null);
                    cbi.setReturnValue(false);
                    return;
                }
            }
        }

        // Chamando o método original para o comportamento padrão
        if (FriendlyFireCommon.preventAttack(self, source, amount)) {
            self.setLastHurtByMob(null);
            cbi.setReturnValue(false);
        }
    }
}
