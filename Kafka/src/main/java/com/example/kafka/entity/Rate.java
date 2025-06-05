package com.example.kafka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "rates")
@Getter
@Setter
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String rateName;

    @Column(nullable = false)
    private float bid;

    @Column(nullable = false)
    private float ask;

    @Column(nullable = false)
    private Instant rateUpdateTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp dbUpdateTime;
}
