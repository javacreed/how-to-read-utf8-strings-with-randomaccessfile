package com.javacreed.examples.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

public class RafUtils {

  public static String lockAndReadFile(final File file, final String encoding, final int bufferSize) throws IOException {
    // The approximate number of bytes required
    final int approxBufferSize = (int) Math.min(Integer.MAX_VALUE, file.length());

    // We need to open this file in read/write mode to be able to lock it
    try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileLock lock = raf.getChannel().lock();
        final ByteArrayOutputStream out = new ByteArrayOutputStream(approxBufferSize)) {

      final byte[] buffer = new byte[bufferSize];
      for (int length; (length = raf.read(buffer)) != -1;) {
        out.write(buffer, 0, length);
      }

      return new String(out.toByteArray(), encoding);
    }
  }

  private RafUtils() {}

}
