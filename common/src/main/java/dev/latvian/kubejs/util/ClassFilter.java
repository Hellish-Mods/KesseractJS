package dev.latvian.kubejs.util;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.mods.rhino.ClassShutter;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassFilter implements ClassShutter {
	private static final byte V_DEF = -1;
	private static final byte V_DENY = 0;
	private static final byte V_ALLOW = 1;

	private final Set<String> denyStrong;
	private final List<String> denyWeak;
	private final Set<String> allowStrong;
	private final List<String> allowWeak;
	private final Object2ByteOpenHashMap<String> cache;

	public ClassFilter() {
		denyStrong = new HashSet<>();
		denyWeak = new ArrayList<>();
		allowStrong = new HashSet<>();
		allowWeak = new ArrayList<>();
		cache = new Object2ByteOpenHashMap<>();
		cache.defaultReturnValue(V_DEF);
	}

	public void deny(String s) {
		if ((s = s.trim()).isEmpty()) {
			return;
		}

		denyStrong.add(s);

		if (!denyWeak.contains(s)) {
			denyWeak.add(s);
		}
	}

	public void deny(Class<?> c) {
		deny(c.getName());
	}

	public void allow(String s) {
		if ((s = s.trim()).isEmpty()) {
			return;
		}

		allowStrong.add(s);

		if (!allowWeak.contains(s)) {
			allowWeak.add(s);
		}
	}

	public void allow(Class<?> c) {
		allow(c.getName());
	}

	private byte isAllowed0(String s) {
		if (denyStrong.contains(s)) {
			return V_DENY;
		}

		if (allowStrong.contains(s)) {
			return V_ALLOW;
		}

		for (String s1 : denyWeak) {
			if (s.startsWith(s1)) {
				return V_DENY;
			}
		}

		for (String s1 : allowWeak) {
			if (s.startsWith(s1)) {
				return V_ALLOW;
			}
		}

		return CommonProperties.get().invertClassLoader ? V_ALLOW : V_DENY;
	}

	public boolean isAllowed(String s) {
		byte b = cache.getByte(s);

		if (b == V_DEF) {
			b = isAllowed0(s);
			cache.put(s, b);
		}

		return b == V_ALLOW;
	}

    @Override
    public boolean visibleToScripts(String fullClassName, int type) {
        return type != ClassShutter.TYPE_CLASS_IN_PACKAGE || isAllowed(fullClassName);
    }
}
