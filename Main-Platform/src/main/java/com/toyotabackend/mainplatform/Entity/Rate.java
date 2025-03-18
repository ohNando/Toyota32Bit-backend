    package com.toyotabackend.mainplatform.Entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "rate")
    public class Rate {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;
        @Column(unique = true, nullable = false, length = 10)
        private String rateName;
        @Column(nullable = false)
        private float bid;
        @Column(nullable = false)
        private float ask;
        @Column(nullable = false)
        private String rateUpdateTime;
    }
