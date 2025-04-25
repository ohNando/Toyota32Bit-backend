package com.toyotabackend.mainplatform.Hazelcast;

import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hazelcast.config.Config;

@Configuration
public class HazelcastConfig {
    @Bean(name = "custom")
    public Config hazelcastConfig() {
        return new Config()
                .setInstanceName("main-hazelcast-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("rates")
                                .setTimeToLiveSeconds(3600)
                );
    }
}
