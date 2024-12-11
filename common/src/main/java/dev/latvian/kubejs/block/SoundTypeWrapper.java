package dev.latvian.kubejs.block;

import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.Undefined;
import dev.latvian.mods.rhino.util.remapper.RemapperManager;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import lombok.val;
import net.minecraft.world.level.block.SoundType;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class SoundTypeWrapper implements TypeWrapperFactory<SoundType> {
	public static final SoundTypeWrapper INSTANCE = new SoundTypeWrapper();

	private Map<String, SoundType> map;

	public Map<String, SoundType> getMap() {
		if (map == null) {
			map = new LinkedHashMap<>();
//			map.put("empty", SoundType.EMPTY);

            for (val field : SoundType.class.getFields()) {
                if (field.getType() == SoundType.class
                    && Modifier.isPublic(field.getModifiers())
                    && Modifier.isStatic(field.getModifiers())
                ) {
                    try {
                        val name = RemapperManager.getDefault().remapField(SoundType.class, field);
                        map.put((name.isEmpty() ? field.getName() : name).toLowerCase(), (SoundType) field.get(null));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
		}

		return map;
	}

	@Override
	public SoundType wrap(Object o) {
		if (o instanceof SoundType t) {
			return t;
		} else if (o == null || Undefined.isUndefined(o)) {
			return null;
		}
        val name = (o instanceof JsonElement j ? j.getAsString() : o.toString()).toLowerCase();
        return getMap().get(name);
    }
}
