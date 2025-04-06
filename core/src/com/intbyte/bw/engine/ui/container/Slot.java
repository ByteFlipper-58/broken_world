package com.intbyte.bw.engine.ui.container;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
// InputEvent and InputListener might still be needed for other interactions, but the complex drag logic is removed.
import com.badlogic.gdx.Gdx; // Import Gdx
// import com.badlogic.gdx.scenes.scene2d.InputEvent; // Keep commented
// import com.badlogic.gdx.scenes.scene2d.InputListener; // Keep commented
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image; // For drag actor
import com.badlogic.gdx.scenes.scene2d.ui.Label; // For drag actor count
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.intbyte.bw.engine.item.Container;
import com.intbyte.bw.engine.item.Item;
import com.intbyte.bw.engine.render.Graphic;
import com.intbyte.bw.engine.utils.Resource;

public class Slot extends Actor {
    private final static BitmapFont font = new BitmapFont();
    // Fields related to old drag system are fully removed now.

    private final SlotSkin slotSkin;
    private Container container;
    private float itemSize;
    private final DragAndDrop dragAndDrop; // Add DragAndDrop field
    private boolean isSelected = false; // Flag to indicate if the slot is selected (for highlighting)

    // Updated constructors to accept DragAndDrop
    public Slot(DragAndDrop dragAndDrop){
        this(64, dragAndDrop);
    }
    public Slot(int maxCountItems, DragAndDrop dragAndDrop){
        this(new Container(maxCountItems), dragAndDrop);
    }

    public Slot(Container container, DragAndDrop dragAndDrop){
        this(SlotSkin.DEFAULT, container, dragAndDrop);
    }

    public Slot(SlotSkin skin, final Container container, DragAndDrop dragAndDrop) {
        slotSkin = skin;
        this.container = container;
        this.dragAndDrop = dragAndDrop; // Store DragAndDrop

        // Add DragAndDrop Source and Target
        addDragAndDrop();
    }

    private void addDragAndDrop() {
        // Source: When dragging starts from this slot
        dragAndDrop.addSource(new Source(this) {
            final Payload payload = new Payload();

            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                // Can't drag empty slots
                if (container == null || container.getItems().isEmpty()) {
                    return null;
                } // <<< ADDED MISSING BRACE
                // --- Revised dragStart ---
                // Set the object being dragged (the source container itself)
                payload.setObject(container);

                // Create visual representation based on source container's content
                // Do NOT move items out of the source container here.
                Item itemToDrag = container.getItems().first();
                int countToDrag = container.getCountItems();

                Image itemImage = new Image(itemToDrag.getIcon());
                itemImage.setSize(itemSize, itemSize);
                payload.setDragActor(itemImage);

                Label countLabel = new Label(String.valueOf(countToDrag), new Label.LabelStyle(font, Color.WHITE));
                payload.setValidDragActor(countLabel);
                payload.setInvalidDragActor(countLabel);

                // Change source slot appearance only
                Slot.this.getColor().a = 0.5f;

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                // Restore source slot appearance
                // --- Revised dragStop ---
                // Restore source slot appearance
                Slot.this.getColor().a = 1f;

                // If target is null, the drop was invalid/cancelled.
                // Items were never removed from the source container in dragStart,
                // so no need to return them here.
            }
        });

        // Target: When an item is dragged over or dropped onto this slot
        dragAndDrop.addTarget(new Target(this) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                // Get the container being dragged from the payload
                Container sourceContainer = (Container) payload.getObject();
                Container targetContainer = Slot.this.container; // This slot's container

                // Check if this slot can accept the item type
                // Valid if target accepts any type (-2) or target's required type matches source item's type
                if (targetContainer.getAvailableType() != -2 && targetContainer.getAvailableType() != sourceContainer.getType()) {
                    return false; // Type mismatch
                }

                // Optional: Highlight the slot if it's a valid target
                getActor().setColor(Color.LIGHT_GRAY); // Example highlight
                return true; // It's a valid drop target (basic check)
            }

