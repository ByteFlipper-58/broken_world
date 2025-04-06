package com.intbyte.bw.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.intbyte.bw.engine.GameThread;
import com.intbyte.bw.engine.entity.Player;
import com.intbyte.bw.engine.block.BlockExtraData;
import com.intbyte.bw.engine.block.CustomBlock;
import com.intbyte.bw.engine.callbacks.Render;
import com.intbyte.bw.engine.callbacks.Touch;
import com.intbyte.bw.engine.callbacks.TouchOnBlock;
import com.intbyte.bw.engine.callbacks.Drag;
import com.intbyte.bw.engine.callbacks.CallBack;
import com.intbyte.bw.engine.entity.Entity;
import com.intbyte.bw.engine.item.Container;
import com.intbyte.bw.engine.block.Block;
import com.intbyte.bw.engine.item.Item;
import com.intbyte.bw.engine.item.ItemFactory;
import com.intbyte.bw.engine.world.Tile;
import com.intbyte.bw.engine.world.World;

import java.util.HashMap;

public class InteractionOfItems {
    private static final Player player = Player.getPlayer();
    private static InteractionOfItems instance;
    private static boolean isDragged;

    private final HashMap<Integer, Integer> settableItemsHashMap = new HashMap<>();
    private final StringBuilder builder = new StringBuilder();
    private Integer id;
    private Container container;
    private Item item;
    private static final Rectangle rectangle = new Rectangle(0, 0, 10, 10);
    private static boolean interaction;
    private static ModelInstance modelInstance;

    public static InteractionOfItems getInstance() {
        if (instance == null) instance = new InteractionOfItems();
        return instance;
    }

    public static boolean isInteraction() {
        return interaction;
    }

    public static void setInteraction(boolean interaction) {
        InteractionOfItems.interaction = interaction;
    }

