package dev.latvian.kubejs.script.data;

import net.minecraft.server.packs.PackResources;

import java.io.IOException;
import java.nio.file.Path;

public interface ExportablePackResources extends PackResources {
    String METADATA_EXTENSION = ".mcmeta";
    String PACK_META = "pack" + METADATA_EXTENSION;

	void export(Path root) throws IOException;
}