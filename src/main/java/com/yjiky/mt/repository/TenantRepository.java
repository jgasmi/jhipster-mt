package com.yjiky.mt.repository;

import com.yjiky.mt.domain.Tenant;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tenant entity.
 */
public interface TenantRepository extends JpaRepository<Tenant,Long> {

}
