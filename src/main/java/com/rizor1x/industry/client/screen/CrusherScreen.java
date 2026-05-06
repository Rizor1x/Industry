package com.rizor1x.industry.client.screen;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.CrusherMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// 1. МАГИЯ ЗДЕСЬ: Наследуемся от нашей Базы (BaseMachineScreen)
public class CrusherScreen extends BaseMachineScreen<CrusherMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Industry.MODID, "textures/gui/crusher_gui.png");

    public CrusherScreen(CrusherMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    // 2. Отдаем Базе текстуру нашего Дробителя
    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 3. ВАЖНО: Заставляем Базу нарисовать фон Дробителя и нашу шестеренку!
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // ==========================================
        // ПОЛОСКА ЭНЕРГИИ (Рисуем только её, фон уже нарисован)
        // ==========================================
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0 && energy > 0) {
            int barWidth = 8;
            int barHeight = 54;
            int filledHeight = (energy * barHeight) / maxEnergy;

            guiGraphics.blit(TEXTURE,
                    x + 156, y + 16 + (barHeight - filledHeight),
                    176, 16 + (barHeight - filledHeight),
                    barWidth, filledHeight);
        }

        // ==========================================
        // ПОЛОСКА ПРОГРЕССА
        // ==========================================
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int arrowWidth = 21;
            int arrowHeight = 12;
            int filledWidth = (progress * arrowWidth) / maxProgress;

            guiGraphics.blit(TEXTURE,
                    x + 80, y + 36,
                    176, 0,
                    filledWidth, arrowHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Вызываем render из Базы
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Всплывающая подсказка энергии
        if (mouseX >= x + 156 && mouseX <= x + 163 && mouseY >= y + 16 && mouseY <= y + 69) {
            guiGraphics.renderTooltip(this.font,
                    Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"),
                    mouseX, mouseY);
        }
    }
}