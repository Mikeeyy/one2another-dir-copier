package com.copier.integration.util;

import org.springframework.stereotype.Component;

/**
 * That class was created by Mikolaj Matejko
 * on 31.08.2016 22:20 as part of one2anothercopier project.
 */
@Component
public class SubdirectoriesResolver {

    public String resolve(String inputPath, String parentPath) {
        String fileDirectory = unifyPath(parentPath);
        return findSubdirectories(inputPath, fileDirectory);
    }

    private String findSubdirectories(String inputPath, String fileDirectory) {
        return fileDirectory.replaceFirst(inputPath, "");
    }

    private String unifyPath(String s) {
        return s.replaceAll("\\\\", "/");
    }
}
