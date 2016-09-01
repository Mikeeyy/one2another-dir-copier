package com.copier.integration.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * That class was created by Mikolaj Matejko
 * on 31.08.2016 22:29 as part of one2anothercopier project.
 */
@RunWith(SpringRunner.class)
public class SubdirectoriesResolverTest {
    private final static String INPUT_PATH = "C:/Users/Desktop/a";

    @Mock
    private File file;

    private SubdirectoriesResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new SubdirectoriesResolver();
    }

    @Test
    public void resolveTest() throws Exception {
        List<String> subdirectories = Arrays.asList("", "/b", "/folder", "/a/b", "/a/b/c");

        subdirectories.forEach(f -> {
            when(file.getParent()).thenReturn(INPUT_PATH + f);

            String resolved = resolver.resolve(INPUT_PATH, file.getParent());

            assertEquals("Subdirectory should be correct", f, resolved);
        });
    }

}