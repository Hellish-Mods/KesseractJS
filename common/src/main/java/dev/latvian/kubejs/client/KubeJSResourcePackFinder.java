package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class KubeJSResourcePackFinder implements RepositorySource {
	@Override
	public void loadPacks(Consumer<Pack> nameToPackMap, Pack.PackConstructor packInfoFactory) {
		if (Files.notExists(KubeJSPaths.ASSETS)) {
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS));
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS.resolve("kubejs/textures/block")));
			UtilsJS.tryIO(() -> Files.createDirectories(KubeJSPaths.ASSETS.resolve("kubejs/textures/item")));

			try (val in = KubeJS.class.getResourceAsStream("/data/kubejs/example_block_texture.png");
                 val out = Files.newOutputStream(KubeJSPaths.ASSETS.resolve("kubejs/textures/block/example_block.png"))) {
                in.transferTo(out);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try (val in = KubeJS.class.getResourceAsStream("/data/kubejs/example_item_texture.png");
				 val out = Files.newOutputStream(KubeJSPaths.ASSETS.resolve("kubejs/textures/item/example_item.png"))) {
				in.transferTo(out);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Moved pack to mixin
	}
}