package dev.latvian.kubejs.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.client.asset.AtlasSpriteRegistryEventJS;
import dev.latvian.kubejs.client.painter.Painter;
import dev.latvian.kubejs.client.painter.world.WorldPaintEventJS;
import dev.latvian.kubejs.client.painter.world.WorldPainterObject;
import dev.latvian.kubejs.core.BucketItemKJS;
import dev.latvian.kubejs.core.ImageButtonKJS;
import dev.latvian.kubejs.fluid.FluidBuilder;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.events.ItemTooltipEventJS;
import dev.latvian.kubejs.item.OldItemTooltipEventJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.registry.BuilderBase;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.Tags;
import dev.latvian.kubejs.world.AttachWorldDataEvent;
import dev.latvian.kubejs.world.ClientWorldJS;
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.architectury.event.events.TextureStitchEvent;
import me.shedaniel.architectury.event.events.TooltipEvent;
import me.shedaniel.architectury.event.events.client.ClientLifecycleEvent;
import me.shedaniel.architectury.event.events.client.ClientPlayerEvent;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.hooks.ScreenHooks;
import me.shedaniel.architectury.registry.ColorHandlers;
import me.shedaniel.architectury.registry.RenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class KubeJSClientEventHandler {
	private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
	public static Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> staticItemTooltips = null;
	private final Map<ResourceLocation, TagInstance> tempTagNames = new LinkedHashMap<>();

	public void init() {
		ClientLifecycleEvent.CLIENT_SETUP.register(this::clientSetup);
		GuiEvent.DEBUG_TEXT_LEFT.register(this::debugInfoLeft);
		GuiEvent.DEBUG_TEXT_RIGHT.register(this::debugInfoRight);
		TooltipEvent.ITEM.register(this::itemTooltip);
		ClientTickEvent.CLIENT_POST.register(this::clientTick);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(this::loggedIn);
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(this::loggedOut);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(this::respawn);
		GuiEvent.RENDER_HUD.register(Painter.INSTANCE::inGameScreenDraw);
		GuiEvent.RENDER_POST.register(Painter.INSTANCE::guiScreenDraw);
		GuiEvent.INIT_POST.register(this::guiPostInit);
        TextureStitchEvent.PRE.register(this::preAtlasStitch);
		TextureStitchEvent.POST.register(this::postAtlasStitch);
	}

    private void preAtlasStitch(TextureAtlas atlas, Consumer<ResourceLocation> consumer) {
        var stitchEvent = new AtlasSpriteRegistryEventJS(consumer);
        for (var builder : RegistryInfos.FLUID) {
            if (builder instanceof FluidBuilder f) {
                stitchEvent.register(ResourceLocation.tryParse(f.flowingTexture));
                stitchEvent.register(ResourceLocation.tryParse(f.stillTexture));
            }
        }
        stitchEvent.post(ScriptType.CLIENT, KubeJSEvents.CLIENT_ATLAS_STITCH);
    }

    private void clientSetup(Minecraft minecraft) {
		renderLayers();
		blockColors();
		itemColors();
	}

	private void renderLayers() {
		for (var builder : RegistryInfos.BLOCK.objects.values()) {
            var bBuilder = (BlockBuilder) builder;
			switch (bBuilder.renderType) {
				case "cutout" -> RenderTypes.register(RenderType.cutout(), bBuilder.block);
				case "cutout_mipped" -> RenderTypes.register(RenderType.cutoutMipped(), bBuilder.block);
				case "translucent" -> RenderTypes.register(RenderType.translucent(), bBuilder.block);
				//default:
				//	RenderTypeLookup.setRenderLayer(block, RenderType.getSolid());
			}
		}
        for (var base : RegistryInfos.FLUID.objects.values()) {
            var builder = (FluidBuilder) base;
            switch (builder.renderType) {
                case "cutout" -> RenderTypes.register(RenderType.cutout(), builder.stillFluid);
                case "cutout_mipped" -> RenderTypes.register(RenderType.cutoutMipped(), builder.stillFluid);
                case "translucent" -> RenderTypes.register(RenderType.translucent(), builder.stillFluid);
            }
        }
	}

	private void debugInfoLeft(List<String> lines) {
		if (Minecraft.getInstance().player != null) {
			new DebugInfoEventJS(lines).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO_LEFT);
		}
	}

	private void debugInfoRight(List<String> lines) {
		if (Minecraft.getInstance().player != null) {
			new DebugInfoEventJS(lines).post(ScriptType.CLIENT, KubeJSEvents.CLIENT_DEBUG_INFO_RIGHT);
		}
	}

	private void itemTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag) {
		boolean advanced = flag.isAdvanced();

		if (advanced && ClientProperties.get().getShowTagNames() && Screen.hasShiftDown()) {
			for (ResourceLocation tag : Tags.byItemStack(stack)) {
				tempTagNames.computeIfAbsent(tag, TagInstance::new).item = true;
			}

			if (stack.getItem() instanceof BlockItem) {
				for (ResourceLocation tag : Tags.byBlock(((BlockItem) stack.getItem()).getBlock())) {
					tempTagNames.computeIfAbsent(tag, TagInstance::new).block = true;
				}
			}

			if (stack.getItem() instanceof BucketItemKJS) {
				for (ResourceLocation tag : Tags.byFluid(((BucketItemKJS) stack.getItem()).getFluidKJS())) {
					tempTagNames.computeIfAbsent(tag, TagInstance::new).fluid = true;
				}
			}

			if (stack.getItem() instanceof SpawnEggItem) {
				for (ResourceLocation tag : Tags.byEntityType(((SpawnEggItem) stack.getItem()).getType(stack.getTag()))) {
					tempTagNames.computeIfAbsent(tag, TagInstance::new).entity = true;
				}
			}

			for (TagInstance instance : tempTagNames.values()) {
				lines.add(instance.toText());
			}

			tempTagNames.clear();
		}

		if (staticItemTooltips == null) {
			staticItemTooltips = new HashMap<>();
			new ItemTooltipEventJS(staticItemTooltips).post(ScriptType.CLIENT, KubeJSEvents.ITEM_TOOLTIP);
		}

		for (ItemTooltipEventJS.StaticTooltipHandler h : staticItemTooltips.getOrDefault(Items.AIR, Collections.emptyList())) {
			h.tooltip(stack, advanced, lines);
		}

		for (ItemTooltipEventJS.StaticTooltipHandler h : staticItemTooltips.getOrDefault(stack.getItem(), Collections.emptyList())) {
			h.tooltip(stack, advanced, lines);
		}

		// TODO: Remove me
		new OldItemTooltipEventJS(stack, lines, advanced).post(ScriptType.CLIENT, "client.item_tooltip");
	}

	private void clientTick(Minecraft minecraft) {
		if (Minecraft.getInstance().player != null && ClientWorldJS.getInstance() != null) {
			new ClientTickEventJS().post(KubeJSEvents.CLIENT_TICK);
		}
	}

	private void loggedIn(LocalPlayer player) {
		ClientWorldJS.setInstance(new ClientWorldJS(Minecraft.getInstance(), player));
		new AttachWorldDataEvent(ClientWorldJS.getInstance()).invoke();
		new AttachPlayerDataEvent(ClientWorldJS.getInstance().clientPlayerData).invoke();
		new ClientLoggedInEventJS().post(KubeJSEvents.CLIENT_LOGGED_IN);
	}

	private void loggedOut(LocalPlayer player) {
		if (ClientWorldJS.getInstance() != null) {
			new ClientLoggedInEventJS().post(KubeJSEvents.CLIENT_LOGGED_OUT);
		}

		ClientWorldJS.setInstance(null);
		Painter.INSTANCE.clear();
	}

	private void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		ClientWorldJS.setInstance(new ClientWorldJS(Minecraft.getInstance(), newPlayer));
		new AttachWorldDataEvent(ClientWorldJS.getInstance()).invoke();
		new AttachPlayerDataEvent(ClientWorldJS.getInstance().clientPlayerData).invoke();
	}

    private void guiPostInit(Screen screen, List<AbstractWidget> widgets, List<GuiEventListener> children) {
		if (ClientProperties.get().getDisableRecipeBook() && screen instanceof RecipeUpdateListener) {
			Iterator<GuiEventListener> iterator = children.iterator();
			while (iterator.hasNext()) {
				GuiEventListener listener = iterator.next();
				if (listener instanceof AbstractWidget && listener instanceof ImageButtonKJS && RECIPE_BUTTON_TEXTURE.equals(((ImageButtonKJS) listener).getButtonTextureKJS())) {
					ScreenHooks.getButtons(screen).remove(listener);
					iterator.remove();
					return;
				}
			}
		}
	}

	private void itemColors() {
        for (BuilderBase<? extends Item> base : RegistryInfos.ITEM.objects.values()) {
            if (base instanceof ItemBuilder builder && builder.tint != null) {
                ColorHandlers.registerItemColors(
                    builder.tint.asItemColor(),
                    Objects.requireNonNull(builder.get(), "Item " + builder.id + " is null!")
                );
            }
        }

        for (BuilderBase<? extends Block> o : RegistryInfos.BLOCK.objects.values()) {
            if (o instanceof BlockBuilder builder
                && builder.itemBuilder != null
                && !builder.color.isEmpty()) {
                ColorHandlers.registerItemColors(
                    (stack, index) -> builder.color.get(index),
                    Objects.requireNonNull(
                        builder.itemBuilder.blockItem,
                        "Block Item " + builder.id + " is null!"
                    )
                );
            }
        }

        for (var base : RegistryInfos.FLUID.objects.values()) {
            if (base instanceof FluidBuilder builder && builder.bucketColor != 0xFFFFFFFF) {
                ColorHandlers.registerItemColors(
                    (stack, index) -> index == 1 ? builder.bucketColor : 0xFFFFFFFF,
                    Objects.requireNonNull(builder.bucketItem, "Bucket Item " + builder.id + " is null!")
                );
            }
        }
	}

	private void blockColors() {
        RegistryInfos.BLOCK.objects.values()
            .stream().map(o -> (BlockBuilder) o)
            .filter(builder -> !builder.color.isEmpty())
            .forEach(builder -> ColorHandlers.registerBlockColors((state, world, pos, index) -> builder.color.get(index), builder.block));
	}

	private void postAtlasStitch(TextureAtlas atlas) {
		if (!ClientProperties.get().getExportAtlases()) {
			return;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.getId());
		int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

		if (w <= 0 || h <= 0) {
			return;
		}

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[w * h];

		IntBuffer result = BufferUtils.createIntBuffer(w * h);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, result);
		result.get(pixels);

		image.setRGB(0, 0, w, h, pixels, 0, w);

		Path path = KubeJSPaths.EXPORTED.resolve(atlas.location().getNamespace() + "/" + atlas.location().getPath());

		if (!Files.exists(path.getParent())) {
			try {
				Files.createDirectories(path.getParent());
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		try (OutputStream stream = Files.newOutputStream(path)) {
			ImageIO.write(image, "PNG", stream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void renderWorldLast(PoseStack ps, float delta) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.player == null) {
			return;
		}

		// RenderSystem.enableBlend();
		// RenderSystem.disableLighting();

		WorldPaintEventJS event = new WorldPaintEventJS(mc, ps, delta);
		event.post(KubeJSEvents.CLIENT_PAINT_WORLD);

		for (WorldPainterObject object : Painter.INSTANCE.getWorldObjects()) {
			if (object.visible) {
				object.preDraw(event);
			}
		}

		for (WorldPainterObject object : Painter.INSTANCE.getWorldObjects()) {
			if (object.visible) {
				object.draw(event);
			}
		}
	}
}