package dev.latvian.kubejs.client.toast.icon;

import com.mojang.serialization.Codec;

/**
 * @author ZZZank
 */
public interface ToastIconType {

    int index();

    Codec<? extends ToastIcon> codec();
}
