package com.intbyte.bw.gameAPI.graphic.ui.container;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.intbyte.bw.gameAPI.environment.Item;
import com.intbyte.bw.gameAPI.graphic.Graphic;
import com.intbyte.bw.gameAPI.utils.Resource;

public class Slot extends Actor {
    private final static BitmapFont font = new BitmapFont();
    private final SlotSkin slotSkin;
    private final Container container;
    private float itemSize;
    private final TakenItems takenItems = TakenItems.getInstance();
    private boolean isSelect;
    private boolean drag;
    public Slot(SlotSkin skin, final Container container) {
        slotSkin = skin;
        this.container = container;
        addListener(new InputListener() {

            boolean take() {
                if (container.items.isEmpty()) {
                    takenItems.isTaken = false;
                    takenItems.isSelect = false;
                    return false;
                }

                takenItems.moveItems(container.items);
                return true;
            }

            void put() {
                container.moveItems(takenItems.items);
                if (takenItems.items.isEmpty()) {
                    takenItems.isTaken = false;
                    takenItems.isSelect = false;
                    isSelect = false;
                }
            }

            void swap() {
                if (takenItems.isSelect && takenItems.selectItems != null && !isSelect) {
                    container.moveItems(takenItems.selectItems.items);
                    takenItems.selectItems = null;
                    takenItems.clear = true;
                    drag = false;
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (hit(x, y, true) == null) return;
                drag = true;

                swap();

                if(!container.items.isEmpty()) {
                    if (isSelect = !isSelect) {
                        takenItems.isSelect = isSelect;
                        takenItems.selectItems = container;
                    }
                    if (takenItems.isTaken || !takenItems.isSelect) {
                        takenItems.isSelect = false;
                        isSelect = false;
                    }
                }

                if (drag && (!isSelect))
                    if (takenItems.items.isEmpty()) {
                        if (take()) {
                            takenItems.isTaken = true;
                        }
                    } else
                        put();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (takenItems.isTaken || !takenItems.isSelect || takenItems.clear) {
            isSelect = false;
            takenItems.clear = false;
            takenItems.isSelect = false;
        }
        batch.draw(isSelect ? slotSkin.getSelectedTexture() : slotSkin.getSprite(), getX(), getY(), getWidth(), getHeight());
        if (container.items.notEmpty()) {
            container.items.get(0).drawIcon(getX() + getWidth() * 0.1f, getY() + getHeight() * 0.1f, itemSize, itemSize);
            font.getData().setScale((18 / (18 / (getHeight() * 0.2f))) / 10);
            font.setColor(slotSkin.getCountTextColor());
            String value = String.valueOf(container.getCountItems());
            font.draw(Graphic.BATCH, value, getX() + getWidth() * 0.8f - (font.getXHeight() * value.length() - font.getXHeight()), getY() + font.getLineHeight() * 0.9f);
        }
    }

    public void addItems(Item... items) {
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

    public static interface SlotSkin {
        SlotSkin DEFAULT = new SlotSkin() {
            private final Sprite texture = Resource.getSprite("testPanel"),
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
}
