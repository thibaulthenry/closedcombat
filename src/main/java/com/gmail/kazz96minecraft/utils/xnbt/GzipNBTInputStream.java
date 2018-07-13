package com.gmail.kazz96minecraft.utils.xnbt;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GzipNBTInputStream extends NBTInputStream {

    public GzipNBTInputStream(final InputStream in) throws IOException {
        super(new GZIPInputStream(in));
    }

}
