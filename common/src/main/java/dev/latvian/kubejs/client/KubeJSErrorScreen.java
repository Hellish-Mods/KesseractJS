package dev.latvian.kubejs.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.CommonProperties;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.util.Mth;

import java.util.Objects;

public class KubeJSErrorScreen extends Screen {
	public final ScriptType type;
	private MultiLineLabel multilineMessage;

	public KubeJSErrorScreen(ScriptType type) {
		super(new TextComponent(""));
		this.type = type;
		this.multilineMessage = MultiLineLabel.EMPTY;
	}

	@Override
	public String getNarrationMessage() {
		return "There were KubeJS startup errors!";
	}

	@Override
	protected void init() {
		super.init();

        var formatted = new TextComponent("");
        formatted.append(
            new TextComponent("There were KubeJS startup errors ")
                .append(new TextComponent("[" + type.errors.size() + "]").withStyle(ChatFormatting.DARK_RED))
                .append("!")
        );

		val style = Style.EMPTY.withColor(TextColor.fromRgb(0xD19893));

        {
            var i = 0;
            for (val error : type.errors) {
                if (i != 0) {
                    formatted.append("\n");
                }
                formatted.append(
                    new TextComponent((i + 1) + ") ").withStyle(ChatFormatting.DARK_RED)
                        .append(
                            new TextComponent(error
                                .replace("Error occurred while handling event ", "Error in ")
                                .replace("dev.latvian.mods.kubejs.", "...")
                            )
                                .withStyle(style)));
                i++;
            }
        }

		this.multilineMessage = MultiLineLabel.create(this.font, formatted, this.width - 12);
		int i = this.height - 26;

		if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
			this.addWidget(new Button(this.width / 2 - 155, i, 150, 20, new TextComponent("Open startup.log"), this::openLog));
			this.addWidget(new Button(this.width / 2 - 155 + 160, i, 150, 20, new TextComponent("Quit"), this::quit));
		} else {
			this.addWidget(new Button(this.width / 4 - 55, i, 100, 20, new TextComponent("Open startup.log"), this::openLog));
			this.addWidget(new Button(this.width / 2 - 50, i, 100, 20, new TextComponent("Report"), this::report));
			this.addWidget(new Button(this.width * 3 / 4 - 45, i, 100, 20, new TextComponent("Quit"), this::quit));
		}
	}

	private void quit(Button button) {
        if (minecraft == null) {
            minecraft = Minecraft.getInstance();
            if (minecraft == null) {
                throw new IllegalStateException("Minecraft instance not exist, try to exit by throwing an exception");
            }
        }
        minecraft.stop();
    }

	private void report(Button button) {
		handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CommonProperties.get().startupErrorReportUrl)));
	}

	private void openLog(Button button) {
		handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, type.getLogFile().toAbsolutePath().toString())));
	}

	@Override
	public void render(PoseStack stack, int i, int j, float f) {
		this.renderBackground(stack);
		this.multilineMessage.renderCentered(stack, this.width / 2, this.messageTop());
		super.render(stack, i, j, f);
	}

	private int titleTop() {
		int i = (this.height - this.messageHeight()) / 2;
		int var10000 = i - 20;
		Objects.requireNonNull(this.font);
		return Mth.clamp(var10000 - 9, 10, 80);
	}

	private int messageTop() {
		return this.titleTop() + 20;
	}

	private int messageHeight() {
		int lineCount = this.multilineMessage.getLineCount();
		Objects.requireNonNull(this.font);
		return lineCount * 9;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
}
