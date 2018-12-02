package com.javacreed.examples.io;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class RafUtilsTest {

  @Test
  public void test() throws Exception {
    // Make sure that the buffer size does not affect the outcome of this method
    for (int bufferSize = 1; bufferSize < 1025; bufferSize *= 2) {
      // Make sure that the UTF-8 Characters are properly read
      File file = new File(getClass().getResource("/samples/Cologne.txt").toURI());
      String data = RafUtils.lockAndReadFile(file, "UTF-8", bufferSize);
      Assert.assertEquals(866, data.length());
      Assert.assertTrue(data.contains("Köln, pronounced [kœln]"));

      // Make sure that the ASCII Characters are properly read
      file = new File(getClass().getResource("/samples/Milan.txt").toURI());
      data = RafUtils.lockAndReadFile(file, "ASCII", bufferSize);
      Assert.assertEquals(933, data.length());
    }
  }
}
