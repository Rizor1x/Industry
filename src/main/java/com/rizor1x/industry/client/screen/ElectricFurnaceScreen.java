package com.rizor1x.industry.client.screen;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.ElectricFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectricFurnaceScreen extends BaseMachineScreen<ElectricFurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Industry.MODID, "textures/gui/electric_furnace_gui.png");

    public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // ЭНЕРГИЯ (Слева)
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0 && energy > 0) {
            int barWidth = 8;
            int barHeight = 54;
            int filledHeight = (energy * barHeight) / maxEnergy;

            // Если у тебя энергия в другом месте, поменяй 156 и 16
            guiGraphics.blit(TEXTURE, x + 156, y + 16 + (barHeight - filledHeight), 176, 16 + (barHeight - filledHeight), barWidth, filledHeight);
        }

        // ПРОГРЕСС ПЛАВКИ (ОГОНЕК)
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int arrowWidth = 22;
            int arrowHeight = 16;
            int filledWidth = (progress * arrowWidth) / maxProgress;

            guiGraphics.blit(TEXTURE,
                    x + 80, y + 34,
                    176, 0,
                    filledWidth, arrowHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Подсказка энергии
        if (mouseX >= x + 156 && mouseX <= x + 163 && mouseY >= y + 16 && mouseY <= y + 69) {
            guiGraphics.renderTooltip(this.font, Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"), mouseX, mouseY);
        }
    }
}