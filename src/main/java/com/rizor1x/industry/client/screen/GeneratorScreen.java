package com.rizor1x.industry.client.screen;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.GeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen extends BaseMachineScreen<GeneratorMenu> {
    // Назови свою картинку generator_gui.png!
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Industry.MODID, "textures/gui/coal_generator_gui.png");

    public GeneratorScreen(GeneratorMenu menu, Inventory playerInventory, Component title) {
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

        // 2. АНИМАЦИЯ ОГОНЬКА (Сгорает сверху вниз)
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            // Размеры огонька (например, 14х14 пикселей)
            int fireWidth = 14;
            int fireHeight = 14;

            // Сколько пикселей огня осталось
            int remainingFire = (progress * fireHeight) / maxProgress;

            // Координаты на экране (между слотами)
            int fireX = x + 81;
            int fireY = y + 36;

            // Координаты полного огонька в твоем png (допустим, он на 176, 0)
            int texX = 176;
            int texY = 0;

            guiGraphics.blit(TEXTURE,
                    fireX, fireY + (fireHeight - remainingFire),
                    texX, texY + (fireHeight - remainingFire),
                    fireWidth, remainingFire);
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