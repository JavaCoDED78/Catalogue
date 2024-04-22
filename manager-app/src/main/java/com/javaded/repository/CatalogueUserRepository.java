package com.javaded.repository;

import com.javaded.entity.CatalogueUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CatalogueUserRepository extends CrudRepository<CatalogueUser, Integer> {

    Optional<CatalogueUser> findByUsername(String username);
}
