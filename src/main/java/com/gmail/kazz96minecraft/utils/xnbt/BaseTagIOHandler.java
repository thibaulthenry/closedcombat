package com.gmail.kazz96minecraft.utils.xnbt;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.kazz96minecraft.utils.xnbt.types.ByteArrayTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.ByteTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.CompoundTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.DoubleTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.EndTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.FloatTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.IntegerArrayTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.IntegerTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.ListTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.LongTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag.BaseType;
import com.gmail.kazz96minecraft.utils.xnbt.types.ShortTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.StringTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.TagHeader;
import com.gmail.kazz96minecraft.utils.xnbt.types.XNBTTag;

abstract class BaseTagIOHandler implements XNBT.TagIOHandler {

    private static final XNBT.TagIOHandler[] handlers = new BaseTagIOHandler[BaseType.values().length];

    static final XNBT.TagIOHandler xnbtHandler = new XNBT.TagIOHandler() {

        @Override
        public XNBTTag build(final byte type, final String name, final Object payload) {
            return new XNBTTag(payload);
        }

        @Override
        public Object read(final NBTInputStream in) throws IOException {
            Object r;

            final CompoundTag root = (CompoundTag) in.readTag();
            final CompoundTag settings = (CompoundTag) root.get(".settings");
            final CompoundTag document = (CompoundTag) root.get(((StringTag) settings.get("name")).getPayload());
            Class<? extends Object> docClass;

            try {
                docClass = Class.forName(((StringTag) settings.get("class")).getPayload());
            } catch (final ClassNotFoundException e) {
                throw new IOException("class not found: " + ((StringTag) settings.get("class")).getPayload(), e);
            }

            try {
                r = docClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IOException("failed to instantiate " + docClass.getName(), e);
            }

            final Map<String, Field> fields = new HashMap<>();
            for (final Field f : r.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(Tag.class)) {
                    fields.put(f.getAnnotation(Tag.class).name().isEmpty() ? f.getName() : f.getAnnotation(Tag.class).name(), f);
                }
            }

            for (final String fieldName : document.keySet()) {

                if (!fields.containsKey(fieldName)) {
                    continue;
                }

                final Field field = fields.get(fieldName);

                field.setAccessible(true);
                try {
                    field.set(r, tagToObject(document.get(fieldName), field.getClass()));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IOException("failed to set XNBT payload element " + field.getName(), e);
                }
                field.setAccessible(false);

            }

            return r;
        }

        @Override
        public void write(final Object payload, final NBTOutputStream out) throws IOException {

            String docName = null;

            // get document name from class annotation, if present
            if (payload.getClass().isAnnotationPresent(Tag.class)) {
                docName = payload.getClass().getAnnotation(Tag.class).name();
            }

            // otherwise use simple class name
            if (docName == null || docName.isEmpty()) {
                docName = payload.getClass().getSimpleName();
            }

            // create NBT structure
            final CompoundTag root = new CompoundTag("XNBTDocument", null);
            final CompoundTag settings = new CompoundTag(".settings", null);
            final CompoundTag document = new CompoundTag(docName, null);

            // put default settings
            settings.put(new IntegerTag("version", 0)); // unused
            settings.put(new StringTag("editor", "XNBT")); // unused
            settings.put(new StringTag("class", payload.getClass().getName()));
            settings.put(new StringTag("name", docName));

            // find data
            for (final Field field : payload.getClass().getDeclaredFields()) {

                // skip fields without annotation
                if (!field.isAnnotationPresent(Tag.class)) {
                    continue;
                }

                final byte type = field.getAnnotation(Tag.class).type();
                String name = field.getAnnotation(Tag.class).name();

                // use field name if annotation empty
                if (name.isEmpty()) {
                    name = field.getName();
                }

                field.setAccessible(true);
                try {
                    document.put(objectToTag(type, name, field.get(payload)));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IOException(e);
                }
                field.setAccessible(false);

            }

            // store content
            root.put(settings);
            root.put(document);

            out.writeTag(root);

        }

        private XNBT.TagBuilder getBuilderForPayloadClass(final Class<? extends Object> clazz) throws IOException {
            for (final XNBT.TagBuilder tb : XNBT.getBuilders().values()) {
                try {
                    if (tb.getClass()
                            .getDeclaredMethod("build", byte.class, String.class, Object.class).getReturnType()
                            .getDeclaredMethod("getPayload").getReturnType().equals(clazz)) {
                        return tb;
                    }
                } catch (NoSuchMethodException | SecurityException e) {
                    throw new IOException(e);
                }
            }
            return null;
        }

