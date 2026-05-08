package com.rizor1x.industry.client;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.item.custom.JetpackItem;
import com.rizor1x.industry.network.JetpackPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Industry.MODID, value = Dist.CLIENT)
public class JetpackClientEvents {

    @SubscribeEvent
    public static void onClientTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Minecraft mc = Minecraft.getInstance();

        if (player != mc.player || player.isSpectator()) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

        if (chest.getItem() instanceof JetpackItem jetpack) {
            int energy = jetpack.getEnergy(chest);

            // 1. ЛОГИКА КВАНТА (Креативный полет)
            if (jetpack.isCreative) {
                if (energy >= jetpack.energyPerTick) {
                    player.getAbilities().mayfly = true;
                    if (player.getAbilities().flying) {
                        PacketDistributor.sendToServer(new JetpackPacket(true));
                    }
                } else {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                }
                player.onUpdateAbilities();
            }
            // 2. ЛОГИКА ОБЫЧНОГО ДЖЕТПАКА (Идеальная физика Iron Jetpacks)
            else {
                if (energy >= jetpack.energyPerTick && mc.options.keyJump.isDown() && !player.getAbilities().flying) {

                    PacketDistributor.sendToServer(new JetpackPacket(true));

                    var currentMotion = player.getDeltaMovement();
                    double motionY = currentMotion.y;

                    // СЕКРЕТ ПЛАВНОГО ПОЛЕТА:
                    // Если мы падаем (motionY < 0), мы резко срезаем скорость падения на 40% каждый тик!
                    // Это позволяет мгновенно восстанавливать высоту после свободного падения.
                    if (motionY < 0) {
                        motionY *= 0.6D;
                    }

                    // Добавляем тягу нашего джетпака
                    motionY += jetpack.thrust;

                    // Ограничитель скорости (чтобы не улетать в космос слишком быстро)
                    if (motionY > jetpack.maxSpeedY) {
                        motionY = jetpack.maxSpeedY;
                    }

                    player.setDeltaMovement(currentMotion.x, motionY, currentMotion.z);
                    player.fallDistance = 0; // Сбрасываем урон
                }
            }
        }
        else if (player.getAbilities().mayfly && !player.isCreative()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }
}