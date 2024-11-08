package dev.latvian.kubejs.forge.mods;

import lombok.val;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author ZZZank
 */
@Mod(DummyEventJS.MOD_ID)
public class DummyEventJS {
    public static final String MOD_ID = "eventjs";

    static IEventBus MOD_BUS;

    public DummyEventJS() {
        val modLoadingContext = FMLJavaModLoadingContext.get();
        MOD_BUS = modLoadingContext == null
            ? null
            : modLoadingContext.getModEventBus();
    }

    public static IEventBus selectBus(Class<? extends Event> eventType) {
        return IModBusEvent.class.isAssignableFrom(eventType)
            ? MOD_BUS
            : MinecraftForge.EVENT_BUS;
    }
}
