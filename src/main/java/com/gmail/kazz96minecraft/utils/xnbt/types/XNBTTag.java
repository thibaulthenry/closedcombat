package com.gmail.kazz96minecraft.utils.xnbt.types;

import com.gmail.kazz96minecraft.utils.xnbt.BaseTag;
import com.gmail.kazz96minecraft.utils.xnbt.Tag;

/**
 * <p>
 * Contains a {@link Tag} annotated object.
 * </p>
 */
public class XNBTTag extends BaseTag {

    private static String getDocName(final Object object) {

        final Class<? extends Object> oC = object.getClass();

        if (oC.isAnnotationPresent(Tag.class)) {
            final String n = oC.getAnnotation(Tag.class).name();
            return n.isEmpty() ? oC.getSimpleName() : n;
        }

        return oC.getSimpleName();

    }

    public XNBTTag(final Object document) {
        this(getDocName(document), document);
    }

    public XNBTTag(final String name, final Object document) {
        super(new TagHeader(BaseType.XNBT.Id(), name), document);

        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }
    }

    @Override
    protected final void setPayload(final Object payload) {
        super.setPayload(payload);
    }

}
