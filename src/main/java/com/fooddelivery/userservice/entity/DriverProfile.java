package com.fooddelivery.userservice.entity;


import com.fooddelivery.userservice.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "DriversProfile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID driverProfileId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;


    private String licensePlate;

    @Column(nullable = false)
    private boolean isAvailable;
}
