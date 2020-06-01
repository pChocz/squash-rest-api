package com.pj.squashrestapp.repository;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundGroupRepository extends JpaRepository<RoundGroup, Long> {

}
