package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.bindings.RarityWrapper;
import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.registry.RegistryInfos;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

public class RecordItemJS extends RecordItem {
	public static class Builder extends ItemBuilder {
		public transient ResourceLocation song;
		public transient SoundEvent songSoundEvent;
//		public transient int length;
		public transient int analogOutput;

		public Builder(ResourceLocation i) {
			super(i);
			song = new ResourceLocation("minecraft:music_disc.11");
//			length = 71;
			analogOutput = 1;
			maxStackSize(1);
			rarity(RarityWrapper.RARE);
		}

        @JSInfo(value = """
            Sets the song that will play when this record is played.
            """)
        public Builder song(
            @JSInfo("The location of sound event.") ResourceLocation s
        ) {
            song = s;
            songSoundEvent = null;
            return this;
        }

		@JSInfo("Sets the redstone output of the jukebox when this record is played.")
		public Builder analogOutput(int o) {
			analogOutput = o;
			return this;
		}

		@Override
		public Item createObject() {
			return new RecordItemJS(this, analogOutput, this.songSoundEvent, createItemProperties());
		}

		public SoundEvent getSoundEvent() {
			if (songSoundEvent == null) {
				songSoundEvent = RegistryInfos.SOUND_EVENT.getValue(song);

				if (songSoundEvent == null || songSoundEvent == SoundEvents.ITEM_PICKUP) {
					songSoundEvent = SoundEvents.MUSIC_DISC_11;
				}
			}

			return songSoundEvent;
		}
	}

	private final Builder builder;

	public RecordItemJS(Builder b, int analogOutput, SoundEvent song, Properties properties) {
		super(analogOutput, song, properties);
		builder = b;
	}

	@Override
	public SoundEvent getSound() {
		return builder.getSoundEvent();
	}

//	public int getLengthInTicks() {
//		return builder.length * 20;
//	}
}