package com.copier.integration.filter;

import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Filter passes only files which are new or were modified
 *
 * In first iteration filter does not pass any file.
 */
@Component
public class ModifiedFileListFilter implements FileListFilter<File> {

    private final Map<File, Long> fileModTime = new HashMap<>();

    private final Object monitor = new Object();

    @Override
    public final List<File> filterFiles(File[] files) {
        List<File> acceptedList = new ArrayList<>();
        if (files != null) {
            boolean firstReading = fileModTime.isEmpty();

            for (File file : files) {
                boolean accepted = this.accept(file);

                if(accepted && !firstReading) acceptedList.add(file);
            }
        }
        return acceptedList;
    }

    private boolean accept(File file) {
        synchronized (this.monitor) {
            if (this.fileModTime.get(file) == null) {
                fileModTime.put(file, file.lastModified());
                return true;
            }
            if (this.fileModTime.get(file).compareTo(file.lastModified()) == 0) {
                return false;
            }
            if (this.fileModTime.get(file).compareTo(file.lastModified()) < 0) {
                fileModTime.put(file, file.lastModified());
                return true;
            }
            return false;
        }
    }
}