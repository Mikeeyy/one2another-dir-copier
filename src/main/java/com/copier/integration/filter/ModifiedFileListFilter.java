package com.copier.integration.filter;

import org.springframework.integration.file.filters.AbstractFileListFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter passes only files which are new or were modified
 */
@Component
public class ModifiedFileListFilter extends AbstractFileListFilter<File> {

    private final Map<File, Long> fileModTime = new HashMap<>();

    private final Object monitor = new Object();

    /* (non-Javadoc)
     * @see org.springframework.integration.file.filters.AbstractFileListFilter#accept(java.lang.Object)
     */
    @Override
    protected boolean accept(File file) {
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