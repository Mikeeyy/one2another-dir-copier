package com.copier.integration.handler;

import org.springframework.integration.dsl.support.GenericHandler;
import org.springframework.integration.support.MessageBuilder;

import java.io.File;
import java.util.Map;

/**
 * That class was created by Mikolaj Matejko
 * on 26.08.2016 19:08 as part of one2anothercopier project.
 */
public class SubdirectoriesHeaderEnricher implements GenericHandler<File> {

    private String inputPath;
    private String headerSubdirectory;

    public SubdirectoriesHeaderEnricher(String inputPath, String headerSubdirectory) {
        this.inputPath = unifyPath(inputPath);
        this.headerSubdirectory = headerSubdirectory;
    }

    @Override
    public Object handle(File payload, Map<String, Object> headers) {
        String fileDirectory = unifyPath(payload.getParent());
        String subDirectories = findSubdirectories(inputPath, fileDirectory);

        MessageBuilder<File> builder = MessageBuilder
                .withPayload(payload)
                .copyHeaders(headers)
                .setHeader(headerSubdirectory, subDirectories);

        return builder.build();
    }

    private String findSubdirectories(String inputPath, String fileDirectory) {
        return fileDirectory.replaceFirst(inputPath, "");
    }

    private String unifyPath(String s) {
        return s.replaceAll("\\\\", "/");
    }
}
