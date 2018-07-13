package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

/**
 * <p>
 * Contains an int[].
 * </p>
 */
public class IntegerArrayTag extends BaseTag {

    private static int[] sanitizePayload(final int[] payload) {
        return payload == null ? new int[0] : payload;
    }

    public IntegerArrayTag(final String name) {
        this(name, null);
    }

    public IntegerArrayTag(final String name, final int[] payload) {
        super(new TagHeader(BaseType.INTEGER_ARRAY.Id(), name), sanitizePayload(payload));
    }

    @Override
    public int[] getPayload() {
        return (int[]) super.getPayload();
    }

    public void setPayload(final int[] payload) {
        super.setPayload(sanitizePayload(payload));
    }

}
