package com.bavarians.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for preview of offer before confirmation
 */
public class OfertaPreviewDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long vehicleId;
    private String vehicleInfo;  // Make, model, year, VIN
    private List<ServiceItemDTO> serviceItems;
    private BigDecimal laborTotal;
    private BigDecimal partsTotal;
    private BigDecimal grandTotal;

    public OfertaPreviewDTO() {
        this.serviceItems = new ArrayList<>();
        this.laborTotal = BigDecimal.ZERO;
        this.partsTotal = BigDecimal.ZERO;
        this.grandTotal = BigDecimal.ZERO;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public List<ServiceItemDTO> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<ServiceItemDTO> serviceItems) {
        this.serviceItems = serviceItems;
    }

    public BigDecimal getLaborTotal() {
        return laborTotal;
    }

    public void setLaborTotal(BigDecimal laborTotal) {
        this.laborTotal = laborTotal;
    }

    public BigDecimal getPartsTotal() {
        return partsTotal;
    }

    public void setPartsTotal(BigDecimal partsTotal) {
        this.partsTotal = partsTotal;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public static class ServiceItemDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String serviceName;
        private String partsDescription;
        private BigDecimal laborHours;
        private BigDecimal laborRate;
        private BigDecimal laborCost;
        private BigDecimal partsCost;
        private BigDecimal itemTotal;

        public ServiceItemDTO() {
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getPartsDescription() {
            return partsDescription;
        }

        public void setPartsDescription(String partsDescription) {
            this.partsDescription = partsDescription;
        }

        public BigDecimal getLaborHours() {
            return laborHours;
        }

        public void setLaborHours(BigDecimal laborHours) {
            this.laborHours = laborHours;
        }

        public BigDecimal getLaborRate() {
            return laborRate;
        }

        public void setLaborRate(BigDecimal laborRate) {
            this.laborRate = laborRate;
        }

        public BigDecimal getLaborCost() {
            return laborCost;
        }

        public void setLaborCost(BigDecimal laborCost) {
            this.laborCost = laborCost;
        }

        public BigDecimal getPartsCost() {
            return partsCost;
        }

        public void setPartsCost(BigDecimal partsCost) {
            this.partsCost = partsCost;
        }

        public BigDecimal getItemTotal() {
            return itemTotal;
        }

        public void setItemTotal(BigDecimal itemTotal) {
            this.itemTotal = itemTotal;
        }
    }
}
