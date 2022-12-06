package com.jimmodel.internalServices.dto.Request;


import com.jimmodel.internalServices.model.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientRequest {

    private UUID id;
    private String name;
    private AddressRequest address;

    public Client toEntity(){
        return Client.builder()
                .id(this.id)
                .address(this.address != null ? this.address.toEntity(): null)
                .name(this.name)
                .build();
    }
}