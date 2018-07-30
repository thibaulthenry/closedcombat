package com.gmail.kazz96minecraft.utils.xnbt;

import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.TagHeader;

/**
 * <p>
 * The base implementation which all tag types extend from.
 * </p>
 */
public class BaseTag implements NBTTag {

    private final TagHeader header;

    private Object payload;

    /**
     * <p>
     * Creates a new BaseTag.
     * </p>
     *
     * @param header
     *            the header
     * @param payload
     *            the payload
     */
    protected BaseTag(final TagHeader header, final Object payload) {
        this.header = header;
        this.payload = payload;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseTag other = (BaseTag) obj;
        if (header == null) {
            return other.header == null;
        } else return header.equals(other.header);
    }

    @Override
    public TagHeader getHeader() {
        return header;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        return result;
    }

    /**
     * @param payload
     *            the new payload
     */
    protected void setPayload(final Object payload) {
        this.payload = payload;
    }

    Class<?> getReturnType() {
        try {
            return getClass().getMethod("getPayload").getReturnType();
        } catch (final Exception e) {}
        return null;
    }

}
