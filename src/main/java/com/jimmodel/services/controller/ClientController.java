package com.jimmodel.services.controller;

import com.jimmodel.services.dto.Request.ClientRequest;
import com.jimmodel.services.dto.Response.ClientResponse;
import com.jimmodel.services.dto.Response.ClientsResponse;
import com.jimmodel.services.domain.Client;
import com.jimmodel.services.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping(value = "/${api-version}/client")
public class ClientController {
    @Autowired
    private ClientService clientService;
    @PostMapping()
    public ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest clientRequest){
        Client createdClient = clientService.save(clientRequest.toEntity());
        ClientResponse responseBody = new ClientResponse(createdClient);
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ClientResponse> updateClientById(@PathVariable(name = "id") UUID id, @RequestBody ClientRequest clientRequest){
        Client updatedClient = clientService.saveById(id, clientRequest.toEntity());
        ClientResponse responseBody = new ClientResponse(updatedClient);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable(name = "id") UUID id){
        Client client = clientService.findById(id);
        ClientResponse responseBody = new ClientResponse(client);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<ClientsResponse> getClients(
            @RequestParam(required = false, defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "${data.page-size}", name = "pageSize") Integer pageSize,
            @RequestParam(required = false, defaultValue = "${data.client.sort-by}", name = "sortBy") String sortBy,
            @RequestParam(required = false, defaultValue = "${data.sort-dir}", name = "sortDir") String sortDir
    ){
        Page<Client> clientPage = clientService.findAll(pageNumber, pageSize, sortBy, sortDir);
        ClientsResponse responseBody = new ClientsResponse(clientPage);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteClientById(@PathVariable(name = "id") UUID id){
        clientService.deleteById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/search/{searchTerm}")
    public ResponseEntity<ClientsResponse> searchClient(
            @PathVariable(name = "searchTerm") String searchTerm,
            @RequestParam(required = false, defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "${data.page-size}", name = "pageSize") Integer pageSize,
            @RequestParam(required = false, defaultValue = "${data.client.sort-by}", name = "sortBy") String sortBy,
            @RequestParam(required = false, defaultValue = "${data.sort-dir}", name = "sortDir") String sortDir){
        Page<Client> clientPage = clientService.search(searchTerm, pageNumber, pageSize, sortBy, sortDir);
        ClientsResponse responseBody = new ClientsResponse(clientPage);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


}
