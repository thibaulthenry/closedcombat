package com.gmail.kazz96minecraft.utils.xnbt.types;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Contains a list of a single type of NBTTags.
 * </p>
 */
public class ListTag extends AbstractListTag {

    public ListTag(final String name) {
        this(name, null);
    }

    public ListTag(final String name, final List<NBTTag> payload) {
        super(name, payload);
    }

    @Override
    public boolean add(final NBTTag e) {
        if (e == null) {
            throw new NullPointerException();
        }

        if (e.equals(EndTag.INSTANCE)) {
            throw new IllegalArgumentException("ListTag may not contain EndTags");
        }

        if (!isEmpty() && e.getHeader().getType() != getContentType()) {
            throw new IllegalArgumentException("NBTTag of type " + e.getHeader().getType()
                    + " can't be added to ListTag because it already contains one or more elements of type "
                    + getPayload().get(0).getHeader().getType());
        }

        return super.getPayload().add(e);
    }

    @Override
    public boolean addAll(final Collection<? extends NBTTag> c) {
        boolean r = false;

        for (final NBTTag e : c) {
            if (add(e)) {
                r = true;
            }
        }

        return r;
    }

    public byte getContentType() {
        return isEmpty() ? 0 : getPayload().get(0).getHeader().getType();
    }

    @Override
    public List<NBTTag> getPayload() {
        return Collections.unmodifiableList(super.getPayload());
    }

}
