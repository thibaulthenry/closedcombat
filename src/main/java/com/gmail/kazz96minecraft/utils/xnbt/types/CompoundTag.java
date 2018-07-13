package com.gmail.kazz96minecraft.utils.xnbt.types;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * <p>
 * Contains a map of NBTTags. Uses the names of its values as keys.
 * </p>
 */
public class CompoundTag extends AbstractCompoundTag {

    public CompoundTag(final String name) {
        this(name, null);
    }

    public CompoundTag(final String name, final Map<String, NBTTag> payload) {
        super(name, payload);
    }

    @Override
    public Map<String, NBTTag> getPayload() {
        return Collections.unmodifiableMap(super.getPayload());
    }

    @Override
    public NBTTag put(final NBTTag tag) {
        if (tag == null) {
            throw new NullPointerException();
        }
        return super.getPayload().put(tag.getHeader().getName(), tag);
    }

    @Override
    public void putAll(final Collection<? extends NBTTag> tags) {
        for (final NBTTag tag : tags) {
            put(tag);
        }
    }

}
