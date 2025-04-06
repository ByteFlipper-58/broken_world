package com.intbyte.bw.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.intbyte.bw.engine.block.Block;
import com.intbyte.bw.engine.block.CustomBlock;
import com.intbyte.bw.engine.callbacks.CallBack;
import com.intbyte.bw.engine.callbacks.Initialization;
import com.intbyte.bw.engine.entity.DropData;
import com.intbyte.bw.engine.entity.DropFactory;
import com.intbyte.bw.engine.entity.Entity;
import com.intbyte.bw.engine.entity.Player;
import com.intbyte.bw.engine.item.Container;
import com.intbyte.bw.engine.item.Item;
import com.intbyte.bw.engine.item.ItemFactory;
import com.intbyte.bw.engine.physic.PhysicBlockObject;
import com.intbyte.bw.engine.render.PointLight;
import com.intbyte.bw.engine.ui.GUI;
import com.intbyte.bw.engine.ui.Panel;
import com.intbyte.bw.engine.utils.ID;
import com.intbyte.bw.engine.utils.Resource;
import com.intbyte.bw.engine.world.World;
import com.intbyte.bw.game.gameUI.InventoryLayerUI;
import com.intbyte.bw.game.gameUI.MainLayerUI;


public class Game {
    Player player;


    public void main() {

        CallBack.addCallBack(new Initialization() {
            @Override
            public void main() {
                ID.registeredId("block:void", 0);
                // GeneratedJsonData.init(); // Block definitions moved below

                Sprite sprite = Panel.drawPanel(400, 400, 6, 0.1f, 0.1f, 0.1f, 0.6f - 0.1f);
                Resource.addSprite(sprite, "slot");

                Block.defineLandBlock("grass", "grass.jpg");

                // Define common physic object
                PhysicBlockObject defaultPhysicObject = new PhysicBlockObject();
                defaultPhysicObject.setShape(new com.badlogic.gdx.math.Rectangle(0, 0, 2, 2)); // Use fully qualified name to avoid import collision if any
                defaultPhysicObject.setOffset(1, 1);
                Resource.putBlockObject("default_block_body", defaultPhysicObject); // Register it if needed elsewhere

                // Define blocks directly using the new method
                Block.defineBlock("grass2", 10, Block.WOOD, 20, "block/fallen_tree_1.obj", "Lp_trees_texture_atlas.png", 20f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 20f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), defaultPhysicObject);
                Block.defineBlock("mashroom", 10, Block.WOOD, 20, "block/_mashroom_2.g3db", "Ekfs_bush_map.png", 0.1f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 0.1f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), defaultPhysicObject);
                Block.defineBlock("spruce", 20, Block.WOOD, 2, "block/spruce_1.obj", "Lp_trees_texture_atlas.png", 4f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 1f, new com.badlogic.gdx.math.Vector3(0f,0f,0f), defaultPhysicObject);
                Block.defineBlock("little_spruce", 5, Block.WOOD, 2, "block/spruce_1.obj", "Lp_trees_texture_atlas.png", 1.5f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 1f, new com.badlogic.gdx.math.Vector3(0f,0f,0f), defaultPhysicObject);
                Block.defineBlock("stone", 10, Block.STONE, 20, "block/_stone_3.g3db", "Ekfs_bush_map.png", 0.1f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 0.1f, new com.badlogic.gdx.math.Vector3(2f,2.5f,2f), defaultPhysicObject);
                Block.defineBlock("oak", 100, Block.WOOD, 1, "block/oak_1.obj", "Lp_trees_texture_atlas.png", 4f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 1f, new com.badlogic.gdx.math.Vector3(0f,0f,0f), defaultPhysicObject);
                Block.defineBlock("oak_2", 100, Block.WOOD, 1, "block/oak_2.obj", "Lp_trees_texture_atlas.png", 20f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 1f, new com.badlogic.gdx.math.Vector3(0f,0f,0f), defaultPhysicObject);
                Block.defineBlock("light", 10, Block.WOOD, 20, "block/Old Lantern Model.obj", "null", 3f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), 3f, new com.badlogic.gdx.math.Vector3(0f,-5f,0f), defaultPhysicObject);


                player = Player.getPlayer();

                // Define Tools/Items (must be after blocks/icons are defined)
                com.intbyte.bw.engine.item.Tools.newPickaxe("pickaxe","pickaxe.png",20,10,10,100f / 12 * 1,10,1);
                // Add Axe definition (using placeholder icon "pickaxe.png" for now)
                com.intbyte.bw.engine.item.Tools.newAxe("axe","pickaxe.png",15,8,12,100f / 10 * 1, 8, 1); // Set weight to 1
                com.intbyte.bw.engine.item.Tools.newBlock("block_grass2","icon:grass2","grass2",0.1); // Set weight to 1 (bush)
                com.intbyte.bw.engine.item.Tools.newBlock("block_mashroom","icon:mashroom","mashroom",0.1); // Set weight to 1 (bush)
                com.intbyte.bw.engine.item.Tools.newBlock("block_spruce","icon:spruce","spruce",0); // Set weight to 0
                com.intbyte.bw.engine.item.Tools.newBlock("block_little_spruce","icon:little_spruce","little_spruce",0); // Set weight to 0
                com.intbyte.bw.engine.item.Tools.newBlock("block_stone","icon:stone","stone",0.1); // Set weight to 1
                com.intbyte.bw.engine.item.Tools.newBlock("block_oak","icon:oak","oak",0); // Set weight to 0
                com.intbyte.bw.engine.item.Tools.newBlock("block_oak_2","icon:oak_2","oak_2",0); // Set weight to 0
                com.intbyte.bw.engine.item.Tools.newBlock("block_light","icon:light","light",0); // Set weight to 0

                // Define drops (must be after blocks and items are defined)
                Entity.addFactory(new DropFactory("stone", "test_drop", new DropData("block_stone", 1, 1)));
                Block.setDropID("stone", "test_drop");
                Entity.addFactory(new DropFactory("grass2", "test_drop2", new DropData("block_grass2", 1, 1)));
                Block.setDropID("grass2", "test_drop2");
                // Add drops for other blocks if needed...


                GUI.putLayer("main", new MainLayerUI());
                GUI.putLayer("inventory", new InventoryLayerUI());
                GUI.setLayer("main", null);

                // Configure specific blocks (like light)
                CustomBlock lightBlock = Block.getBlock(ID.get("block:light"));
                if (lightBlock != null) { // Check if block exists
                    lightBlock.setGlowing(true);
                    PointLight pointLight = new PointLight();
                    pointLight.getLandPointLight().set(Color.YELLOW,0, 30, 0, 1000);
                    pointLight.getPointLight().set(Color.YELLOW,0, 20, 0, 500);
                    lightBlock.setPointLight(pointLight);
                } else {
                    com.badlogic.gdx.Gdx.app.error("GAME_INIT", "Light block not found after definition!");
                }


                player.setPosition(10000, 100);
                Container container = new Container(64);
                // Removed duplicated lines that were here
                if (World.getConfig().isCreative())
                    for (int i = 1; i < Item.getItemFactories().length; i++) {
                        ItemFactory factory;
                        if ((factory = Item.getItemFactories()[i]) == null) break;

                        int countToAdd;
                        // Check item type from factory
                        int itemType = factory.getType();
                        if (itemType == Item.PICKAXE || itemType == Item.AXE || itemType == Item.SWARD) {
                            countToAdd = 1; // Only add 1 tool
                        } else {
                            countToAdd = factory.getStacksize(); // Add full stack for other items
                        }

                        // Ensure container max size is appropriate (at least countToAdd, or 1 for tools)
                        container.setMaxCountItems(Math.max(countToAdd, 1));
                        container.clear();

                        // Add the calculated number of items
                        player.takeDrop(container.addItems(Item.newItems(factory.getId(), countToAdd)));
                    }
            }
        });
    }
}
