package com.copier.integration.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * That class was created by Mikolaj Matejko
 * on 27.08.2016 14:08 as part of one2anothercopier project.
 */
@RunWith(SpringRunner.class)
public class ModifiedFileListFilterTest {

    private FileListFilter<File> filter;

    @Spy
    private List<File> files = new ArrayList<>();

    private static final int FILES_QUANTITY = 5;

    @Before
    public void setUp() throws Exception {
        filter = new ModifiedFileListFilter();

        for (int i = 0; i < FILES_QUANTITY; i++) {
            files.add(mock(File.class));
            when(files.get(i).lastModified()).thenReturn(i + 5L);
        }
    }

    @Test
    public void acceptTest() throws Exception {
        assertTrue("In single iteration not a single file should be passed",
                filter.filterFiles(filesList2Array()).isEmpty());

        assertTrue("Each unmodified file should not be accepted",
                filter.filterFiles(filesList2Array()).isEmpty());

        List<Integer> modifiedFilesIndexes = Arrays.asList(1, 3);
        modifiedFilesIndexes.forEach(f -> when(files.get(f).lastModified()).thenReturn(f + 6L));

        for (int i = 0; i < files.size(); i++) {
            if (modifiedFilesIndexes.contains(i))
                assertFalse("Each modified file should be accepted", filter.filterFiles(new File[]{filesList2Array()[i]}).isEmpty());
            else
                assertTrue("Each unmodified file should not accepted", filter.filterFiles(new File[]{filesList2Array()[i]}).isEmpty());
        }

        assertTrue("Each unmodified file should not be accepted",
                filter.filterFiles(filesList2Array()).isEmpty());
    }

    private File[] filesList2Array() {
        return files.toArray(new File[files.size()]);
    }

}