package com.intbyte.bw.game;


import com.badlogic.gdx.math.Rectangle;
import com.intbyte.bw.engine.block.Block;
import com.intbyte.bw.engine.block.CustomBlock;
import com.intbyte.bw.engine.item.Tools;
import com.intbyte.bw.engine.physic.PhysicBlockObject;
import com.intbyte.bw.engine.utils.Resource;
import com.intbyte.bw.engine.utils.json_wrapper.BlockWrapper;

public class GeneratedJsonData{
    public static void init(){
        BlockWrapper blockWrapper = new BlockWrapper();
        CustomBlock block;
        PhysicBlockObject object = new PhysicBlockObject();
        object.setShape(new Rectangle(0,0,2,2));
        object.setOffset(1, 1);
        Resource.putBlockObject("10x10",object);
        
        blockWrapper.reset();
        blockWrapper.setId("grass2");
		blockWrapper.setModel("fallen_tree_1.obj");
		blockWrapper.setTexture("Lp_trees_texture_atlas.png");
		blockWrapper.setHealth(10);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(20f);
		blockWrapper.getIconRender().set(0f,-5f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(20);
		block = Block.defineBlock(blockWrapper);
		block.setScale(20f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("mashroom");
		blockWrapper.setModel("_mashroom_2.g3db");
		blockWrapper.setTexture("Ekfs_bush_map.png");
		blockWrapper.setHealth(10);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(0.1f);
		blockWrapper.getIconRender().set(0f,-5f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(20);
		block = Block.defineBlock(blockWrapper);
		block.setScale(0.1f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("spruce");
		blockWrapper.setModel("spruce_1.obj");
		blockWrapper.setTexture("Lp_trees_texture_atlas.png");
		blockWrapper.setHealth(20);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(1f);
		blockWrapper.getIconRender().set(0f,0f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(2);
		block = Block.defineBlock(blockWrapper);
		block.setScale(4f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("little_spruce");
		blockWrapper.setModel("spruce_1.obj");
		blockWrapper.setTexture("Lp_trees_texture_atlas.png");
		blockWrapper.setHealth(5);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(1f);
		blockWrapper.getIconRender().set(0f,0f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(2);
		block = Block.defineBlock(blockWrapper);
		block.setScale(1.5f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("stone");
		blockWrapper.setModel("_stone_3.g3db");
		blockWrapper.setTexture("Ekfs_bush_map.png");
		blockWrapper.setHealth(10);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(0.1f);
		blockWrapper.getIconRender().set(2f,2.5f,2f);
		blockWrapper.setType(Block.STONE);
		blockWrapper.setLevel(20);
		block = Block.defineBlock(blockWrapper);
		block.setScale(0.1f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("oak");
		blockWrapper.setModel("oak_1.obj");
		blockWrapper.setTexture("Lp_trees_texture_atlas.png");
		blockWrapper.setHealth(100);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(1f);
		blockWrapper.getIconRender().set(0f,0f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(1);
		block = Block.defineBlock(blockWrapper);
		block.setScale(4f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("oak_2");
		blockWrapper.setModel("oak_2.obj");
		blockWrapper.setTexture("Lp_trees_texture_atlas.png");
		blockWrapper.setHealth(100);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(1f);
		blockWrapper.getIconRender().set(0f,0f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(1);
		block = Block.defineBlock(blockWrapper);
		block.setScale(20f);
		block.setPosition(0f,-5f,0f);
        
        blockWrapper.reset();
        blockWrapper.setId("light");
		blockWrapper.setModel("Old Lantern Model.obj");
		blockWrapper.setTexture("null");
		blockWrapper.setHealth(10);
		blockWrapper.setBody(object);
		blockWrapper.setIconScale(3f);
		blockWrapper.getIconRender().set(0f,-5f,0f);
		blockWrapper.setType(Block.WOOD);
		blockWrapper.setLevel(20);
		block = Block.defineBlock(blockWrapper);
		block.setScale(3f);
		block.setPosition(0f,-5f,0f);


		Tools.newPickaxe("pickaxe","pickaxe.png",20,10,10,100f / 12 * 1,10,1);
		Tools.newBlock("block_grass2","icon:grass2","grass2",0);
		Tools.newBlock("block_mashroom","icon:mashroom","mashroom",0);
		Tools.newBlock("block_spruce","icon:spruce","spruce",0);
		Tools.newBlock("block_little_spruce","icon:little_spruce","little_spruce",0);
		Tools.newBlock("block_stone","icon:stone","stone",0);
		Tools.newBlock("block_oak","icon:oak","oak",0);
		Tools.newBlock("block_oak_2","icon:oak_2","oak_2",0);
		Tools.newBlock("block_light","icon:light","light",1);
    }
}