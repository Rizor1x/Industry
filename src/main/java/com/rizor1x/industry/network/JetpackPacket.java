package com.rizor1x.industry.network;

import com.rizor1x.industry.Industry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

// Этот пакет просто говорит серверу: "Игрок жмет пробел!"
public record JetpackPacket(boolean isFlying) implements CustomPacketPayload {

    public static final Type<JetpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Industry.MODID, "jetpack_fly"));

    public static final StreamCodec<FriendlyByteBuf, JetpackPacket> CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.BOOL, JetpackPacket::isFlying,
            JetpackPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // ЛОГИКА НА СЕРВЕРЕ (ТРАТИМ ЭНЕРГИЮ)
    public static void handle(JetpackPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!payload.isFlying()) return;

            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof com.rizor1x.industry.item.custom.JetpackItem jetpack) {
                int energy = jetpack.getEnergy(chest);
                if (energy >= jetpack.energyPerTick) {
                    jetpack.setEnergy(chest, energy - jetpack.energyPerTick);
                    player.fallDistance = 0; // Защита от падения во время тяги!
                }
            }
        });
    }
}