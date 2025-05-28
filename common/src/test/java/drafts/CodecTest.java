package drafts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;
import dev.latvian.kubejs.client.toast.NotificationData;
import dev.latvian.kubejs.item.ItemStackJS;
import lombok.val;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class CodecTest {
    public static final Logger LOGGER = LogManager.getLogger("codec-test");
    public static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    @Test
    public void sameAfterEncodeAndDecode() {
        val data = NotificationData.ofTitles(
            new TextComponent("wellwellwell"),
            new TextComponent("井井井")
        )
            // there's no builtin `.equals()` for Color implementations
//            .borderColor(new SimpleColorWithAlpha(1234))
//            .backgroundColor(new SimpleColorWithAlpha(5678))
            .itemIcon(ItemStackJS.of(Items.BAMBOO)
                .withName(new TextComponent("someRandomName"))
                .getItemStack()
            );
        val encoded = NotificationData.CODEC
            .encodeStart(JsonOps.INSTANCE, data)
            .result()
            .orElseThrow();
        LOGGER.info(GSON.toJson(encoded));
        val decoded = NotificationData.CODEC
            .decode(JsonOps.INSTANCE, encoded)
            .result()
            .orElseThrow()
            .getFirst();
        Assertions.assertEquals(data.icon(), decoded.icon());
        Assertions.assertEquals(decoded, data);
    }
}
