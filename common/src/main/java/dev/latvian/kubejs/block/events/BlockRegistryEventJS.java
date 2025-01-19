package dev.latvian.kubejs.block.events;

import dev.latvian.kubejs.block.BlockBuilder;
import dev.latvian.kubejs.registry.RegistryEventJS;
import dev.latvian.kubejs.registry.RegistryInfos;
import lombok.val;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BlockRegistryEventJS extends RegistryEventJS<Block> {

    public BlockRegistryEventJS() {
        super(RegistryInfos.BLOCK);
    }

    public void create(String name, Consumer<BlockBuilder> callback) {
		val builder = create(name);
		callback.accept(builder);
	}

    @Override
    public BlockBuilder create(String id, String type) {
        return (BlockBuilder) super.create(id, type);
    }

    @Override
    public BlockBuilder create(String id) {
        return (BlockBuilder) super.create(id);
    }

    public void addDetector(String id) {
        create(id, "detector");
	}
}