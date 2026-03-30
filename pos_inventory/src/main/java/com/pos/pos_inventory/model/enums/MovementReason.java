package com.pos.pos_inventory.model.enums;

public enum MovementReason {

    // stock coming in
    INITIAL,          // first time stock is set for a product at a location
    PURCHASE,         // stock received from supplier / warehouse
    TRANSFER_IN,      // stock moved in from another branch or warehouse
    RETURN,           // customer returned item, stock restored

    // stock going out
    SALE,             // sold at POS
    TRANSFER_OUT,     // stock moved out to another branch or warehouse
    DAMAGE,           // item damaged or expired, written off
    THEFT,            // shrinkage / loss

    // manual
    ADJUSTMENT_UP,    // manual increase by admin (e.g. after stock count)
    ADJUSTMENT_DOWN   // manual decrease by admin (e.g. after stock count)
}