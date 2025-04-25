    package com.toyotabackend.mainplatform.Entity;

    import jakarta.persistence.*;
    import lombok.*;

    @Getter
    @Setter
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

        public Rate(String _rateName,float _bid,float _ask,String _rateUpdateTime){
            this.rateName = _rateName;
            this.bid = _bid;
            this.ask = _ask;
            this.rateUpdateTime = _rateUpdateTime;
        }
    }
