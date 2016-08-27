package com.copier.integration.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * That class was created by Mikolaj Matejko
 * on 27.08.2016 14:07 as part of one2anothercopier project.
 */
@RunWith(SpringRunner.class)
public class SubdirectoriesHeaderEnricherTest {

    private final static String INPUT_PATH = "C:/Users/Desktop/a";
    private final static String HEADER_NAME = "header.subdir";

    private SubdirectoriesHeaderEnricher enricher;

    @Mock
    private File file;

    @Spy
    Map<String, Object> headers = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        enricher = new SubdirectoriesHeaderEnricher(INPUT_PATH, HEADER_NAME);
    }

    @Test
    public void handleTest() throws Exception {
        List<String> subdirectories = Arrays.asList("", "/b", "/folder", "/a/b", "/a/b/c");

        subdirectories.forEach(f -> {
            when(file.getParent()).thenReturn(INPUT_PATH + f);

            @SuppressWarnings("unchecked")
            Message<File> message = (Message<File>) enricher.handle(file, headers);
            assertEquals(f, message.getHeaders().get(HEADER_NAME));
        });
    }

}