            @Override
            public void reset(Source source, Payload payload) {
                // Restore original appearance
                getActor().setColor(Color.WHITE);
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                Container sourceContainer = (Container) payload.getObject();
                Container targetContainer = Slot.this.container; // This slot's container

                // --- Item Transfer Logic (Revised) ---

                // Attempt to add/merge items from source to target using the updated moveItems method
                boolean success = targetContainer.moveItems(sourceContainer.getItems());

                // If adding/merging failed (moveItems returned false), attempt a swap
                if (!success) {
                    // Check if items can be swapped (types compatible with both slots)
                    Slot sourceSlot = (Slot) source.getActor();
                    // Ensure target is not empty before getting its type for comparison
                    int targetType = targetContainer.getItems().isEmpty() ? -1 : targetContainer.getType();

                    boolean targetCanAcceptSource = targetContainer.getAvailableType() == -2 || targetContainer.getAvailableType() == sourceContainer.getType();
                    boolean sourceCanAcceptTarget = sourceSlot.getContainer().getAvailableType() == -2 || sourceSlot.getContainer().getAvailableType() == targetType;

                    // Also ensure target is not empty for a swap (cannot swap with empty)
                    if (!targetContainer.getItems().isEmpty() && targetCanAcceptSource && sourceCanAcceptTarget) {
                        // Use temporary containers for swapping
                        // Use the Container.moveItems(Container) overload which handles compatibility checks internally
                        Container tempTarget = new Container(targetContainer.getMaxCountItems());
                        tempTarget.moveItems(targetContainer); // Move target items to temp

                        // Try moving source items to target (should be empty now)
                        targetContainer.moveItems(sourceContainer);

                        // Try moving temp target items (original target items) to source
                        sourceContainer.moveItems(tempTarget);

                    } else {
                        // Cannot add/merge and cannot swap. Drop fails.
                        // Items remain in sourceContainer implicitly because moveItems didn't take them.
                    }
                }
                // If success is true, items were added/merged, sourceContainer.getItems() was modified accordingly.
            }
        });
    }


    public void setContainer(Container container){
        // Need to handle potential null container if set externally after creation
        if (container == null) {
            this.container = new Container(0); // Or handle appropriately
        }
        this.container = container;
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Remove logic related to takenItems and isSelect
        /* if (takenItems.isTaken || !takenItems.isSelect || takenItems.clear) {
            isSelect = false;
            takenItems.clear = false;
            takenItems.isSelect = false;
        }*/
        // Draw base slot texture
        batch.draw(slotSkin.getSprite(), getX(), getY(), getWidth(), getHeight());
        // Draw selection highlight if selected
        if (isSelected) {
            batch.draw(slotSkin.getSelectedTexture(), getX(), getY(), getWidth(), getHeight());
        }

        // Draw item icon and count if container is not null and not empty
        if (container != null && container.getItems().notEmpty()) {
            // Ensure item exists before drawing
            Item item = container.getItems().first(); // Use first() for safety
            if (item != null) {
                item.drawIcon(getX() + getWidth() * 0.1f, getY() + getHeight() * 0.1f, itemSize, itemSize);
            }
            // Draw count
            font.getData().setScale((18 / (18 / (getHeight() * 0.2f))) / 10);
            font.setColor(slotSkin.getCountTextColor());
            String value = String.valueOf(container.getCountItems());
            // Use Graphic.batch if it's the intended batch, otherwise use the passed 'batch' parameter
            font.draw(batch, value, getX() + getWidth() * 0.8f - (font.getXHeight() * value.length() - font.getXHeight()), getY() + font.getLineHeight() * 0.9f);
        }
    }

    public void addItems(Array<Item> items) {
        container.addItems(items);
    }

    @Override
    @Deprecated
    public void setHeight(float height) {
        throw new RuntimeException("Class Slot don't support the method setHeight");
    }

    @Override
    @Deprecated
    public void setWidth(float width) {
        throw new RuntimeException("Class Slot don't support the method setWidth");
    }

    @Override
    @Deprecated
    public void setSize(float width, float height) {
        throw new RuntimeException("Class Slot don't support the method setSize(float width, float height)");
    }

    public void setSize(float size) {
        super.setSize(size, size);
        itemSize = size * 0.8f;
    }

    public Container getContainer() {
        return container;
    }
    // Removed hit method override related to SlotAllocateController

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public interface SlotSkin {
        SlotSkin DEFAULT = new SlotSkin() {
            private final Sprite texture = Resource.getSprite("slot"),
                    selected = Resource.getSprite("selected_slot.png");

            @Override
            public Sprite getSprite() {
                return texture;
            }

            @Override
            public Color getCountTextColor() {
                return Color.WHITE;
            }

            @Override
            public Sprite getSelectedTexture() {
                return selected;
            }
        };

        Sprite getSprite();

        Color getCountTextColor();

        Sprite getSelectedTexture();
    }
    // Removed extra brace that might have been here
}
