package com.drugInventory.service;

import com.drugInventory.exception.ResourceNotFoundException;
import com.drugInventory.model.Supplier;
import com.drugInventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Supplier Service - manages supplier information.
 */
@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByActiveTrue();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        supplier.setActive(true);
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplierData) {
        Supplier supplier = getSupplierById(id);
        supplier.setName(supplierData.getName());
        supplier.setEmail(supplierData.getEmail());
        supplier.setPhone(supplierData.getPhone());
        supplier.setAddress(supplierData.getAddress());
        supplier.setLicenseNumber(supplierData.getLicenseNumber());
        supplier.setContactPerson(supplierData.getContactPerson());
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplier.setActive(false); // Soft delete
        supplierRepository.save(supplier);
    }

    public List<Supplier> searchSuppliers(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }
}
