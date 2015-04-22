package com.yjiky.mt.repository;

import com.yjiky.mt.domain.DbType;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DbType entity.
 */
public interface DbTypeRepository extends JpaRepository<DbType,Long> {

}
