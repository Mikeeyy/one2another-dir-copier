package com.copier.config;

import com.copier.integration.filter.ModifiedFileListFilter;
import com.copier.integration.util.SubdirectoriesResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * That class was created by Mikeeyy
 * on 25.08.2016 18:24 as part of one2anothercopier project.
 */
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:${integration.properties.classpath}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${integration.properties.file}", ignoreResourceNotFound = true)
})
public class IntegrationConfiguration {

    @Value("${directory.input}")
    private String inputDirectory;

    @Value("${directory.output}")
    private String outputDirectory;

    @Value("${statistics.file}")
    private String statisticsFile;

    @Value("${poller.period}")
    private int pollerPeriod;

    @Value("${poller.maxMessages}")
    private long maxMessagesPerPoll;

    @Value("${logger.file.date.pattern}")
    private String loggerFileDatePattern;

    private static final String HEADER_SUBDIRECTORIES = "subdirectories";

    private final ModifiedFileListFilter modifiedFileListFilter;

    private final SubdirectoriesResolver subdirectoriesResolver;


    @Autowired
    public IntegrationConfiguration(ModifiedFileListFilter modifiedFileListFilter, SubdirectoriesResolver subdirectoriesResolver) {
        Assert.notNull(modifiedFileListFilter, "Parameter modifiedFileListFilter should not be null");
        Assert.notNull(subdirectoriesResolver, "Parameter subdirectoriesResolver should not be null");
        this.modifiedFileListFilter = modifiedFileListFilter;
        this.subdirectoriesResolver = subdirectoriesResolver;
    }

    /**
     * Polling given directory for modified or new files and pushing them to the output directory
     */
    @Bean
    public IntegrationFlow copyingFilesFlow() {
        return IntegrationFlows
                .from(fileMessageSource(), poller())
                .enrichHeaders(h -> h.headerFunction(HEADER_SUBDIRECTORIES,
                        (Message<File> m) -> subdirectoriesResolver.resolve(inputDirectory, m.getPayload().getParent())))
                .publishSubscribeChannel(f -> f
                        .subscribe(loggingToFileFlow()))
                .handle(outboundAdapter())
                .get();
    }

    @Bean
    public IntegrationFlow loggingToFileFlow() {
        return f -> f
                .enrichHeaders(h -> h
                        .header(FileHeaders.FILENAME, Paths.get(statisticsFile).getFileName().toString()))
                .split()
                .transform(dateLoggerFileTransformer())
                .handle(Files
                        .outboundAdapter(Paths.get(statisticsFile).getParent().toFile())
                        .appendNewLine(true)
                        .autoCreateDirectory(true)
                        .fileExistsMode(FileExistsMode.APPEND));
    }

    @Bean
    public GenericTransformer<File, String> dateLoggerFileTransformer() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(loggerFileDatePattern);

        return file -> LocalDateTime.now().format(formatter) + " "
                + subdirectoriesResolver.resolve(inputDirectory, file.getParent() + "/" + file.getName());
    }

    @Bean
    public Consumer<SourcePollingChannelAdapterSpec> poller() {
        return c -> c.poller(Pollers.fixedRate(pollerPeriod).maxMessagesPerPoll(maxMessagesPerPoll));
    }

    @Bean
    public FileReadingMessageSource fileMessageSource() {
        FileReadingMessageSource messageSource = Files
                .inboundAdapter(new File(inputDirectory))
                .autoCreateDirectory(true)
                .filter(modifiedFileListFilter)
                .get();

        messageSource.setUseWatchService(true);
        messageSource.setWatchEvents(FileReadingMessageSource.WatchEventType.CREATE, FileReadingMessageSource.WatchEventType.MODIFY);
        return messageSource;
    }

    @Bean
    public FileWritingMessageHandler outboundAdapter() {
        return Files
                .outboundAdapter(f -> outputDirectory + f.getHeaders().get(HEADER_SUBDIRECTORIES))
                .autoCreateDirectory(true)
                .get();
    }
}
