package com.copier.config;

import com.copier.integration.filter.ModifiedFileListFilter;
import com.copier.integration.handler.SubdirectoriesHeaderEnricher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.dsl.support.GenericHandler;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.util.Assert;

import java.io.File;

/**
 * That class was created by Mikeeyy
 * on 25.08.2016 18:24 as part of one2anothercopier project.
 */
@Configuration
@PropertySource("${integration.properties.type}:${integration.properties}")
public class IntegrationConfiguration {

    @Value("${directory.input}")
    private String inputDirectory;

    @Value("${directory.output}")
    private String outputDirectory;

    @Value("${poller.period}")
    private int pollerPeriod;

    @Value("${poller.maxMessages}")
    private long maxMessagesPerPoll;

    private static final String HEADER_SUBDIRECTORIES = "subdirectories";
    private final ModifiedFileListFilter modifiedFileListFilter;

    @Autowired
    public IntegrationConfiguration(ModifiedFileListFilter modifiedFileListFilter) {
        Assert.notNull(modifiedFileListFilter, "Parameter modifiedFileListFilter should not be null");
        this.modifiedFileListFilter = modifiedFileListFilter;
    }

    /**
     * Polling given directory for modified or new files and pushing them to the output directory
     */
    @Bean
    public IntegrationFlow copyingFilesFlow() {
        return IntegrationFlows
                .from(fileMessageSource(), poller())
                .handle(enrichSubdirectoriesHeader())
                .handle(outboundAdapter())
                .get();
    }

    @Bean
    public GenericHandler<File> enrichSubdirectoriesHeader() {
        return new SubdirectoriesHeaderEnricher(inputDirectory, HEADER_SUBDIRECTORIES);
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
