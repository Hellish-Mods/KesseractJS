package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.UtilsWrapper;
import dev.latvian.kubejs.client.toast.NotificationData;
import lombok.val;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

public class NotificationMessage extends BaseS2CMessage {
	private final NotificationData data;

	public NotificationMessage(NotificationData data) {
		this.data = data;
	}

	NotificationMessage(FriendlyByteBuf buf) {
        NotificationData got;
        try {
            got = buf.readWithCodec(NotificationData.CODEC);
        } catch (IOException e) {
            KubeJS.LOGGER.error("Error when reading NotificationData from network message", e);
            got = null;
        }
        data = got;
    }

	@Override
	public MessageType getType() {
		return KubeJSNet.NOTIFICATION;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
        try {
            buf.writeWithCodec(NotificationData.CODEC, this.data);
        } catch (IOException e) {
            KubeJS.LOGGER.error("Error when writing NotificationData to network message", e);
        }
    }

	@Override
	public void handle(PacketContext context) {
        if (data == null) {
            return;
        }
        val player = UtilsWrapper.getClientWorld().getPlayer(KubeJS.PROXY.getClientPlayer());
		if (player == null) {
			return;
		}
        player.notify(data);
	}
}