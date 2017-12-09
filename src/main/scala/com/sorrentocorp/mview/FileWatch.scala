package com.sorrentocorp.mview

import java.io.ByteArrayOutputStream
import java.nio.file.{Path, Paths,WatchEvent}
import java.nio.channels.FileChannel

case class FileWatch(filepath: String) {
  val path = Paths.get(filepath).toAbsolutePath
  assert(path.toFile.exists, s"Expected filepath $filepath ($path) to exist")

  /** The last byte offset we read, note that this limits us to 2G file size */
  private var last: Int = 0

  private def withChannel[T](channel: FileChannel)(func: FileChannel => T) =
    try { func(channel) } finally { channel.close() }

  /** Returns the added lines since the last emit */
  def emit: String = withChannel(FileChannel.open(path)) { channel =>
    val len = channel.size.toInt
    if (len == last)
      return ""; // assume no change

    // if file's been truncated, read from beginning
    val start = if (len < last) 0 else last
    last = len

    // maps
		val buf = channel.map(FileChannel.MapMode.READ_ONLY, start, len - start)
    val out = new ByteArrayOutputStream
    for (i <- 0 to buf.limit - 1)
      out.write(buf.get(i))
    return out.toString;
  }

}
