package com.trojan.proficiency.client.screen;

import com.trojan.proficiency.menu.ProficientBrewStandMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ProficientBrewStandScreen
        extends AbstractContainerScreen<ProficientBrewStandMenu> {

    public ProficientBrewStandScreen(
            ProficientBrewStandMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 221;
        inventoryLabelY = 127;
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
                0xFF2B241B
        );
        graphics.renderOutline(
                leftPos,
                topPos,
                imageWidth,
                imageHeight,
                0xFF8C7347
        );

        drawSlot(graphics, leftPos + 88, topPos + 21);
        drawSlot(graphics, leftPos + 16, topPos + 21);
        int[][] potionSlots = {
                {55, 55}, {78, 61}, {101, 55},
                {55, 99}, {78, 105}, {101, 99}
        };
        for (int[] slot : potionSlots) {
            drawSlot(graphics, leftPos + slot[0], topPos + slot[1]);
        }

        graphics.fill(
                leftPos + 87,
                topPos + 43,
                leftPos + 93,
                topPos + 73,
                0xFF17120D
        );
        int progress = menu.getBrewProgressHeight();
        graphics.fill(
                leftPos + 88,
                topPos + 44 + 28 - progress,
                leftPos + 92,
                topPos + 72,
                0xFFB59B58
        );
        graphics.fill(
                leftPos + 16,
                topPos + 45,
                leftPos + 35,
                topPos + 50,
                0xFF17120D
        );
        graphics.fill(
                leftPos + 17,
                topPos + 46,
                leftPos + 17 + menu.getFuelWidth(),
                topPos + 49,
                0xFFD48A2A
        );

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(
                        graphics,
                        leftPos + 7 + column * 18,
                        topPos + 138 + row * 18
                );
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(
                    graphics,
                    leftPos + 7 + column * 18,
                    topPos + 196
            );
        }
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0xFFE6E6E6, false);
        graphics.drawString(
                font,
                playerInventoryTitle,
                inventoryLabelX,
                inventoryLabelY,
                0xFFE6E6E6,
                false
        );
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF18120C);
        graphics.renderOutline(x, y, 18, 18, 0xFF6F5A38);
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