        private boolean isAnnotated(final Class<? extends Object> c) {

            if (c.isAnnotationPresent(Tag.class)) {
                return true;
            }

            for (final Field f : c.getDeclaredFields()) {
                if (f.isAnnotationPresent(Tag.class)) {
                    return true;
                }
            }

            return false;

        }

        private NBTTag objectToTag(final byte type, final String name, final Object payload) throws IOException {

            XNBT.TagBuilder builder = null;
            final Class<? extends Object> pc = payload.getClass();

            if (type == Tag.AUTO) {

                if (pc.isAssignableFrom(NBTTag.class)) {

                    final NBTTag tag = (NBTTag) payload;
                    return XNBT.getBuilder(tag.getHeader().getType()).build(tag.getHeader().getType(), name, tag.getPayload());

                } else if (pc.isAssignableFrom(List.class)) {

                    @SuppressWarnings("unchecked")
                    final List<Object> list = (List<Object>) payload;
                    final ListTag lTag = new ListTag(name);

                    for (final Object o : list) {
                        lTag.add(objectToTag(Tag.AUTO, null, o));
                    }

                    return lTag;

                } else if (pc.isAssignableFrom(Map.class)) {

                    @SuppressWarnings("unchecked")
                    final Map<String, Object> map = (Map<String, Object>) payload;
                    final CompoundTag cTag = new CompoundTag(name);

                    for (final String s : map.keySet()) {
                        cTag.put(objectToTag(Tag.AUTO, s, map.get(s)));
                    }

                    return cTag;

                } else if (isAnnotated(pc)) {
                    return new XNBTTag(payload);
                } else {
                    builder = getBuilderForPayloadClass(pc);
                }

            } else {
                builder = XNBT.getBuilder(type);
            }

            if (builder == null) {
                throw new IOException("no builder found for " + payload.getClass().getName());
            }

            return builder.build(type, name, payload);
        }

