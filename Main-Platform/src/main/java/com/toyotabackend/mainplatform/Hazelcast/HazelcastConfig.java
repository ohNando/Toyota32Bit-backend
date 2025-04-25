package com.toyotabackend.mainplatform.Hazelcast;

import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hazelcast.config.Config;

/**
 * Configuration class for setting up Hazelcast in the application.
 * <p>
 * Defines and customizes the Hazelcast instance and map configurations.
 */
@Configuration
public class HazelcastConfig {

    /**
     * Defines the Hazelcast configuration bean.
     *
     * @return a configured Hazelcast {@link Config} instance
     */
    @Bean(name = "custom")
    public Config hazelcastConfig() {
        return new Config()
                .setInstanceName("main-hazelcast-instance")  // Name of the Hazelcast instance
                .addMapConfig(
                        new MapConfig()
                                .setName("rates")               // Map name used for rate caching
                                .setTimeToLiveSeconds(3600)     // TTL (1 hour) for entries
                );
    }
}
