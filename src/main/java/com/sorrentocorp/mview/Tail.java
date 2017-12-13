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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tail {
	final Map<Path, FileWatch> watches = new HashMap<>();

	public Tail(String[] filenames) {
		for (String f: filenames) {
			FileId id = FileId.apply(f);
			watches.put(id.realPath(), new FileWatch(id));
		}
	}

	void processEvent(Path dir, WatchEvent<?> event) throws IOException {
		WatchEvent.Kind kind = event.kind();
		if (kind == OVERFLOW)
			return;
		Path p = dir.resolve(((WatchEvent<Path>)event).context()).toAbsolutePath();
		System.out.println("got new line for file " + p);
		System.out.print(watches.get(p).emit());
	}

	public void run () throws IOException {
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
			// register with watch service
			Set<WatchKey> keys = new HashSet<>();
			Set<Path> directories = new HashSet<>();
			for (Path p: watches.keySet()) {
				Path dir = p.getParent();
				directories.add(dir);
				if (keys.add(dir.register(watcher, ENTRY_MODIFY)))
					System.out.println("watching directory " + dir);
			}

			while(true) {
				WatchKey key = null;
				try {
					key = watcher.take();
				} catch (InterruptedException ignored) {
					continue;
				}

				for (WatchEvent<?> event: key.pollEvents()) {
					processEvent((Path)key.watchable(), event);
				}
				if (!key.reset()) {
					key.cancel();
					keys.remove(key);
				}
			}
		}
	}

	public static void main (String[] args) throws Exception {
		new Tail(args).run();
	}
}
