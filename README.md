Reading a UTF-8 string using `RandomAccessFile` ([Java Doc](http://docs.oracle.com/javase/7/docs/api/java/io/RandomAccessFile.html)) is easier than one thinks as shown in the following coder fragment.

```java
  public static String lockAndReadFile(final File file, final String encoding, final int bufferSize) throws IOException {
    /* The approximate number of bytes required */
    final int approxBufferSize = (int) Math.min(Integer.MAX_VALUE, file.length());

    /* We need to open this file in read/write mode to be able to lock it */
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
```

The above example, does the following:

1. Calculates the file length in order to create a byte buffer of the right size, and minimise the number of times the buffer has to resize.

    ```java
        final int approxBufferSize = (int) Math.min(Integer.MAX_VALUE, file.length());
    ```

1. Creates an instance of the `RandomAccessFile` and acquires a `FileLock` ([Java Doc](http://docs.oracle.com/javase/7/docs/api/java/nio/channels/FileLock.html)).

    ```java
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileLock lock = raf.getChannel().lock();
    ```

    This is done from within the _try with resources_ ([Tutorial](http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)) so that we do not have to worry about releasing and closing the `FileLock` and `RandomAccessFile`

1. Creates an instance of `ByteArrayOutputStream`  ([Java Doc](http://docs.oracle.com/javase/7/docs/api/java/io/ByteArrayOutputStream.html)), which will be used to saved the file contents.

    ```java
            final ByteArrayOutputStream out = new ByteArrayOutputStream(approxBufferSize)) {
    ```

1. Read all file into the buffered array (instance of `ByteArrayOutputStream`).

    ```java
          final byte[] buffer = new byte[bufferSize];
          for (int length; (length = raf.read(buffer)) != -1;) {
            out.write(buffer, 0, length);
          }
    ```

    We cannot read parts of the file as some special characters, such as _ö_, are represented by more than one byte.  Therefore if we happen to read half of this letter, the we will corrupt the output.  Please note the word _Köln_ uses 5 bytes and not 4 bytes as many think.

    This has a limitation of the file size.  We cannot read very large files using this method.

1. Finally create a new string with the bytes read before using the given encoding.

    ```java
          return new String(out.toByteArray(), encoding);
    ```

    The _try with resources_ will close all three resources before exiting.

The example does not contain the whole code.  The readers can download or view all code from [https://github.com/javacreed/how-to-read-utf8-strings-with-randomaccessfile/](https://github.com/javacreed/how-to-read-utf8-strings-with-randomaccessfile/).
