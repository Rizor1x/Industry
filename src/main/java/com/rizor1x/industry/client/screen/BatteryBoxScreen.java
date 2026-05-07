package com.rizor1x.industry.client.screen;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.BatteryBoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BatteryBoxScreen extends BaseMachineScreen<BatteryBoxMenu> {
    // Назови свою картинку generator_gui.png!
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Industry.MODID, "textures/gui/battery_box_gui.png");

    public BatteryBoxScreen(BatteryBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // База рисует фон и выезжающую менюшку конфигурации!
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. ПОЛОСКА ЭНЕРГИИ
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0 && energy > 0) {
            int barWidth = 8;
            int barHeight = 54;
            int filledHeight = (energy * barHeight) / maxEnergy;

            // Если у тебя энергия в другом месте, поменяй 156 и 16
            guiGraphics.blit(TEXTURE, x + 156, y + 16 + (barHeight - filledHeight), 176, 16 + (barHeight - filledHeight), barWidth, filledHeight);
        }

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if (mouseX >= x + 156 && mouseX <= x + 163 && mouseY >= y + 16 && mouseY <= y + 69) {
            guiGraphics.renderTooltip(this.font, Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), mouseX, mouseY);
        }
    }
}