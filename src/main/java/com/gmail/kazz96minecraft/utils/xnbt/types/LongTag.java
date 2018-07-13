package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

/**
 * <p>
 * Contains a Long.
 * </p>
 */
public class LongTag extends BaseTag {

    public LongTag(final String name) {
        this(name, 0L);
    }

    public LongTag(final String name, final long payload) {
        super(new TagHeader(BaseType.LONG.Id(), name), payload);
    }

    @Override
    public Long getPayload() {
        return (long) super.getPayload();
    }

    public void setPayload(final long payload) {
        super.setPayload(payload);
    }

}
