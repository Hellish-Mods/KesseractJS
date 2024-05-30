package dev.latvian.kubejs.script.fabric;

import dev.latvian.kubejs.script.PlatformWrapper;
import net.fabricmc.loader.api.FabricLoader;

/**
 * @see dev.latvian.kubejs.script.PlatformWrapper
 * @author ZZZank
 */
public class PlatformWrapperImpl {

	public static boolean setModName(PlatformWrapper.ModInfo info, String newName) {
		try {
			var mc = FabricLoader.getInstance().getModContainer(info.getId());

			if (mc.isPresent()) {
				var meta = mc.get().getMetadata();
				var field = meta.getClass().getDeclaredField("name");
				field.setAccessible(true);
				field.set(meta, newName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
