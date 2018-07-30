package com.gmail.kazz96minecraft.utils.xnbt;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.kazz96minecraft.utils.xnbt.types.EndTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag;
import com.gmail.kazz96minecraft.utils.xnbt.types.NBTTag.BaseType;

public final class XNBT {

    public interface TagBuilder {

        NBTTag build(byte type, String name, Object payload);

    }

    public interface TagIOHandler extends TagBuilder, TagPayloadReader, TagPayloadWriter {}

    public interface TagPayloadReader {

        Object read(NBTInputStream in) throws IOException;

    }

    public interface TagPayloadWriter {

        void write(Object payload, NBTOutputStream out) throws IOException;

    }

    private static final Map<Byte, TagPayloadReader> readers = new HashMap<>();
    private static final Map<Byte, TagBuilder> builders = new HashMap<>();
    private static final Map<Byte, TagPayloadWriter> writers = new HashMap<>();

    static {
        for (byte i = 1; i < BaseTagIOHandler.getHandlers().length - 1; i++) {
            registerType(i, BaseTagIOHandler.getHandlers()[i]);
        }
        registerType(BaseType.XNBT.Id(), BaseTagIOHandler.xnbtHandler);
    }

    public static void dump(final List<NBTTag> tags, final PrintStream out) throws Exception {
        for (final NBTTag tag : tags) {
            dump(0, tag, null, out);
        }
    }

    public static TagBuilder getBuilder(final byte type) {
        return builders.get(type);
    }

    public static TagPayloadReader getReader(final byte type) {
        return readers.get(type);
    }

    public static TagPayloadWriter getWriter(final byte type) {
        return writers.get(type);
    }

    public static List<NBTTag> loadTags(final File file) throws IOException {
        return loadTags(file, true);
    }

