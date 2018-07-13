package com.gmail.kazz96minecraft.utils.xnbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag.BaseType;

/**
 * <p>
 * Writes {@link NBTTag}s to a DataOutputStream.
 * </p>
 * <p>
 * Supports extended tags. See {@link XNBT.TagIOHandler} for more details.
 * </p>
 */
public class NBTOutputStream extends DataOutputStream {

    /**
     * Creates a new NBTOutputStream.
     *
     * @param out
     *            the DataOutputStream to write to
     */
    public NBTOutputStream(final OutputStream out) {
        super(out);
    }

    /**
     * <p>
     * Writes a {@link NBTTag} to this stream.
     * </p>
     *
     * @param tag
     *            the tag to write
     * @throws IOException
     *             under the following conditions:
     *             <ul>
     *             <li>If the given NBTTag is not a {@link BaseType} and there is no registered {@link XNBT.TagPayloadWriter}
     *             .</li>
     *             <li>If the given NBTTag is a BaseType and the the payload is null.</li>
     *             <li>Any exception caused by the underlying stream.</li>
     *             </ul>
     */
    public void writeTag(final NBTTag tag) throws IOException {
        final byte type = tag.getHeader().getType();
        writeByte(type);

        if (type == BaseType.END.Id()) {
            return;
        }

        if (type < BaseType.values().length && tag.getPayload() == null) {
            throw new IOException(new StringBuilder()
                    .append("null payload in base tag ").append(tag.getHeader().getName())
                    .append(" (").append(BaseType.byId(type).name()).append(")").toString());
        }

        writeUTF(tag.getHeader().getName());
        XNBT.getWriter(type).write(tag.getPayload(), this);

    }
}
