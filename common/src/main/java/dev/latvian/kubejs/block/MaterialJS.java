package dev.latvian.kubejs.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

/**
 * @author LatvianModder
 */
@Getter
@AllArgsConstructor
public class MaterialJS {
	private final String id;
	private final Material minecraftMaterial;
	private final SoundType sound;

    public MaterialColor getColor() {
        return minecraftMaterial.getColor();
    }
}