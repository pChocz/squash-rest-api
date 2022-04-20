package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    @EntityGraph(
            attributePaths = {
                "players",
            })
    Authority findByType(AuthorityType type);
}
