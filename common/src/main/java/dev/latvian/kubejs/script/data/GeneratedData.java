package dev.latvian.kubejs.script.data;

import com.google.gson.JsonObject;
import lombok.val;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.Lazy;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public record GeneratedData(ResourceLocation id, Lazy<byte[]> data) implements Supplier<InputStream> {
	public static final GeneratedData INTERNAL_RELOAD = of(
        KubeJS.id("__internal.reload"),
        () -> new byte[0]
    );

	public static final GeneratedData PACK_META = of(KubeJS.id("pack.mcmeta"), () -> {
		val json = new JsonObject();
		val pack = new JsonObject();
		pack.addProperty("description", "KubeJS Pack");
		pack.addProperty("pack_format", 15);
		json.add("pack", pack);
		return json.toString().getBytes(StandardCharsets.UTF_8);
	});

	public static final GeneratedData PACK_ICON = of(KubeJS.id("textures/kubejs_logo.png"), () -> {
		try (val logoStream = GeneratedData.class.getResourceAsStream("/kubejs_logo.png")) {
//            return Files.readAllBytes(Platform.getMod(KubeJS.MOD_ID).findResource("assets", "kubejs", "textures", "kubejs_logo.png").get());
            if (logoStream != null) {
                return logoStream.readAllBytes();
            }
        } catch (Exception ex) {
			ex.printStackTrace();
		}
        return new byte[0];
	});

    public static GeneratedData of(ResourceLocation id, Supplier<byte[]> data) {
        return new GeneratedData(id, Lazy.of(data));
    }

    public static GeneratedData of(String namespace, String path, Supplier<byte[]> data) {
        return new GeneratedData(new ResourceLocation(namespace, path), Lazy.of(data));
    }

	@Override
	@NotNull
	public InputStream get() {
		return new ByteArrayInputStream(data.get());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GeneratedData g && id.equals(g.id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
