package com.intbyte.bw.engine.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.intbyte.bw.engine.item.Container;
import com.intbyte.bw.engine.ui.container.Slot;

public class Inventory extends Group {
    protected int elementsPerLine;
    protected Array<Container> containers;
    protected Array<Slot> slots;
    protected Table scrollTable;
    protected Table layout;
    protected ScrollPane scrollPane;
    protected final DragAndDrop dragAndDrop; // Add DragAndDrop field

    protected int defaultSize = 12;

    // Remove default constructor or make it private if not needed
    /* public  Inventory(){
        this(null, null); // Would need a default DragAndDrop? Better to remove.
    }*/


    @Override
    public void setPosition(float x, float y) {
        layout.setPosition(x, y);
    }

    public void setDefaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
    }

    public void setContainers(Array<Container> containers) {
        this.containers = containers;
    }

    // Updated constructor to accept DragAndDrop
    public Inventory(DragAndDrop dragAndDrop){
        this(null, dragAndDrop);
    }

    // Updated constructor to accept DragAndDrop
    public Inventory(Array<Container> containers, DragAndDrop dragAndDrop){
        this.containers = containers;
        this.dragAndDrop = dragAndDrop; // Store DragAndDrop instance
        this.scrollTable = new Table();
        this.slots = new Array<>();
        this.scrollPane = new ScrollPane(scrollTable);
        scrollPane.setOverscroll(false,true);
        scrollTable.top().left();
        layout = new Table();
        layout.setFillParent(true);
        layout.add(scrollPane).expand().fill();
        layout.setSize(getWidth(),getHeight());
        addActor(layout);
    }

    public void setElementsPerLine(int elementsPerLine) {
        this.elementsPerLine = elementsPerLine;
    }

    public void apply(){
        scrollTable.clear();
        // Ensure containers is not null before proceeding
        if (this.containers == null) {
            this.containers = new Array<>();
        }

        int i = 0;
        // Pre-create slots with DragAndDrop instance
        while (slots.size < containers.size) {
            // Pass dragAndDrop to Slot constructor
            slots.add(new Slot(Slot.SlotSkin.DEFAULT, containers.get(i++), dragAndDrop));
        }

        for(i = 0; i < containers.size; i++){
            // Slot already created with container and dragAndDrop, just configure size and add
            slots.get(i).setSize(getSlotSize()); // Use getSlotSize() for consistency
            // slots.get(i).setContainer(containers.get(i)); // Container is set in constructor now
            scrollTable.add(slots.get(i)).size(getSlotSize()); // Add size constraint to table cell
            if((i+1)%elementsPerLine==0)
                scrollTable.row();
        }
    }


    public float getSlotSize(){
        return getWidth()/elementsPerLine;
    }
    boolean scrollLock;
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(1,1,1,1);
    }

    // Remove act method override related to SlotAllocateController
    /* @Override
    public void act(float delta) {
        super.act(delta);
        // if(SlotAllocateController.isAllocate()||SlotAllocateController.isLockScroll()) // Remove this check
        //     scrollPane.cancel();
    }*/
}
