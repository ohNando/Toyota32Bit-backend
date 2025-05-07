package com.toyotabackend.mainplatform.Cache;

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
        Config config =  new Config();
        config.setInstanceName("main-hazelcast-instance");

        MapConfig rawRateConfig = new MapConfig();
        rawRateConfig.setName("raw-rates").setTimeToLiveSeconds(300);

        MapConfig calculatedRateConfig = new MapConfig();
        calculatedRateConfig.setName("calculated-rates").setTimeToLiveSeconds(300);

        config.addMapConfig(rawRateConfig);
        config.addMapConfig(calculatedRateConfig);

        return config;
    }
}
