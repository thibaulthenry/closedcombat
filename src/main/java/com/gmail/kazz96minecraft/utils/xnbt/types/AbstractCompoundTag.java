package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCompoundTag extends BaseTag implements Map<String, NBTTag> {

    private static Map<String, NBTTag> sanitizePayload(final Map<String, NBTTag> payload) {
        if (payload == null) {
            return new HashMap<>();
        }

        if (payload.containsKey(null) || payload.containsValue(null)) {
            throw new NullPointerException("payload must not contain null keys or values");
        }

        return new HashMap<>(payload);
    }

    protected AbstractCompoundTag(final String name, final Map<String, NBTTag> payload) {
        super(new TagHeader(BaseType.COMPOUND.Id(), name), sanitizePayload(payload));
    }

    @Override
    public void clear() {
        getPayload().clear();
    }

    @Override
    public boolean containsKey(final Object key) {
        return getPayload().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return getPayload().containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, NBTTag>> entrySet() {
        return getPayload().entrySet();
    }

    @Override
    public NBTTag get(final Object key) {
        return getPayload().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, NBTTag> getPayload() {
        return ((Map<String, NBTTag>) super.getPayload());
    }

    @Override
    public boolean isEmpty() {
        return getPayload().isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return getPayload().keySet();
    }

    public abstract NBTTag put(NBTTag tag);

    /**
     * <p>
     * Use {@link #put(NBTTag)} instead.
     * </p>
     */
    @Override
    public final NBTTag put(final String key, final NBTTag value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public abstract void putAll(Collection<? extends NBTTag> tags);

    @Override
    public void putAll(final Map<? extends String, ? extends NBTTag> m) {
        putAll(m.values());
    }

    @Override
    public NBTTag remove(final Object key) {
        return getPayload().remove(key);
    }

    public void setPayload(final Map<String, NBTTag> payload) {
        super.setPayload(sanitizePayload(payload));
    }

    @Override
    public int size() {
        return getPayload().size();
    }

    @Override
    public Collection<NBTTag> values() {
        return getPayload().values();
    }

}