        private Object tagToObject(final NBTTag tag, final Class<? extends Object> clazz) throws IOException {

            if (clazz.isAssignableFrom(NBTTag.class)) {

                return tag;

            } else if (clazz.isAssignableFrom(List.class) && tag.getHeader().getType() == BaseType.LIST.Id()) {

                final List<Object> list = new ArrayList<>();
                final ListTag lTag = (ListTag) tag;

                for (int i = 0; i < lTag.size(); i++) {
                    final NBTTag e = lTag.get(i);
                    list.add(tagToObject(e, e.getPayload().getClass()));
                }

                return list;

            } else if (clazz.isAssignableFrom(Map.class) && tag.getHeader().getType() == BaseType.COMPOUND.Id()) {

                final Map<String, Object> map = new HashMap<>();
                final CompoundTag cTag = (CompoundTag) tag;

                for (final String k : cTag.keySet()) {
                    final NBTTag v = cTag.get(k);
                    map.put(k, tagToObject(v, v.getPayload().getClass()));
                }

                return map;

            } else {

                return tag.getPayload();

            }

        }

    };

    static {

        // End tag
        handlers[BaseType.END.Id()] = null;

        // Byte tag
        handlers[BaseType.BYTE.Id()] = new BaseTagIOHandler() {

            @Override
            public ByteTag build(final byte type, final String name, final Object payload) {
                return new ByteTag(name, (Byte) payload);
            }

            @Override
            public Byte read(final NBTInputStream in) throws IOException {
                return in.readByte();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeByte((byte) payload);
            }

        };

        // Short tag
        handlers[BaseType.SHORT.Id()] = new BaseTagIOHandler() {

            @Override
            public ShortTag build(final byte type, final String name, final Object payload) {
                return new ShortTag(name, (Short) payload);
            }

            @Override
            public Short read(final NBTInputStream in) throws IOException {
                return in.readShort();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeShort((short) payload);
            }

        };

        // Integer tag
        handlers[BaseType.INTEGER.Id()] = new BaseTagIOHandler() {

            @Override
            public IntegerTag build(final byte type, final String name, final Object payload) {
                return new IntegerTag(name, (Integer) payload);
            }

            @Override
            public Integer read(final NBTInputStream in) throws IOException {
                return in.readInt();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeInt((int) payload);
            }

        };

        // Long tag
        handlers[BaseType.LONG.Id()] = new BaseTagIOHandler() {

            @Override
            public LongTag build(final byte type, final String name, final Object payload) {
                return new LongTag(name, (Long) payload);
            }

            @Override
            public Long read(final NBTInputStream in) throws IOException {
                return in.readLong();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeLong((long) payload);
            }

        };

        // Float tag
        handlers[BaseType.FLOAT.Id()] = new BaseTagIOHandler() {

            @Override
            public FloatTag build(final byte type, final String name, final Object payload) {
                return new FloatTag(name, (Float) payload);
            }

            @Override
            public Float read(final NBTInputStream in) throws IOException {
                return in.readFloat();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeFloat((float) payload);
            }

        };

        // Double tag
        handlers[BaseType.DOUBLE.Id()] = new BaseTagIOHandler() {

            @Override
            public DoubleTag build(final byte type, final String name, final Object payload) {
                return new DoubleTag(name, (Double) payload);
            }

            @Override
            public Double read(final NBTInputStream in) throws IOException {
                return in.readDouble();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeDouble((double) payload);
            }

        };

        // Byte array tag
        handlers[BaseType.BYTE_ARRAY.Id()] = new BaseTagIOHandler() {

            @Override
            public ByteArrayTag build(final byte type, final String name, final Object payload) {
                return new ByteArrayTag(name, (byte[]) payload);
            }

            @Override
            public byte[] read(final NBTInputStream in) throws IOException {
                final byte[] p = new byte[in.readInt()];
                for (int i = 0; i < p.length; i++) {
                    p[i] = in.readByte();
                }
                return p;
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                final byte[] p = (byte[]) payload;
                out.writeInt(p.length);
                for (int i = 0; i < p.length; i++) {
                    out.writeByte(p[i]);
                }
            }

        };

        // String tag
        handlers[BaseType.STRING.Id()] = new BaseTagIOHandler() {

            @Override
            public StringTag build(final byte type, final String name, final Object payload) {
                return new StringTag(name, (String) payload);
            }

            @Override
            public String read(final NBTInputStream in) throws IOException {
                return in.readUTF();
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                out.writeUTF((String) payload);
            }

        };

        // List tag
        handlers[BaseType.LIST.Id()] = new BaseTagIOHandler() {

            @SuppressWarnings("unchecked")
            @Override
            public ListTag build(final byte type, final String name, final Object payload) {
                return new ListTag(name, (List<NBTTag>) payload);
            }

            @Override
            public List<NBTTag> read(final NBTInputStream in) throws IOException {
                final byte ct = in.readByte();
                final int s = in.readInt();

                final List<NBTTag> r = new ArrayList<>();

                for (int i = 0; i < s; i++) {
                    r.add(new BaseTag(new TagHeader(ct, null), XNBT.getReader(ct).read(in)));
                }

                return r;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                final List<NBTTag> list = (List<NBTTag>) payload;

                byte ct;

                if (list.isEmpty()) {
                    ct = 0;
                } else {
                    ct = list.get(0).getHeader().getType();
                }

                final int s = list.size();

                out.writeByte(ct);
                out.writeInt(s);

                for (int i = 0; i < s; i++) {
                    XNBT.getWriter(ct).write(list.get(i).getPayload(), out);
                }
            }

        };

        // Compound tag
        handlers[BaseType.COMPOUND.Id()] = new BaseTagIOHandler() {

            @SuppressWarnings("unchecked")
            @Override
            public CompoundTag build(final byte type, final String name, final Object payload) {
                return new CompoundTag(name, (Map<String, NBTTag>) payload);
            }

            @Override
            public Map<String, NBTTag> read(final NBTInputStream in) throws IOException {
                final Map<String, NBTTag> r = new HashMap<>();

                while (true) {
                    final NBTTag t = in.readTag();
                    if (t.equals(EndTag.INSTANCE)) {
                        break;
                    }
                    r.put(t.getHeader().getName(), t);
                }

                return r;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {
                for (final NBTTag t : ((Map<String, NBTTag>) payload).values()) {
                    out.writeTag(t);
                }
                out.writeTag(EndTag.INSTANCE);
            }

        };

        // Integer array tag
        handlers[BaseType.INTEGER_ARRAY.Id()] = new BaseTagIOHandler() {

            @Override
            public IntegerArrayTag build(final byte type, final String name, final Object payload) {
                return new IntegerArrayTag(name, (int[]) payload);
            }

            @Override
            public int[] read(final NBTInputStream in) throws IOException {
                final int[] p = new int[in.readInt()];
                for (int i = 0; i < p.length; i++) {
                    p[i] = in.readInt();
                }
                return p;
            }

            @Override
            public void write(final Object payload, final NBTOutputStream out) throws IOException {

                final int[] p = (int[]) payload;
                out.writeInt(p.length);
                for (int i = 0; i < p.length; i++) {
                    out.writeInt(p[i]);
                }
            }

        };

    }

    static XNBT.TagIOHandler[] getHandlers() {
        return handlers;
    }

    private BaseTagIOHandler() {}
}
