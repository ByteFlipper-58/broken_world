package com.intbyte.bw.gameAPI.callbacks;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public interface Drag {
    Array<Drag> callBacks = new Array<>();

    void main(Vector3 position);
}
