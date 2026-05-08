package com.rizor1x.industry.client.screen;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.SolarPanelMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends BaseMachineScreen<SolarPanelMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Industry.MODID, "textures/gui/solar_panel_gui.png");

    public SolarPanelScreen(SolarPanelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected ResourceLocation getGuiTexture() { return TEXTURE; }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // ==========================================
        // 1. ИНДИКАТОР СОЛНЦА / ЛУНЫ (Теперь берет данные прямо с сервера!)
        // ==========================================
        int iconX = x + 81;
        int iconY = y + 18;
        int iconSize = 14;

        // Если генерация больше 0 - значит работает!
        boolean isGenerating = menu.getProgress() > 0;

        if (isGenerating) {
            // Рисуем Солнышко
            guiGraphics.blit(TEXTURE, iconX, iconY, 185, 0, iconSize, iconSize);
        } else {
            // Рисуем Ночь/Дождь
            guiGraphics.blit(TEXTURE, iconX, iconY, 185, 17, iconSize, iconSize);
        }

        // ==========================================
        // 2. ПОЛОСКА ЭНЕРГИИ
        // ==========================================
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0 && energy > 0) {
            int barWidth = 8;
            int barHeight = 54;
            int filledHeight = (energy * barHeight) / maxEnergy;

            int texX = 176;
            int texY = 16;

            guiGraphics.blit(TEXTURE,
                    x + 156, y + 16 + (barHeight - filledHeight),
                    texX, texY + (barHeight - filledHeight),
                    barWidth, filledHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Всплывающая подсказка над батареей
        if (mouseX >= x + 156 && mouseX <= x + 163 && mouseY >= y + 16 && mouseY <= y + 69) {
            guiGraphics.renderTooltip(this.font, Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), mouseX, mouseY);
        }

        // Всплывающая подсказка над Солнцем (Показывает реальную генерацию!)
        if (mouseX >= x + 81 && mouseX <= x + 94 && mouseY >= y + 18 && mouseY <= y + 31) {
            int currentGen = menu.getProgress();

            if (currentGen > 0) {
                // Пишет зеленым: "Generating: 10 FE/t"
                guiGraphics.renderTooltip(this.font, Component.literal("Generating: " + currentGen + " FE/t").withStyle(ChatFormatting.GREEN), mouseX, mouseY);
            } else {
                // Пишет красным: "Not Generating"
                guiGraphics.renderTooltip(this.font, Component.literal("Not Generating").withStyle(ChatFormatting.RED), mouseX, mouseY);
            }
        }
    }
}