    public static List<NBTTag> loadTags(final File file, final boolean isZipped) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            return readTags(is, isZipped);
        }
    }

    public static List<NBTTag> readTags(final InputStream is) throws IOException {
        return readTags(is, true);
    }

    public static List<NBTTag> readTags(final InputStream is, final boolean isZipped) throws IOException {

        if (is == null) {
            throw new IllegalArgumentException("is must not be null");
        }

        final List<NBTTag> r = new ArrayList<>();

        try (NBTInputStream in = isZipped
                ? new GzipNBTInputStream(is)
                : new NBTInputStream(is)) {
            while (true) {
                final NBTTag tag = in.readTag();
                if (tag.equals(EndTag.INSTANCE)) {
                    break;
                }
                r.add(tag);
            }
        } catch (final EOFException e) {} // Expected if no end tag is present

        return r;
    }

    public static void registerType(final byte type, final TagIOHandler handler) {
        registerType(type, handler, handler, handler);
    }

    public static void registerType(final byte type, final TagPayloadReader reader, final TagBuilder builder,
            final TagPayloadWriter writer) {

        if (readers.containsKey(type)) {
            throw new IllegalArgumentException("type already registered: " + type);
        }

        if (reader == null) {
            throw new IllegalArgumentException("reader must not be null");
        }

        if (builder == null) {
            throw new IllegalArgumentException("builder must not be null");
        }

        if (writer == null) {
            throw new IllegalArgumentException("writer must not be null");
        }

        readers.put(type, reader);
        builders.put(type, builder);
        writers.put(type, writer);

    }

    public static void saveTags(final List<NBTTag> tags, final File file) throws IOException {
        saveTags(tags, file, true);
    }

    public static void saveTags(final List<NBTTag> tags, final File file, final boolean zip) throws IOException {
        try (FileOutputStream os = new FileOutputStream(file)) {
            writeTags(tags, os, zip);
        }
    }

    public static void unregisterType(final byte type) {
        readers.remove(type);
        builders.remove(type);
        writers.remove(type);
    }

    public static void writeTags(final List<NBTTag> tags, final OutputStream os) throws IOException {
        writeTags(tags, os, true);
    }

    public static void writeTags(final List<NBTTag> tags, final OutputStream os, final boolean zip)
            throws IOException {

        if (tags == null) {
            throw new IllegalArgumentException("tags must not be null");
        }

        if (os == null) {
            throw new IllegalArgumentException("os must not be null");
        }

        if (tags.isEmpty()) {
            return;
        }

        if (tags.contains(null)) {
            throw new IllegalArgumentException("tags must not contain null elements");
        }

        if (tags.contains(EndTag.INSTANCE)) {
            throw new IllegalArgumentException("tags must not contain end tags");
        }

        try (NBTOutputStream out = zip
                ? new GzipNBTOutputStream(new DataOutputStream(os))
                : new NBTOutputStream(new DataOutputStream(os))) {
            for (final NBTTag t : tags) {
                out.writeTag(t);
            }
        }

    }

    static void dump(final int depth, final NBTTag tag, StringBuilder sb, final PrintStream out) throws Exception {

        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(indent(depth));

        final byte type = tag.getHeader().getType();
        final String name = tag.getHeader().getName();
        final Object payload = tag.getPayload();

        // type
        sb.append(type).append(" ")

                // type descriptor
                .append((type < BaseType.values().length) ? BaseType.byId(type).name() : getBuilder(type).getClass().getName()).append(" ")

                // name
                .append(name.isEmpty() ? "(untitled)" : name).append(" ");

        // payload
        if (type < BaseType.values().length) {

            switch (BaseType.byId(type)) {

            case XNBT:
                final Object o = tag.getPayload();
                if (!o.getClass().isAnnotationPresent(Tag.class)) {
                    break;
                } else {
                    sb.append(o.getClass().getName()).append(" ")
                            .append("\"").append(o.getClass().getAnnotation(Tag.class).name()).append("\"");
                }
                for (final Field f : o.getClass().getDeclaredFields()) {
                    if (f.isAnnotationPresent(Tag.class)) {
                        f.setAccessible(true);
                        sb.append("\n").append(indent(depth + 1))
                                .append(f.getName()).append(" ")
                                .append(f.getType().getName()).append(" ")
                                .append("\"").append(f.getAnnotation(Tag.class).name()).append("\" ")
                                .append(f.get(o));
                        f.setAccessible(false);
                    }
                }
                break;
            case END:
                sb.append("\n");
                break;

            case BYTE_ARRAY:
                sb.append(Arrays.toString((byte[]) payload)).append("\n");
                break;

            case INTEGER_ARRAY:
                sb.append(Arrays.toString((int[]) payload)).append("\n");
                break;

            case COMPOUND:
                @SuppressWarnings("unchecked")
                final Map<String, NBTTag> _c = (Map<String, NBTTag>) payload;
                sb.append(_c.size()).append(" values");

                if (!_c.isEmpty()) {
                    sb.append("\n");
                    for (final NBTTag e : _c.values()) {
                        dump(depth + 1, e, sb, null);
                    }
                }
                break;

            case LIST:
                @SuppressWarnings("unchecked")
                final List<NBTTag> _l = (List<NBTTag>) payload;
                sb.append(_l.size()).append(" values");
                if (!_l.isEmpty()) {
                    sb.append("\n");
                    for (final NBTTag e : _l) {
                        dump(depth + 1, e, sb, null);
                    }
                }
                break;

            case STRING:
                final String s = (String) payload;
                sb.append(s.isEmpty() ? "(empty string)" : s).append("\n");
                break;

            default:
                sb.append(payload).append("\n");
                break;
            }

        } else {
            sb.append(payload.toString()).append("\n");
        }

        if (depth == 0) {
            out.println(sb.toString());
        }

    }

    static Map<Byte, TagBuilder> getBuilders() {
        return builders;
    }

    private static String indent(final int depth) {
        final int off = depth * 3;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < off; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    private XNBT() {}
}
