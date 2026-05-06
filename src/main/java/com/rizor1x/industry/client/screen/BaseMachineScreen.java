package com.rizor1x.industry.client.screen;

import com.rizor1x.industry.Industry;
import com.rizor1x.industry.menu.BaseMachineMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class BaseMachineScreen<T extends BaseMachineMenu> extends AbstractContainerScreen<T> {
    public static final ResourceLocation COMPONENTS = ResourceLocation.fromNamespaceAndPath(Industry.MODID, "textures/gui/configuration_gui.png");

    private boolean isIoPanelOpen = false;
    private float slideProgress = 0f; // Переменная для ПЛАВНОЙ анимации

    // ==========================================
    // НАСТРОЙКИ (МЕНЯЙ ЭТИ ЦИФРЫ ПОД СЕБЯ)
    // ==========================================
    int panelWidth = 86;  // Ширина выезжающей панели
    int panelHeight = 88; // Высота выезжающей панели

    int gearWidth = 22;   // Размер самой кнопки-шестеренки
    int gearHeight = 22;

    // Высота спавна менюшки относительно верха основного окна (Если низко - сделай 0 или отрицательное число)
    int tabOffsetY = 0;

    // Координаты 6 квадратиков относительно панели
    int[][] slotPos = { {41, 62}, {41, 24}, {41, 43}, {60, 62}, {22, 43}, {60, 43} };

    // Названия сторон для всплывающих подсказок (Индексы: 0=DOWN, 1=UP, 2=NORTH, 3=SOUTH, 4=WEST, 5=EAST)
    String[] sideNames = {"Bottom", "Top", "Front", "Back", "Left", "Right"};

    // Координаты вырезания иконок из png
    int iconBlueU = 88, iconBlueV = 22;
    int iconOrangeU = 88, iconOrangeV = 38;
    int gearU = 87, gearV = 0; // Координаты отдельной шестеренки (закрытого состояния)
    // ==========================================

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    protected abstract ResourceLocation getGuiTexture();

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int rightEdge = x + imageWidth;

        // Математика плавной анимации
        float target = isIoPanelOpen ? 1.0f : 0.0f;
        if (slideProgress != target) {
            slideProgress += (target - slideProgress) * 0.2f; // Скорость анимации
            if (Math.abs(target - slideProgress) < 0.01f) slideProgress = target;
        }

        // 1. Рисуем основной GUI
        guiGraphics.blit(getGuiTexture(), x, y, 0, 0, imageWidth, imageHeight);

        // 2. Анимация панели
        if (slideProgress > 0.01f) {
            // Плавно увеличиваем ширину и высоту от размера шестеренки до полного размера панели!
            int currentWidth = (int) (gearWidth + (panelWidth - gearWidth) * slideProgress);
            int currentHeight = (int) (gearHeight + (panelHeight - gearHeight) * slideProgress);

            // Отрисовка раздвигающейся панели (Она вырезается от 0,0 до нужного размера)
            guiGraphics.blit(COMPONENTS, rightEdge, y + tabOffsetY, 0, 0, currentWidth, currentHeight);

            // Рисуем иконки в слотах ТОЛЬКО когда панель почти открыта (чтобы не вылезали за края во время анимации)
            if (slideProgress > 0.9f) {
                for (int i = 0; i < 6; i++) {
                    int mode = menu.getSideConfig(i);
                    if (mode == 1) {
                        guiGraphics.blit(COMPONENTS, rightEdge + slotPos[i][0], y + tabOffsetY + slotPos[i][1], iconBlueU, iconBlueV, 16, 16);
                    } else if (mode == 2) {
                        guiGraphics.blit(COMPONENTS, rightEdge + slotPos[i][0], y + tabOffsetY + slotPos[i][1], iconOrangeU, iconOrangeV, 16, 16);
                    }
                }
            }
        } else {
            // 3. Если меню полностью закрыто - рисуем только шестеренку
            guiGraphics.blit(COMPONENTS, rightEdge, y + tabOffsetY, gearU, gearV, gearWidth, gearHeight);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int rightEdge = x + imageWidth;

        // Клик по шестеренке (Эта зона 22х22 пикселя всегда находится слева сверху, даже когда панель открыта!)
        if (mouseX >= rightEdge && mouseX <= rightEdge + gearWidth && mouseY >= y + tabOffsetY && mouseY <= y + tabOffsetY + gearHeight) {
            isIoPanelOpen = !isIoPanelOpen; // Переключаем открытие/закрытие
            return true;
        }

        // Клик по слотам
        if (isIoPanelOpen && slideProgress > 0.9f) {
            for (int i = 0; i < 6; i++) {
                int btnX = rightEdge + slotPos[i][0];
                int btnY = y + tabOffsetY + slotPos[i][1];

                if (mouseX >= btnX && mouseX <= btnX + 16 && mouseY >= btnY && mouseY <= btnY + 16) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int rightEdge = x + imageWidth;

        // ==========================================
        // ВСПЛЫВАЮЩИЕ ПОДСКАЗКИ (TOOLTIPS)
        // ==========================================

        // 1. Подсказка для шестеренки (когда меню закрыто)
        if (slideProgress < 0.1f) {
            if (mouseX >= rightEdge && mouseX <= rightEdge + gearWidth && mouseY >= y + tabOffsetY && mouseY <= y + tabOffsetY + gearHeight) {
                guiGraphics.renderTooltip(this.font, Component.literal("Configuration").withStyle(ChatFormatting.GRAY), mouseX, mouseY);
            }
        }
        // 2. Подсказки для сторон (когда меню открыто)
        else if (isIoPanelOpen && slideProgress > 0.9f) {
            for (int i = 0; i < 6; i++) {
                int btnX = rightEdge + slotPos[i][0];
                int btnY = y + tabOffsetY + slotPos[i][1];

                if (mouseX >= btnX && mouseX <= btnX + 16 && mouseY >= btnY && mouseY <= btnY + 16) {
                    int mode = menu.getSideConfig(i);

                    // Красивый цветной текст режима
                    Component modeText = Component.literal("None").withStyle(ChatFormatting.DARK_GRAY);
                    if (mode == 1) modeText = Component.literal("Input").withStyle(ChatFormatting.BLUE);
                    if (mode == 2) modeText = Component.literal("Output").withStyle(ChatFormatting.GOLD);

                    // Склеиваем: "Top: Input"
                    Component tooltip = Component.literal(sideNames[i] + ": ").withStyle(ChatFormatting.WHITE).append(modeText);

                    guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
                }
            }
        }

        // Сохраняем подсказку для энергии и предметов
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}