    public static void init() {
        final Vector3 vector3 = new Vector3();
        final Vector3 destination = new Vector3();
        final Vector3 velocity = new Vector3();
        final Vector3 origin = new Vector3();
        final Vector3 oldPosition = new Vector3();
        final InteractionOfItems interaction = getInstance();

        CallBack.addCallBack(new Touch() {
            @Override
            public void main(Vector3 position) {
                if (!InteractionOfItems.interaction) return;
                float x = position.x * 10 - GameThread.xDraw, z = position.z * 10 - GameThread.zDraw;
                isDragged = !rectangle.contains(x, z - 10);
                if (!isDragged) return;
                rectangle.setCenter(x, z - 10);
                destination.set(x, 0, z - 10);
                velocity.x = (destination.x - vector3.x);
                velocity.z = (destination.z - vector3.z);
                origin.set(vector3);

            }
        });

        CallBack.addCallBack(new Drag() {
            @Override
            public void main(Vector3 position) {
                if (!InteractionOfItems.interaction) return;
                float x = position.x * 10 - GameThread.xDraw, z = position.z * 10 - GameThread.zDraw;
                rectangle.setCenter(x, z - 10);
                destination.set(x, 0, z - 10);
                velocity.x = (destination.x - vector3.x) * 0.2f;
                velocity.z = (destination.z - vector3.z) * 0.2f;
                oldPosition.set(vector3);
                origin.set(vector3);
            }
        });

        CallBack.addCallBack(new Render() {
            @Override
            public void main() {
                if (isDragged && InteractionOfItems.interaction) {
                    Integer id = interaction.settableItemsHashMap.get(interaction.container.getId());
                    if (id == null) {
                        isDragged = false;
                        rectangle.setCenter(-1000, -1000);
                        return;
                    }

                    if (!(Math.abs(vector3.x - destination.x) <= Math.abs(velocity.x) &&
                            Math.abs(vector3.z - destination.z) <= Math.abs(velocity.z))) {
                        vector3.add(velocity);
                        if (!(Math.abs(vector3.x - destination.x) - Math.abs(oldPosition.x - destination.x) < Math.abs(velocity.x) &&
                                Math.abs(vector3.z - destination.z) - Math.abs(oldPosition.z - destination.z) < Math.abs(velocity.z)))
                            vector3.set(destination);
                    } else
                        vector3.set(destination);
                    if (modelInstance == null) return;
                    Color.GREEN.a = 0.5f;
                    modelInstance.materials.peek().set(ColorAttribute.createDiffuse(Color.GREEN), new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
                    Block.getBlocks()[id].render(modelInstance, (float) (vector3.x - player.getPixelX()) + GameThread.xDraw, 5, (float) (vector3.z - player.getPixelZ()) + GameThread.zDraw);
                    return;
                }
                isDragged = false;
                rectangle.setPosition(-1000, -1000);
            }
        });
        CallBack.addCallBack(new TouchOnBlock() {

            private int oldId;

            @Override
            public void main(float x, float z) {
                if (!InteractionOfItems.interaction) return;
                interaction.builder.setLength(0);
                // Use the active hotbar container instead of just carriedItem
                interaction.container = player.getActiveHotbarContainer();
                // Check if container is valid and not empty before proceeding
                if (interaction.container == null || interaction.container.getItems().isEmpty()) {
                    interaction.id = null; // No item selected or container is empty
                    return; // Cannot interact without an item
                }

                interaction.id = interaction.settableItemsHashMap.get(interaction.container.getId());
                boolean set = true;
                if (interaction.id == null) { // If not a settable item, get the item's own ID for hit check
                    interaction.id = interaction.container.getId(); // Use the item's ID itself
                    set = false; // Cannot set this item as a block
                } else { // It IS a settable item (a block)
                    // Update preview model only if the settable block ID changed
                    if (oldId != interaction.id) { // Use interaction.id (block ID) here
                        modelInstance = new ModelInstance(Block.getBlock(interaction.id).getModelInstance().model);
                        oldId = interaction.id;
                    }
                }

                // Prevent interaction if dragging the placement preview
                if (isDragged && !World.isCollision(x, z - 1)) {
                    return;
                }

                // Check if hitting an existing block
                if (World.isCollision(x, z - 1)) {
                    // Ensure we have a valid item factory for the selected item and it's not a block type itself
                    ItemFactory selectedFactory = Item.getItemFactories()[interaction.container.getId()];
                    if (selectedFactory != null && player.getCoolDown() == 0 && selectedFactory.getType() != Item.BLOCK) {
                        interaction.hit(x, z); // Attempt to hit the block
                        return;
                    }
                }
                // Check if placing a block
                else if (set && !isDragged) { // 'set' is true if it's a placeable block item
                    interaction.set(x, z); // Attempt to place the block
                    isDragged = false; // Reset drag state after placement attempt
                }
                // If neither hitting nor placing, reset drag state
                else {
                   isDragged = false;
                }

                rectangle.setCenter(Integer.MIN_VALUE, Integer.MIN_VALUE);
            }
        });
    }

    public void addSettableItem(int itemId, int blockId) {
        settableItemsHashMap.put(itemId, blockId);
    }

    synchronized private void hit(float x, float z) {
        // Use the active container determined in the callback
        Container activeContainer = this.container; // Use the container set in the callback
        if (activeContainer == null || activeContainer.getItems().isEmpty() || player.getCoolDown() > 0 || player.getEndurance() < 2) return;

        // Get the item from the active container
        item = activeContainer.getLastElement(); // Assuming interaction always uses the last item?
        if (item == null) return; // Should not happen if container is not empty, but safety check

        // Decrement strength and potentially remove item
        item.getItemData().decrementStrength();
        if (item.getItemData().getStrength() <= 0) {
            activeContainer.delete(); // Remove the item from the active container
            item = null; // Item is broken/gone
        }

        Tile tile = World.getIntersectedTile(x, z - 1);

        assert tile != null;
        CustomBlock customBlock = tile.getBlock();

        BlockExtraData data = tile.getData();
        player.setCoolDown(player.getCoolDown() + item.getItemData().getCoolDown());
        // If item broke, we can't deal damage
        if (item == null) return;

        int blockLevel = tile.getBlock().getLevel();
        float damage = item.getItemData().getDamage();
        if (blockLevel != 0)
            damage = damage * ((float) item.getItemData().getLevel() / blockLevel);
        // Use the item's type for comparison, not the potentially outdated 'id' field
        if (item.getType() != customBlock.TYPE) {
            damage /= 10;
        }
        if (player.getEndurance() - item.getItemData().getTakeEndurance() < 0) {
            damage /= item.getItemData().getTakeEndurance() / player.getEndurance();
        }
        player.increaseEndurance(-item.getItemData().getTakeEndurance());

        data.setHealth(data.getHealth() - Math.round(damage));
        builder.append("player hit to block with id ").
                append(tile.getID()). // Use getID() instead of getBlockID()
                append(", used item with id ").
                append(item.getId()). // Log the ID of the item used
                append("; block health = ").
                append(data.getHealth()).
                append("; item damage = ").
                append(damage).
                append(";").append("player endurance = ").
                append(player.getEndurance()).
                append("; player coolDown = ").
                append(player.getCoolDown()).
                append(" item strength = ");
        // Log strength of the item actually used, if it still exists
        if (activeContainer.getItems().contains(item, true)) { // Check if item still exists
             builder.append(item.getItemData().getStrength());
        } else {
             builder.append("BROKEN");
        }
        builder.append("; x = ").
                append(x).
                append("; z = ").
                append(z);
        Gdx.app.log("PLAYER", builder.toString());

        if (data.getHealth() <= 0) {
            builder.setLength(0);
            builder.append("player destroyed block; x = ").
                    append(x).
                    append("; z = ").
                    append(z);
            if (customBlock.getDropID() != 0) {
                Entity.spawn(customBlock.getDropID(), x, z - 0.5f);
                builder.append("; drop = ").append(customBlock.getDropID());
            }
            tile.setBlockID(0);
            Gdx.app.log("PLAYER", builder.toString());
        }

    }

    private void set(float x, float z) {
        // Use the active container determined in the callback
        Container activeContainer = this.container;
        if (activeContainer == null || activeContainer.getItems().isEmpty() || World.isCollision(x, z - 1)) return;

        // 'id' (Integer) here should be the block ID determined in the callback
        // Check this.id for null BEFORE assigning to int
        if (this.id == null) return; // Check the Integer object for null
        int blockIdToPlace = this.id; // Now it's safe to assign to int

        builder.append("player set block with id ").
                append(blockIdToPlace).
                append(", used item with id ").
                append(activeContainer.getId()).
                append("; x = ").
                append(x).
                append("; z = ").
                append(z);
        Gdx.app.log("PLAYER", builder.toString());

        World.setBlock((rectangle.x + 5) / 10, (rectangle.y + 5) / 10, blockIdToPlace);
        activeContainer.delete();
        isDragged = false;
    }
}
