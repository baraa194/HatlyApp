package com.Hatly.Backend.user.service;

import com.Hatly.Backend.user.dto.CustomerAddressdto;
import com.Hatly.Backend.user.mapper.CustomerAddressMapper;
import com.Hatly.Backend.user.model.CustomerAddress;
import com.Hatly.Backend.user.repo.CustomerAddresseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerAddressService {
    @Autowired
    private CustomerAddresseRepo AddressRepo;
    @Autowired
    private CustomerAddressMapper AddressMapper;

    public CustomerAddressdto addCustomerAddress(CustomerAddressdto Addressreq){

        CustomerAddress Caddress = AddressMapper.toEntity(Addressreq);
        AddressRepo.save(Caddress);
        CustomerAddress savedAddress = AddressRepo.save(Caddress);
        return AddressMapper.toDto(savedAddress);
    }
    public CustomerAddressdto updateCustomerAddress(CustomerAddressdto addressreq, Long id){
        CustomerAddress existingAddress = AddressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        CustomerAddress caddress = AddressMapper.toEntity(addressreq);
        caddress.setId(existingAddress.getId());

        caddress.setUser(existingAddress.getUser());

        CustomerAddress updatedAddress = AddressRepo.save(caddress);

        return AddressMapper.toDto(updatedAddress);

    }
    public CustomerAddressdto getAddressById(Long id) {
        CustomerAddress address = AddressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        return AddressMapper.toDto(address);
    }
    public List<CustomerAddressdto> getAddresses(Long userid){

        List<CustomerAddress> addresses = AddressRepo.findByUserId(userid);
       return addresses.stream().map(address->AddressMapper.
                toDto(address)).toList();
    }

    public CustomerAddressdto getDefaultCustomerAddress(Long userid){
        CustomerAddress address=AddressRepo.findByUserIdAndIsDefaultTrue(userid)
                .orElseThrow(() -> new RuntimeException("Address not found with user id: " + userid));
        return AddressMapper.toDto(address);
    }



    public void deleteCustomerAddress(Long id) {
        CustomerAddress address = AddressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        AddressRepo.delete(address);
    }






}
