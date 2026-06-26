package com.trojan.proficiency.client.screen;

import com.trojan.proficiency.menu.SolarComposterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SolarComposterScreen
        extends AbstractContainerScreen<SolarComposterMenu> {

    public SolarComposterScreen(
            SolarComposterMenu menu,
            Inventory playerInventory,
            Component title
    ) {

        super(
                menu,
                playerInventory,
                title
        );

        imageWidth = 194;
        imageHeight = 166;
        inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {

        graphics.fill(
                leftPos,
                topPos,
                leftPos + imageWidth,
                topPos + imageHeight,
                0xFF2E2417
        );

        graphics.renderOutline(
                leftPos,
                topPos,
                imageWidth,
                imageHeight,
                0xFF8A6A35
        );

        for (int slot = 0; slot < 5; slot++) {

            drawSlot(
                    graphics,
                    leftPos + 27 + slot * 18,
                    topPos + 24
            );
        }

        drawSlot(
                graphics,
                leftPos + 157,
                topPos + 24
        );

        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 9; column++) {

                drawSlot(
                        graphics,
                        leftPos + 7 + column * 18,
                        topPos + 83 + row * 18
                );
            }
        }

        for (int column = 0; column < 9; column++) {

            drawSlot(
                    graphics,
                    leftPos + 7 + column * 18,
                    topPos + 141
            );
        }

        graphics.fill(
                leftPos + 123,
                topPos + 48,
                leftPos + 149,
                topPos + 54,
                0xFF1D160E
        );

        graphics.fill(
                leftPos + 124,
                topPos + 49,
                leftPos + 124 + menu.getProgressWidth(),
                topPos + 53,
                0xFFE6D26A
        );
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        graphics.drawString(
                font,
                title,
                titleLabelX,
                titleLabelY,
                0xFFE6E6E6,
                false
        );

        graphics.drawString(
                font,
                playerInventoryTitle,
                inventoryLabelX,
                inventoryLabelY,
                0xFFE6E6E6,
                false
        );
    }

    private void drawSlot(
            GuiGraphics graphics,
            int x,
            int y
    ) {

        graphics.fill(
                x,
                y,
                x + 18,
                y + 18,
                0xFF1B140D
        );

        graphics.renderOutline(
                x,
                y,
                18,
                18,
                0xFF7A6238
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        renderTooltip(
                graphics,
                mouseX,
                mouseY
        );
    }
}
