package dev.latvian.kubejs.client.error;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.CommonProperties;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Objects;

public class KubeJSErrorScreen extends Screen {
    public static boolean used = !CommonProperties.get().startupErrorGUI;

	public final ScriptType type;
    private final Screen lastScreen;
    private final boolean canClose;
    private MultiLineLabel multilineMessage;

	public KubeJSErrorScreen(ScriptType type, Screen lastScreen, boolean canClose) {
		super(new TextComponent(""));
		this.type = type;
        this.lastScreen = lastScreen;
        this.canClose = canClose;
        this.multilineMessage = MultiLineLabel.EMPTY;
	}

	@Override
	public String getNarrationMessage() {
		return "There were KubeJS startup errors!";
	}

	@Override
	protected void init() {
		super.init();

        val formatted = new TextComponent("");
        formatted.append(
            new TextComponent("There were KubeJS startup errors ")
                .append(new TextComponent("[" + type.errors.size() + "]").withStyle(ChatFormatting.DARK_RED))
                .append("!")
        );

		val style = Style.EMPTY.withColor(TextColor.fromRgb(0xD19893));

        {
            List<String> errors = type.errors;
            for (int i = 0, errorsSize = errors.size(); i < errorsSize; i++) {
                formatted.append("\n")
                    .append(
                        new TextComponent((i + 1) + ") ").withStyle(ChatFormatting.DARK_RED)
                            .append(
                                new TextComponent(errors.get(i)
                                    .replace("Error occurred while handling event ", "Error in ")
                                    .replace("dev.latvian.mods.rhino.", "...rhino.")
                                    .replace("dev.latvian.kubejs.", "...")
                                )
                                    .withStyle(style)));
            }
        }

		this.multilineMessage = MultiLineLabel.create(this.font, formatted, this.width - 12);
		int i = this.height - 26;

		if (CommonProperties.get().startupErrorReportUrl.isBlank()) {
			this.addButton(new Button(this.width / 2 - 155, i, 150, 20, new TextComponent("Open startup.log"), this::openLog));
			this.addButton(new Button(this.width / 2 - 155 + 160, i, 150, 20, new TextComponent("Back"), this::quit));
		} else {
			this.addButton(new Button(this.width / 4 - 55, i, 100, 20, new TextComponent("Open startup.log"), this::openLog));
			this.addButton(new Button(this.width / 2 - 50, i, 100, 20, new TextComponent("Report"), this::report));
			this.addButton(new Button(this.width * 3 / 4 - 45, i, 100, 20, new TextComponent("Quit"), this::quit));
		}
	}

	private void quit(Button button) {
        used = true;
        if (canClose) {
            onClose();
        } else {
            minecraft.stop();
        }
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
		return canClose;
	}

    @Override
    public void onClose() {
        minecraft.setScreen(lastScreen);
    }
}
