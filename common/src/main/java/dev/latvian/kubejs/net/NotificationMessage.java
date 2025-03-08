package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.UtilsWrapper;
import dev.latvian.kubejs.client.toast.NotificationBuilder;
import lombok.val;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

public class NotificationMessage extends BaseS2CMessage {
	private final NotificationBuilder notification;

	public NotificationMessage(NotificationBuilder notification) {
		this.notification = notification;
	}

	NotificationMessage(FriendlyByteBuf buf) {
		notification = new NotificationBuilder(buf);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.NOTIFICATION;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		notification.write(buf);
	}

	@Override
	public void handle(PacketContext context) {
        val player = UtilsWrapper.getClientWorld().getPlayer(KubeJS.PROXY.getClientPlayer());
		if (player == null) {
			return;
		}
        player.notify(notification);
	}
}