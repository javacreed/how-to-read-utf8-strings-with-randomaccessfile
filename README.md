Reading a UTF-8 string using <code>RandomAccessFile</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/io/RandomAccessFile.html" target="_blank">Java Doc</a>) is easier than one thinks as shown in the following coder fragment.


<pre>
  public static String lockAndReadFile(final File file, final String encoding, final int bufferSize) throws IOException {
    <span class="comments">// The approximate number of bytes required</span>
    final int approxBufferSize = (int) Math.min(Integer.MAX_VALUE, file.length());

    <span class="comments">// We need to open this file in read/write mode to be able to lock it</span>
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
</pre>


The above example, does the following:


<ol>
<li>Calculates the file length in order to create a byte buffer of the right size, and minimise the number of times the buffer has to resize.
<pre>
    final int approxBufferSize = (int) Math.min(Integer.MAX_VALUE, file.length());
</pre>
</li>

<li>
Creates an instance of the <code>RandomAccessFile</code> and acquires a <code>FileLock</code> (<a href="http://docs.oracle.com/javase/7/docs/api/java/nio/channels/FileLock.html" target="_blank">Java Doc</a>).
<pre>
    try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileLock lock = raf.getChannel().lock();
</pre>

This is done from within the <em>try with resources</em> (<a href="http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html" target="_blank">Tutorial</a>) so that we do not have to worry about releasing and closing the <code>FileLock</code> and <code>RandomAccessFile</code>
</li>

<li>

Creates an instance of <code>ByteArrayOutputStream</code>  (<a href="http://docs.oracle.com/javase/7/docs/api/java/io/ByteArrayOutputStream.html" target="_blank">Java Doc</a>), which will be used to saved the file contents.
<pre>
        final ByteArrayOutputStream out = new ByteArrayOutputStream(approxBufferSize)) {
</pre>
</li>

<li>
Read all file into the buffered array (instance of <code>ByteArrayOutputStream</code>).

<pre>
      final byte[] buffer = new byte[bufferSize];
      for (int length; (length = raf.read(buffer)) != -1;) {
        out.write(buffer, 0, length);
      }
</pre>

We cannot read parts of the file as some special characters, such as <em>ö</em>, are represented by more than one byte.  Therefore if we happen to read half of this letter, the we will corrupt the output.  Please note the word <em>Köln</em> uses 5 bytes and not 4 bytes as many think.

This has a limitation of the file size.  We cannot read very large files using this method.
</li>

<li>
Finally create a new string with the bytes read before using the given encoding.

<pre>
      return new String(out.toByteArray(), encoding);
</pre>

The <em>try with resources</em> will close all three resources before exiting.
</li>
</ol>


The example does not contain the whole code.  The readers can download or view all code from the above link.
