package com.sorrentocorp.mview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;

public class Tail {
	final String filename;
	public Tail(String filename) {
		this.filename = filename;
	}
	void p(Object obj) {
		System.out.println(obj);
	}

	void processEvent(WatchEvent<?> event) throws IOException {
		WatchEvent.Kind kind = event.kind();
		if (kind == OVERFLOW)
			return;
		Path path = ((WatchEvent<Path>)event).context();
		FileChannel channel = FileChannel.open(path);
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0; i < buf.limit(); i++) {
			out.write(buf.get(i));
		}
		p(out.toString());
	}

	public void run () throws IOException {
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
			Set<WatchKey> keys = new HashSet<>();
			Path path = Paths.get(filename).toAbsolutePath();
			p("watching path " + path);
			keys.add(path.getParent().register(watcher, ENTRY_MODIFY));

			while(true) {
				WatchKey key = null;
				try {
					key = watcher.take();
				} catch (InterruptedException ignored) {
					continue;
				}

				for (WatchEvent<?> event: key.pollEvents()) {
					processEvent(event);
				}
				if (!key.reset()) {
					key.cancel();
				}
			}
		}
	}

	public static void main (String[] args) throws Exception {
		new Tail(args[0]).run();
	}
}
