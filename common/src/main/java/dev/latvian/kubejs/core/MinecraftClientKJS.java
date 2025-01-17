package dev.latvian.kubejs.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.latvian.kubejs.client.ClientProperties;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

/**
 * @author ZZZank
 */
public interface MinecraftClientKJS {

	default Minecraft kjs$self() {
		return (Minecraft) this;
	}

	default Component kjs$getName() {
		return new TextComponent(kjs$self().name());
	}

	@Nullable
	default Screen kjs$getCurrentScreen() {
		return kjs$self().screen;
	}

	default void kjs$setCurrentScreen(Screen gui) {
		kjs$self().setScreen(gui);
	}

	default void kjs$setTitle(String t) {
		ClientProperties.get().title = t.trim();
		kjs$self().updateTitle();
	}

	default String kjs$getCurrentWorldName() {
		val server = kjs$self().getCurrentServer();
		return server == null ? "Singleplayer" : server.name;
	}

	default boolean kjs$isKeyDown(int key) {
		return InputConstants.isKeyDown(kjs$self().getWindow().getWindow(), key);
	}

	default boolean kjs$isShiftDown() {
		return Screen.hasShiftDown();
	}

	default boolean kjs$isCtrlDown() {
		return Screen.hasControlDown();
	}

	default boolean kjs$isAltDown() {
		return Screen.hasAltDown();
	}
}
