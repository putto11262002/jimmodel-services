package com.jimmodel.services.repository;

import com.jimmodel.services.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    @Query(value = "select c from Client c join c.address a where lower(c.name) like concat('%', lower(:searchTerm), '%')")
    public Page<Client> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}
