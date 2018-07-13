package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

/**
 * <p>
 * Contains an Float.
 * </p>
 */

public class FloatTag extends BaseTag {

    public FloatTag(final String name) {
        this(name, 0F);
    }

    public FloatTag(final String name, final float payload) {
        super(new TagHeader(BaseType.FLOAT.Id(), name), payload);
    }

    @Override
    public Float getPayload() {
        return (float) super.getPayload();
    }

    public void setPayload(final float payload) {
        super.setPayload(payload);
    }

}
