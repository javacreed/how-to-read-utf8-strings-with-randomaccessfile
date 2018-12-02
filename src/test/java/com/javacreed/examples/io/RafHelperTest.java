package com.javacreed.examples.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.javacreed.examples.io.RafHelper.Mode;

public class RafHelperTest {

  private static File createSampleFile(final String data) throws IOException {
    final File file = File.createTempFile("RafHelper", "test");
    file.deleteOnExit();
    FileUtils.write(file, data, "UTF-8");

    return file;
  }

  @Test
  public void test() throws Exception {
    final String data = FileUtils.readFileToString(new File(getClass().getResource("/samples/Cologne.txt").toURI()),
                                                   "UTF-8");

    final File file = RafHelperTest.createSampleFile(data);

    try (RafHelper helper = new RafHelper(file, Mode.READ_WRITE)) {
      helper.lock();
      Assert.assertEquals(data, helper.read("UTF-8"));
      helper.clear();
      Assert.assertEquals("", helper.read("UTF-8"));
    }
  }
}
