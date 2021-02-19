package com.intbyte.bw.core.game;

import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.intbyte.bw.gameAPI.environment.Chunck;
import com.intbyte.bw.gameAPI.environment.Tile;
import com.intbyte.bw.gameAPI.environment.World;

import java.util.Iterator;

import static com.intbyte.bw.gameAPI.graphic.Graphic.ENVIRONMENT;
import static com.intbyte.bw.gameAPI.graphic.Graphic.MODEL_BATCH;

public class ChuncksRender extends FrustumCullingRender {

    private ModelCache modelCache = new ModelCache();
    @Override
    protected void draw(int x, int z) {
        if (!camera3d.frustum.boundsInFrustum(x * 10f, 0, z * 10f - 5, 5, 0, 5)) return;
        int id = World.getLandBlock(x + ((int) player.getX()), z + ((int) player.getZ()));
        landBlocks[id].render(x * 10f, 0, z * 10f - 5);
        GameThread.visible++;
    }

    protected void draw2(int x, int z) {

        if (!camera3d.frustum.boundsInFrustum(x * 10f, 0, z * 10f, 20, 0, 20)) return;
        Chunck chunck = World.world[World.fixedIndex(World.playerX + x / 2)][World.fixedIndex(World.playerZ + z / 2)];
        for (Iterator<Tile> iterator = chunck.getTiles().iterator(); iterator.hasNext(); ) {
            Tile tile = iterator.next();
            if (tile.getID() == 0) {
                iterator.remove();
                continue;
            }
            tile.render((float) (tile.getPosition().x - player.getPixelX() + GameThread.xDraw), 0, (float) (tile.getPosition().z - player.getPixelZ() + GameThread.zDraw));
            GameThread.visible++;
        }

    }

    protected void draw2(int x, int xTo, int z, int zTo) {

        for (; x < xTo; x += 2)
            for (int zz = z; zz > zTo; zz -= 2)
                draw2(x, zz);

    }

    @Override
    protected void draw(int x, int xTo, int z, int zTo) {
        draw2(x, xTo, z, zTo);
        draw2(x, xTo, z, zTo);
        for (; x < xTo; x++)
            for (int zz = z; zz > zTo; zz--)
                draw(x, zz);


    }
}
