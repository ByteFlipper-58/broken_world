package com.intbyte.bw.engine.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class Container {
    protected int maxCountItems;
    protected Array<Item> items;
    protected int availableType = -2;

    public Container(int maxCountItems) {
        items = new Array<>();
        this.maxCountItems = maxCountItems;
    }

    /**
     * Attempts to add items from the source array into this container.
     * Handles stacking only if item's stackSize > 1.
     * Does NOT perform swaps. Modifies the source items array by removing added items.
     * @param sourceItems The array of items to add from.
     * @return true if any items were successfully added or merged, false otherwise.
     */
    public boolean moveItems(Array<Item> sourceItems) {
        if (sourceItems.isEmpty()) return false; // Nothing to add

        Item sourceItemExample = sourceItems.first(); // Item type being added

        // Case 1: Target container is empty
        if (this.items.isEmpty()) {
            // Check if the whole stack fits
            if (sourceItems.size <= this.maxCountItems) {
                this.items.addAll(sourceItems);
                sourceItems.clear();
                return true;
            }
            // Check if item is stackable (even if target is empty, maybe source has > 1 non-stackable?)
            // If stackable, add until full. If not stackable, add only one if possible.
            else if (sourceItemExample.getStackSize() > 1) {
                 while (this.items.size < this.maxCountItems && !sourceItems.isEmpty()) {
                    this.items.add(sourceItems.pop());
                 }
                 return true; // Items were added
            } else if (this.maxCountItems >= 1) { // Not stackable, add one if space exists
                this.items.add(sourceItems.pop());
                return true;
            } else {
                return false; // Cannot add even one item
            }
        }
        // Case 2: Target container is NOT empty, check for matching IDs and stackability
        else if (this.items.first().getId() == sourceItemExample.getId() &&
                 this.items.first().getStackSize() > 1) { // Check stackability of items ALREADY in target

            int spaceAvailable = this.maxCountItems - this.items.size;
            if (spaceAvailable <= 0) return false; // Target is full

            int itemsToMove = Math.min(spaceAvailable, sourceItems.size);

            for (int i = 0; i < itemsToMove; i++) {
                if (!sourceItems.isEmpty()) { // Should always be true here, but safety check
                    this.items.add(sourceItems.pop());
                } else {
                    break; // Source became empty
                }
            }
            return itemsToMove > 0; // Return true if we moved at least one item
        }

        // Case 3: Items are different, or target item is not stackable (stackSize=1)
        return false; // Cannot add/merge, indicates a swap might be needed externally
    }


    public void moveItems(Container container){

        if(container.getAvailableType()==getAvailableType()||
                getAvailableType()==-2||
                container.getType()==getType()||
                (getAvailableType()==container.getType())&&
                        ((container.getCountItems()<=getMaxCountItems()&&
                getCountItems()<=container.getMaxCountItems()||
                getId()==container.getId())||
                getItems().isEmpty()))
            moveItems(container.getItems());
    }
    // This method might be misleading now as moveItems returns boolean.
    // Keep it for compatibility or refactor its usage.
    // For now, let's assume it tries to move and returns the container.
    public Container addItems(Array<Item> items) {
        moveItems(items); // Call the boolean version
        return this;      // Return self regardless of success for compatibility
    }

    public int getMaxCountItems() {
        return maxCountItems;
    }

    public int getId() {
        return items.notEmpty() ? items.get(0).getId() : 0;
    }

    public int getCountItems() {
        return items.size;
    }

    public Item delete() {
        if (items.isEmpty()) {
            Gdx.app.log("CONTAINER", "cannot delete element, because container is empty");
            return null;
        }
        return items.pop();
    }

    public void clear() {
        items.clear();
    }

    public Array<Item> getItems() {
        return items;
    }

    public void setItems(Array<Item> items) {
        if (items.isEmpty()) return;
        this.items.clear();
        if (maxCountItems >= items.size && items.get(0).getStackSize() >= items.size)
            this.items.addAll(items);
        else
            for (int i = 0; i < maxCountItems; i++) {
                this.items.add(items.get(i));
            }
    }

    public Item getLastElement() {
        return items.get(getCountItems() - 1);
    }

    public int getType(){
        return items.notEmpty() ? items.get(0).getType() : -1;
    }

    public int getAvailableType() {
        return availableType;
    }

    public void setAvailableType(int availableType) {
        this.availableType = availableType;
    }

    public void setMaxCountItems(int maxCountItems) {
        this.maxCountItems = maxCountItems;
    }
}
