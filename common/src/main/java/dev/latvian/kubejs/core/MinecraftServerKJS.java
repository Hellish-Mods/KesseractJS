package dev.latvian.kubejs.core;

import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.server.ServerResources;

public interface MinecraftServerKJS extends AsKJS<ServerJS> {
	@Override
	default ServerJS asKJS() {
		return ServerJS.instance;
	}

	ServerResources getServerResourcesKJS();
}
