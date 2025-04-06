package com.intbyte.bw.game.gameUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener; // Import ClickListener
import com.badlogic.gdx.scenes.scene2d.ui.Table; // Import Table
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop; // Import DragAndDrop
import com.badlogic.gdx.utils.Array;
import com.intbyte.bw.engine.callbacks.CallBack;
import com.intbyte.bw.engine.callbacks.Render;
import com.intbyte.bw.engine.entity.Player;
import com.intbyte.bw.engine.graphic.GravityAdapter;
import com.intbyte.bw.engine.graphic.GravityAttribute;
import com.intbyte.bw.engine.input.InteractionOfItems;
import com.intbyte.bw.engine.item.Container;
import com.intbyte.bw.engine.item.Item;
import com.intbyte.bw.engine.ui.GUI;
import com.intbyte.bw.engine.ui.Joystick;
import com.intbyte.bw.engine.ui.Layer;
import com.intbyte.bw.engine.ui.ProgressBar;
import com.intbyte.bw.engine.ui.container.Slot;
// import com.intbyte.bw.engine.ui.container.TakenItemsRender; // Remove import
import com.intbyte.bw.engine.utils.Debug;
import com.intbyte.bw.engine.utils.ExtraData;
import com.intbyte.bw.engine.utils.Resource;
import com.intbyte.bw.engine.world.World;

import static com.intbyte.bw.engine.graphic.TypedValue.APIXEL;
import static com.intbyte.bw.engine.graphic.TypedValue.HHPIXEL;

public class MainLayerUI extends Layer {


    private final Player player = Player.getPlayer();
    private final Label label;
    private final DragAndDrop dragAndDrop; // Add DragAndDrop instance
    // Remove local activeHotbarIndex, use player's state


    public MainLayerUI() {
        dragAndDrop = new DragAndDrop(); // Create local DragAndDrop instance
        final GravityAdapter adapter = new GravityAdapter();
        final StringBuilder builder = new StringBuilder();

        CallBack.addCallBack(new Render() {
            @Override
            public void main() {

                for (int i = 0; i < Debug.getDebugMessages().size(); i++) {
                    builder.append(Debug.getDebugMessages().get(i));
                    builder.append("\n");
                }
                label.setText(builder);
                builder.setLength(0);
                adapter.addActor(label);
                adapter.setHeight(label.getPrefHeight());
                adapter.setGravity(GravityAttribute.TOP, GravityAttribute.LEFT);
                adapter.margin(10 * APIXEL, 0);
                adapter.apply();
            }
        });

        // --- Hotbar Setup ---
        final float hotbarSlotSize = 80 * APIXEL; // Smaller size for hotbar slots
        final Container carriedItemContainer = Player.getPlayer().getCarriedItem(); // Get carried item container

        // Create slots for hotbar
        final Slot slot = new Slot(Slot.SlotSkin.DEFAULT, carriedItemContainer, dragAndDrop);
        slot.setSize(hotbarSlotSize);

        // Link slot2 and slot3 to the first inventory containers
        // Handle cases where inventory might have less than 2 items
        final Container inventorySlot0 = player.getInventory().size > 0 ? player.getInventory().get(0) : new Container(0); // Use empty container if index invalid
        final Container inventorySlot1 = player.getInventory().size > 1 ? player.getInventory().get(1) : new Container(0); // Use empty container if index invalid

        final Slot slot2 = new Slot(Slot.SlotSkin.DEFAULT, inventorySlot0, dragAndDrop);
        slot2.setSize(hotbarSlotSize);

        final Slot slot3 = new Slot(Slot.SlotSkin.DEFAULT, inventorySlot1, dragAndDrop);
        slot3.setSize(hotbarSlotSize);

        // Add ClickListeners to set the active index
        slot.addListener(new ClickListener() { // Add listener to the first slot too
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.setActiveHotbarIndex(0);
            }
        });
        slot2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.setActiveHotbarIndex(1);
            }
        });
        slot3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.setActiveHotbarIndex(2);
            }
        });


        // Create a Table for the hotbar layout
        Table hotbarTable = new Table();
        hotbarTable.add(slot).size(hotbarSlotSize).pad(5 * APIXEL);
        hotbarTable.add(slot2).size(hotbarSlotSize).pad(5 * APIXEL);
        hotbarTable.add(slot3).size(hotbarSlotSize).pad(5 * APIXEL);

        // Position the table at the bottom center
        hotbarTable.pack(); // Calculate preferred size
        hotbarTable.setPosition((Gdx.graphics.getWidth() - hotbarTable.getWidth()) / 2, 20 * APIXEL); // Position at bottom center with some padding
        addActor(hotbarTable);

        // Add Render callback to highlight the selected slot based on activeHotbarIndex
        CallBack.addCallBack(new Render() {
            @Override
            public void main() {
                // Set selection state based on player's activeHotbarIndex
                int currentIndex = player.getActiveHotbarIndex();
                slot.setSelected(currentIndex == 0);
                slot2.setSelected(currentIndex == 1);
                slot3.setSelected(currentIndex == 2);
            }
        });


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/jb_mono.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        BitmapFont font12 = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        label = new Label(" ", new Label.LabelStyle(font12, Color.WHITE));
        label.setFontScale(HHPIXEL / 50 * 10 * 0.8f);
        adapter.addActor(label);
        adapter.setHeight(label.getPrefHeight());
        adapter.setGravity(GravityAttribute.TOP, GravityAttribute.LEFT);
        addActor(label);

        Joystick joystick = new Joystick(Resource.getSprite("gui/Joystick_0.png"), Resource.getSprite("gui/Joystick_1.png"), 80 * APIXEL);
        joystick.moveBy(APIXEL, 40, 40);
        addActor(joystick);


        final ProgressBar bar = new ProgressBar(new TestProgressBarSkin());
        bar.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                GUI.setLayer("inventory", null);
            }
        });
        bar.setSize(100 * APIXEL, 20 * APIXEL);
        adapter.addActor(bar);
        adapter.setGravity(GravityAttribute.TOP, GravityAttribute.RIGHT);
        addActor(bar);

        CallBack.addCallBack(new Render() {
            @Override
            public void main() {
                bar.setState(player.getEndurance());
            }
        });
        adapter.apply();

    }

    @Override
    public Layer onCreate(ExtraData data) {
        InteractionOfItems.setInteraction(true);
        if (!World.getConfig().isDebug())
            removeActor(label);
        else
            addActor(label);
        return this;
    }

    @Override
    public void destroy() {
        InteractionOfItems.setInteraction(false);
    }

    // Remove swapContainers method as it's no longer used for selection
    /* private void swapContainers(Container c1, Container c2) {
        // Use temporary storage to swap items array and counts
        Array<Item> tempItems = new Array<>(c1.getItems());
        int tempMaxCount = c1.getMaxCountItems();
        int tempAvailableType = c1.getAvailableType();

        c1.clear();
        c1.setMaxCountItems(c2.getMaxCountItems());
        c1.setAvailableType(c2.getAvailableType());
        c1.addItems(c2.getItems());

        c2.clear();
        c2.setMaxCountItems(tempMaxCount);
        c2.setAvailableType(tempAvailableType);
        c2.addItems(tempItems);
    }*/
}
