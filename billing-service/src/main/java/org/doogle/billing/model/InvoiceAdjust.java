package org.doogle.billing.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvoiceAdjust extends PanacheMongoEntity {

    public String rentalId;
    public String userId;
    public LocalDate actualEndDate;
    public double price;
    public boolean paid;
}