package dev.latvian.kubejs.registry.types.tab;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import lombok.val;
import me.shedaniel.architectury.registry.CreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class CreativeTabRegistryEventJS extends EventJS {

    @JSInfo("""
			register a CreativeModeTab. Its namespace will always be `kubejs`
			
			vanilla id is valid, but will be denied here to prevent confusion
			""")
	public CreativeModeTab create(String id, Supplier<ItemStackJS> icon) {
        ResourceLocation fullId;
        try {
            fullId = KubeJS.rl(id);
        } catch (Exception e) {
            ConsoleJS.STARTUP.error(String.format("Tab id '%s' is not valid!", id), e);
            return null;
        }

		if (KjsTabs.has(id)) {
			ConsoleJS.STARTUP.errorf("Tab with id '%s' already registered!", id);
			return KjsTabs.get(id);
		}

		val tab = CreativeTabs.create(fullId, () -> icon.get().getItemStack());
		KjsTabs.put(id, tab);

		return tab;
	}
}
