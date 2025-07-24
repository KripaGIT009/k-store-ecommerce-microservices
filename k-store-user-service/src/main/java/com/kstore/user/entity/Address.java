package com.kstore.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AddressType type;

    @Column(name = "street_address", nullable = false)
    private String streetAddress;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AddressType {
        BILLING,
        SHIPPING,
        BOTH
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(streetAddress);
        if (city != null) {
            fullAddress.append(", ").append(city);
        }
        if (state != null) {
            fullAddress.append(", ").append(state);
        }
        if (postalCode != null) {
            fullAddress.append(" ").append(postalCode);
        }
        if (country != null) {
            fullAddress.append(", ").append(country);
        }
        return fullAddress.toString();
    }
}
