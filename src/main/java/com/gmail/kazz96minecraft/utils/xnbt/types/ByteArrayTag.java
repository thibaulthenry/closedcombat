package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

/**
 * <p>
 * Contains a byte[].
 * </p>
 */
public class ByteArrayTag extends BaseTag {

    private static byte[] sanitizePayload(final byte[] payload) {
        return payload == null ? new byte[0] : payload;
    }

    public ByteArrayTag(final String name) {
        this(name, null);
    }

    public ByteArrayTag(final String name, final byte[] payload) {
        super(new TagHeader(BaseType.BYTE_ARRAY.Id(), name), sanitizePayload(payload));
    }

    @Override
    public byte[] getPayload() {
        return (byte[]) super.getPayload();
    }

    public void setPayload(final byte[] payload) {
        super.setPayload(sanitizePayload(payload));
    }

}
