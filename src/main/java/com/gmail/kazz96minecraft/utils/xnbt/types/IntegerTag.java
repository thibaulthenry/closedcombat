package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

/**
 * <p>
 * Contains an Integer.
 * </p>
 */
public class IntegerTag extends BaseTag {

    public IntegerTag(final String name) {
        this(name, 0);
    }

    public IntegerTag(final String name, final int payload) {
        super(new TagHeader(BaseType.INTEGER.Id(), name), payload);
    }

    @Override
    public Integer getPayload() {
        return (int) super.getPayload();
    }

    public void setPayload(final int payload) {
        super.setPayload(payload);
    }

}
