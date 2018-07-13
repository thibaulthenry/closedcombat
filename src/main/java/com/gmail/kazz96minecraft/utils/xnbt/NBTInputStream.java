package com.gmail.kazz96minecraft.utils.xnbt;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.gmail.kazz96minecraft.utils.xnbt.types.EndTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag.BaseType;

/**
 * <p>
 * Reads {@link NBTTag}s from a DataInputStream.
 * </p>
 * <p>
 * Supports extended tags. See {@link XNBT.TagIOHandler} for more details.
 * </p>
 */
public class NBTInputStream extends DataInputStream {

    /**
     * Creates a new NBTInputStream.
     *
     * @param in
     *            the DataInputStream to read from
     */
    public NBTInputStream(final InputStream in) {
        super(in);
    }

    /**
     * <p>
     * Reads a {@link NBTTag} from this stream.
     * </p>
     *
     * @return a NBTTag
     * @throws IOException
     *             if the NBTTag is not a BaseType and there is no registered {@link XNBT.TagPayloadReader}
     */
    public NBTTag readTag() throws IOException {
        final byte type = readByte();

        if (type == BaseType.END.Id()) {
            return EndTag.INSTANCE;
        }

        return XNBT.getBuilder(type).build(type, readUTF(), XNBT.getReader(type).read(this));

    }

}
