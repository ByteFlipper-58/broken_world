package com.intbyte.bw.game.gameUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Import BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator; // Import FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.intbyte.bw.engine.entity.Player;
import com.intbyte.bw.engine.graphic.GravityAdapter;
import com.intbyte.bw.engine.graphic.GravityAttribute;
import com.intbyte.bw.engine.graphic.TypedValue;
import com.intbyte.bw.engine.ui.GUI;
import com.intbyte.bw.engine.ui.Inventory;
import com.intbyte.bw.engine.ui.Layer;
import com.intbyte.bw.engine.ui.Panel;
import com.intbyte.bw.engine.ui.container.Slot;
import com.intbyte.bw.engine.utils.ExtraData;
import com.intbyte.bw.engine.utils.Resource; // Keep Resource import
// Remove duplicate Resource import if present
// import com.intbyte.bw.engine.utils.Resource;

import static com.intbyte.bw.engine.item.Item.*;

public class InventoryLayerUI extends Layer {

    private final Inventory inventory;
    private final DragAndDrop dragAndDrop;

    public InventoryLayerUI() {
        dragAndDrop = new DragAndDrop();
        inventory = new Inventory(dragAndDrop); // Pass DragAndDrop to Inventory

        // Load font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/jetbrains_mono.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18; // Choose desired font size
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose(); // Dispose generator

        final float padding = Gdx.graphics.getHeight() * 0.06f;
        float width = (Gdx.graphics.getWidth() - padding * 2 - padding) / 2;
        float height = Gdx.graphics.getHeight() - padding * 2;

        Sprite sprite = Panel.getDrawRoundedPanel((int) (width), (int) (height), (int) (TypedValue.APIXEL * 4), 0.1f, 0.1f, 0.1f, 0.6f - 0.1f);
        Resource.addSprite(sprite, "inventory");

        Actor actor = new Actor();
        actor.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        actor.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GUI.setLayer("main", null);
            }
        });
        addActor(actor);

        // Panel 1 (Main Inventory)
        Panel panel = new Panel("inventory"); // Consider a different background later
        panel.setSize(width, height);
        panel.setPosition(padding, padding);
        addActor(panel);

        // Label for Panel 1 using loaded font
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label inventoryLabel = new Label("Инвентарь", labelStyle);
        inventoryLabel.setPosition(padding + TypedValue.APIXEL * 4, padding + height - inventoryLabel.getHeight() - TypedValue.APIXEL * 2);
        // Add label later, after adapter.apply()


        GravityAdapter adapter = new GravityAdapter();
        // Panel 2 (Equipment/Hotbar)
        Panel panel2 = new Panel("inventory"); // Consider a different background later
        panel2.setSize(width, height);
        adapter.addActor(panel2);
        adapter.tiedTo(GravityAttribute.LEFT, panel);
        adapter.margin(padding, padding);
        addActor(panel2);

        // Label for Panel 2 using loaded font
        Label equipmentLabel = new Label("Снаряжение", labelStyle);
        // Position label relative to panel2 after adapter.apply() might be better, but let's try this for now
        equipmentLabel.setPosition(padding * 2 + width + TypedValue.APIXEL * 4, padding + height - equipmentLabel.getHeight() - TypedValue.APIXEL * 2);
        // Add label later, after adapter.apply()


        inventory.setContainers(Player.getPlayer().getInventory());
        inventory.setSize(width - padding * 2, height - padding * 2); // Size is set relative to panel
        inventory.setElementsPerLine(5);
        // Position is managed by the panel's layout, setting 0,0 relative to panel
        inventory.setPosition(padding, padding); // Position inside the panel
        panel.addActor(inventory);

        // Create Slots and pass DragAndDrop
        Slot firstItem = new Slot(Player.getPlayer().getCarriedItem(), dragAndDrop),
             secondItem = new Slot(1, dragAndDrop), // Assuming Container constructor takes maxCount
             thirdItem = new Slot(dragAndDrop),      // Assuming default constructor exists or Container(defaultMax)
             helmet = new Slot(1, dragAndDrop),
             armor = new Slot(1, dragAndDrop),
             leggings = new Slot(1, dragAndDrop);


        final float slotSize = (panel2.getWidth() - padding * 4) / 3f * 0.85f;
        firstItem.setSize(slotSize);
        secondItem.setSize(slotSize);
        thirdItem.setSize(slotSize);
        helmet.setSize(slotSize);
        armor.setSize(slotSize);
        leggings.setSize(slotSize);

        helmet.getContainer().setAvailableType(PICKAXE);
        armor.getContainer().setAvailableType(ARMOR);
        leggings.getContainer().setAvailableType(LEGGINGS);

        panel2.addActor(firstItem);
        panel2.addActor(secondItem);
        panel2.addActor(thirdItem);

        firstItem.setPosition(padding, panel2.getHeight() - padding - firstItem.getHeight());

        adapter.addActor(secondItem);
        adapter.tiedTo(GravityAttribute.BOTTOM, firstItem);
        adapter.margin(padding, 0);

        adapter.addActor(thirdItem);
        adapter.tiedTo(GravityAttribute.BOTTOM, secondItem);
        adapter.margin(padding, 0);

        Panel playerPanel = new Panel(Panel.drawPanel(10, 10, 0, 0.1f, 0.1f, 0.1f, 0.5f));
        playerPanel.setSize(panel2.getWidth() - (firstItem.getWidth() + padding * 2) * 2, firstItem.getHeight() * 3);

        adapter.addActor(playerPanel);
        adapter.tiedTo(GravityAttribute.BOTTOM, secondItem);
        adapter.tiedTo(GravityAttribute.LEFT, secondItem);
        adapter.margin(padding, 0);
        panel2.addActor(playerPanel);

        helmet.setPosition(0, panel2.getHeight() - padding - firstItem.getHeight());
        adapter.addActor(helmet);
        adapter.tiedTo(GravityAttribute.LEFT, playerPanel);
        adapter.margin(padding, 0);
        panel2.addActor(helmet);

        adapter.addActor(armor);
        adapter.tiedTo(GravityAttribute.LEFT, playerPanel);
        adapter.tiedTo(GravityAttribute.BOTTOM, helmet);
        adapter.margin(padding, 0);
        panel2.addActor(armor);

        adapter.addActor(leggings);

        adapter.tiedTo(GravityAttribute.LEFT, playerPanel);
        adapter.tiedTo(GravityAttribute.BOTTOM, armor);
        adapter.margin(padding, 0);
        panel2.addActor(leggings);

        adapter.apply();

        // Add labels after adapter has positioned panels
        inventoryLabel.setPosition(panel.getX() + TypedValue.APIXEL * 4, panel.getY() + panel.getHeight() - inventoryLabel.getHeight() - TypedValue.APIXEL * 2);
        equipmentLabel.setPosition(panel2.getX() + TypedValue.APIXEL * 4, panel2.getY() + panel2.getHeight() - equipmentLabel.getHeight() - TypedValue.APIXEL * 2);

        addActor(inventoryLabel);
        addActor(equipmentLabel);
        inventoryLabel.toFront(); // Bring labels to front
        equipmentLabel.toFront();

    }

    @Override
    public Layer onCreate(ExtraData data) {
        inventory.apply();
        return this;
    }

    @Override
    public void destroy() {

    }
}
