package com.yjiky.mt.repository;

import com.yjiky.mt.domain.Action;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Action entity.
 */
public interface ActionRepository extends JpaRepository<Action,Long> {

}
