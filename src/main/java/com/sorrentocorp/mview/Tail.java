package com.sorrentocorp.mview;

import com.google.common.eventbus.EventBus;

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

public class Tail implements Runnable {
	final EventBus eventBus;
	final Map<FileId, FileWatch> watches = new HashMap<>();

	public Tail(EventBus eventBus, String[] filenames) {
		this.eventBus = eventBus;
		for (String f: filenames) {
			FileId id = FileId.apply(f);
			watches.put(id, new FileWatch(id));
		}
	}

	void processEvent(Path dir, WatchEvent<?> event) throws IOException {
		WatchEvent.Kind kind = event.kind();
		if (kind == OVERFLOW)
			return;
		Path p = dir.resolve(((WatchEvent<Path>)event).context());
		FileId id = FileId.apply(p.toString());
		eventBus.post(new FileModified(id, watches.get(p).emit()));
	}

	public void run () {
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
			// register with watch service
			Set<WatchKey> keys = new HashSet<>();
			Set<Path> directories = new HashSet<>();
			for (FileId fid: watches.keySet()) {
				Path dir = fid.realPath().getParent();
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
