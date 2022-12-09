package com.jimmodel.internalServices.repository;

import com.jimmodel.internalServices.model.ERole;
import com.jimmodel.internalServices.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, ERole> {

}
