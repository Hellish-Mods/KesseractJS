package dev.latvian.kubejs.script.forge;

import dev.latvian.kubejs.script.PlatformWrapper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

/**
 * @see dev.latvian.kubejs.script.PlatformWrapper
 * @author ZZZank
 */
public class PlatformWrapperImpl {

	public static boolean setModName(PlatformWrapper.ModInfo info, String newName) {
		try {
			var mod = ModList.get().getModContainerById(info.getId());

			if (mod.isPresent() && mod.get().getModInfo() instanceof ModInfo i) {
				var field = ModInfo.class.getDeclaredField("displayName");
				field.setAccessible(true);
				field.set(i, newName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
