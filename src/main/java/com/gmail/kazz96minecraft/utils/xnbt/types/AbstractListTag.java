package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

import java.util.*;

public abstract class AbstractListTag extends BaseTag implements List<NBTTag> {

    private static List<NBTTag> sanitizePayload(final List<NBTTag> payload) {

        if (payload == null) {
            return new ArrayList<>();
        }

        byte t = -1;

        for (final NBTTag e : payload) {

            if (e == null) {
                throw new NullPointerException("payload must not contain null elements");
            }

            if (t == -1) {
                t = e.getHeader().getType();
                if (t == BaseType.END.Id()) {
                    throw new IllegalArgumentException("ListTag must not contain EndTags");
                }
            } else if (t != e.getHeader().getType()) {
                throw new IllegalArgumentException("NBTTag of type " + e.getHeader().getType()
                        + " can't be added to ListTag because it already contains one or more elements of type " + t);
            }

        }

        return payload;
    }

    protected AbstractListTag(final String name, final List<NBTTag> payload) {
        super(new TagHeader(BaseType.LIST.Id(), name), sanitizePayload(payload));
    }

    /**
     * <p>
     * Use {@link #add(NBTTag)} instead.
     * </p>
     */
    @Override
    public final void add(final int index, final NBTTag element) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Use {@link #addAll(Collection)} instead.
     * </p>
     */
    @Override
    public final boolean addAll(final int index, final Collection<? extends NBTTag> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        getPayload().clear();
    }

    @Override
    public boolean contains(final Object o) {
        return getPayload().contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return getPayload().containsAll(c);
    }

    @Override
    public NBTTag get(final int index) {
        return getPayload().get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NBTTag> getPayload() {
        return (List<NBTTag>) super.getPayload();
    }

    @Override
    public int indexOf(final Object o) {
        return getPayload().indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return getPayload().isEmpty();
    }

    @Override
    public Iterator<NBTTag> iterator() {
        return getPayload().iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
        return getPayload().lastIndexOf(o);
    }

    @Override
    public ListIterator<NBTTag> listIterator() {
        return getPayload().listIterator();
    }

    @Override
    public ListIterator<NBTTag> listIterator(final int index) {
        return getPayload().listIterator(index);
    }

    @Override
    public NBTTag remove(final int index) {
        return getPayload().remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        return getPayload().remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return getPayload().removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return getPayload().retainAll(c);
    }

    /**
     * <p>
     * Can not set specific element.
     * </p>
     */
    @Override
    public final NBTTag set(final int index, final NBTTag element) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void setPayload(final List<NBTTag> payload) {
        super.setPayload(payload == null ? new ArrayList<>() : sanitizePayload(payload));
    }

    @Override
    public int size() {
        return getPayload().size();
    }

    @Override
    public List<NBTTag> subList(final int fromIndex, final int toIndex) {
        return getPayload().subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return getPayload().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return getPayload().toArray(a);
    }

}
