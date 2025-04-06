package com.intbyte.bw.engine.block;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3; // Added import for Vector3
import com.intbyte.bw.engine.utils.json_wrapper.BlockWrapper;
import com.intbyte.bw.engine.physic.PhysicBlockObject;
import com.intbyte.bw.engine.utils.ID;
import com.intbyte.bw.engine.utils.Resource;


public class Block {

    public static final int STONE = 0, WOOD = 1;
    static CustomBlock[] blocks = new CustomBlock[1200];
    static CustomBlock[] landBlocks = new CustomBlock[1200];

    public static CustomBlock[] getBlocks() {
        return blocks;
    }

    public static CustomBlock getBlock(int id){
        return blocks[id];
    }
    public static CustomBlock[] getLandBlocks() {
        return landBlocks;
    }





    private static ModelInstance getModelInstance(String pathToModel, String pathToTexture) {

        ModelInstance instance = Resource.createModelInstance(pathToModel);
        if(pathToTexture.equals("null")) return instance;
        TextureAttribute textureAttribute1 = new TextureAttribute(TextureAttribute.Diffuse, Resource.getSprite(pathToTexture));
        Material material = instance.materials.get(0);
        material.clear();
        material.set(textureAttribute1);

        return instance;
    }

    // Helper method to create and configure a CustomBlock
    private static CustomBlock createBlock(int id, int health, int type, String stringId, int level, String modelPath, String texturePath, float scale, Vector3 renderOffset, float iconScale, Vector3 iconRenderOffset, PhysicBlockObject body) {
        CustomBlock block = new CustomBlock(health, type, id);
        block.setId(stringId);
        block.setLevel(level);
        block.setModelInstance(getModelInstance(modelPath, texturePath));
        block.setScale(iconScale); // Set icon scale first for icon generation
        block.setPosition(iconRenderOffset); // Set icon offset for icon generation
        block.updateIcon(); // Generate icon before changing scale/position
        block.setPhysicEntity(body);
        block.setScale(scale); // Set world scale
        block.setPosition(renderOffset); // Set world offset
        blocks[id] = block;
        Gdx.app.log("BLOCK", "defined block " + stringId + " with id " + id);
        return block;
    }


    public static void defineLandBlock(String id, String texture) {
        int integerId = ID.registeredId("land_block:" + id);
        CustomBlock block = new CustomBlock(-1, -1, integerId); // Health and Type are irrelevant for land blocks?
        block.setId(id);
        block.setModelInstance(getModelInstance("block/landblock.obj", texture));
        block.setLand(true);
        landBlocks[integerId] = block;
        Gdx.app.log("BLOCK", "defined land block " + id + " with id " + integerId);
    }

    /**
     * Defines a block using parameters from a BlockWrapper (likely loaded from JSON).
     * @param wrapper Data object containing block properties.
     * @return The created CustomBlock instance.
     */
    public static CustomBlock defineBlock(BlockWrapper wrapper) {
        int id = ID.registeredId("block:" + wrapper.getId());
        return createBlock(
                id,
                wrapper.getHealth(),
                wrapper.getType(),
                wrapper.getId(),
                wrapper.getLevel(),
                "block/" + wrapper.getModel(), // Assuming models are always in 'block/' subdirectory
                wrapper.getTexture(),
                wrapper.getScale(),
                wrapper.getRender(),
                wrapper.getIconScale(),
                wrapper.getIconRender(),
                wrapper.getBody()
        );
    }

    /**
     * Defines a block directly using code parameters.
     * @param stringId Unique string identifier for the block (e.g., "grass", "stone_wall").
     * @param health Maximum health of the block.
     * @param type Block type (e.g., Block.STONE, Block.WOOD).
     * @param level Block level.
     * @param modelPath Path to the model file (relative to assets/objects/).
     * @param texturePath Path to the texture file (relative to assets/textures/ or null).
     * @param scale Scale of the block model in the world.
     * @param renderOffset Offset for rendering the block model in the world.
     * @param iconScale Scale for rendering the block's icon.
     * @param iconRenderOffset Offset for rendering the block's icon.
     * @param body Optional physical body definition.
     * @return The created CustomBlock instance.
     */
    public static CustomBlock defineBlock(String stringId, int health, int type, int level, String modelPath, String texturePath, float scale, Vector3 renderOffset, float iconScale, Vector3 iconRenderOffset, PhysicBlockObject body) {
        int id = ID.registeredId("block:" + stringId);
        return createBlock(id, health, type, stringId, level, modelPath, texturePath, scale, renderOffset, iconScale, iconRenderOffset, body);
    }

    public static void setDropID(int blockID, int dropID) {
        blocks[blockID].setDropID(dropID);
    }

    public static void setDropID(String blockID, String dropID) {
        setDropID(ID.get("block:" + blockID),ID.get("entity:" + dropID));
    }

}
