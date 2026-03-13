package com.example.ecommerce.model;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Embedded document representing shipping information for an order.
 * This is embedded within the Order document and should not be used as a standalone collection.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ShippingInfo {

    /**
     * Tracking number for the shipment.
     */
    private String tracking;

    /**
     * Carrier name (FedEx, UPS, DHL, etc.)
     */
    private String carrier;

    /**
     * Estimated delivery date.
     */
    private LocalDateTime estimatedDelivery;

    /**
     * Convenience constructor for basic shipping info.
     *
     * @param carrier the shipping carrier
     * @param tracking the tracking number
     */
    public ShippingInfo(String carrier, String tracking) {
        this.carrier = carrier;
        this.tracking = tracking;
    }
}
