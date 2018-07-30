package com.gmail.kazz96minecraft.utils.xnbt.types;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * A NBTTag.
 * </p>
 */
public interface NBTTag {

    /**
     * <p>
     * Contains all base NBT types.
     * </p>
     */
    enum BaseType {

        XNBT((byte) -1),
        END((byte) 0),
        BYTE((byte) 1),
        SHORT((byte) 2),
        INTEGER((byte) 3),
        LONG((byte) 4),
        FLOAT((byte) 5),
        DOUBLE((byte) 6),
        BYTE_ARRAY((byte) 7),
        STRING((byte) 8),
        LIST((byte) 9),
        COMPOUND((byte) 10),
        INTEGER_ARRAY((byte) 11);

        private static final Map<Byte, BaseType> idMap = new HashMap<>();

        static {
            for (final BaseType e : values()) {
                idMap.put(e.id, e);
            }
        }

        /**
         * @param id
         *            the id
         * @return the BaseType of the given id
         */
        public static BaseType byId(final byte id) {
            return idMap.get(id);
        }

        private final byte id;

        BaseType(final byte id) {
            this.id = id;
        }

        /**
         * @return the id
         */
        public byte Id() {
            return id;
        }

    }

    /**
     * @return the TagHeader
     */
    TagHeader getHeader();

    /**
     * @return the payload
     */
    Object getPayload();

}
