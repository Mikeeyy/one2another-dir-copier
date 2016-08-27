package com.copier.integration.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * That class was created by Mikolaj Matejko
 * on 27.08.2016 14:08 as part of one2anothercopier project.
 */
@RunWith(SpringRunner.class)
public class ModifiedFileListFilterTest {

    private ModifiedFileListFilter filter;

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
        files.forEach(file -> assertTrue("Each new file should be accepted", filter.accept(file)));

        files.forEach(file -> assertFalse("Each unmodified file should not be accepted", filter.accept(file)));

        List<Integer> modifiedFilesIndexes = Arrays.asList(1, 3);
        modifiedFilesIndexes.forEach(f -> when(files.get(f).lastModified()).thenReturn(f + 6L));

        for (int i = 0; i < files.size(); i++) {
            if (modifiedFilesIndexes.contains(i))
                assertTrue("Each modified file should be accepted", filter.accept(files.get(i)));
            else
                assertFalse("Each unmodified file should not accepted", filter.accept(files.get(i)));
        }

        files.forEach(file -> assertFalse("Each unmodified file should not be accepted", filter.accept(file)));
    }

}