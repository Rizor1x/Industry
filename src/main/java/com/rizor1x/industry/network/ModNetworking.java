package com.rizor1x.industry.network;

import com.rizor1x.industry.Industry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Industry.MODID);
        // Говорим игре: мы разрешаем клиентам отправлять пакет JetpackPacket на сервер
        registrar.playToServer(JetpackPacket.TYPE, JetpackPacket.CODEC, JetpackPacket::handle);
    }
}