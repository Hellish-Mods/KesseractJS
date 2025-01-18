package dev.latvian.kubejs.client.asset;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import lombok.val;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LangEventJS extends EventJS {
	/**
	 * only matches lower case letters and underscore, for LangKey or ResourceLocation, or ...
	 */
	public static final Pattern PATTERN = Pattern.compile("[a-z_]+");

    public final Map<String, Map<String, LangEntry>> namespace2lang2entries;

	public LangEventJS() {
		namespace2lang2entries = new HashMap<>();
	}

    @NotNull
	public LangEntry get(String namespace, String lang) {
		if (namespace == null || lang == null || namespace.isEmpty() || lang.isEmpty() || !PATTERN.matcher(lang).matches()) {
			throw new IllegalArgumentException("Invalid namespace or lang: [" + namespace + ", " + lang + "]");
		}
		return namespace2lang2entries
            .computeIfAbsent(namespace, k -> new HashMap<>())
            .computeIfAbsent(lang, k -> new LangEntry(namespace, lang));
	}

	public LangEntry get(String lang) {
		return get(KubeJS.MOD_ID, lang);
	}

	public void renameItem(String lang, ItemStackJS stack, String name) {
		if (stack == null || stack.isEmpty()) {
			return;
		}
		val desc = stack.getItem().getDescriptionId();
		if (desc != null && !desc.isEmpty()) {
			get(stack.getMod(), lang).add(desc, name);
		}
	}

	public void renameBlock(String lang, Block block, String name) {
		if (block == null || block == Blocks.AIR) {
			return;
		}
		val desc = block.getDescriptionId();
		if (desc != null && !desc.isEmpty()) {
			val modid = Registry.BLOCK.getKey(block).getNamespace();
			get(modid, lang).add(desc, name);
		}
	}

	public void renameEntity(String lang, ResourceLocation id, String name) {
		get(id.getNamespace(), lang).add("entity." + id.getNamespace() + "." + id.getPath().replace('/', '.'), name);
	}

	public void renameBiome(String lang, ResourceLocation id, String name) {
		get(id.getNamespace(), lang).add("biome." + id.getNamespace() + "." + id.getPath().replace('/', '.'), name);
	}
}
