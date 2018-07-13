package com.gmail.kazz96minecraft.utils.xnbt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class GzipNBTOutputStream extends NBTOutputStream {

    public GzipNBTOutputStream(final OutputStream out) throws IOException {
        super(new GZIPOutputStream(out));
    }

}
