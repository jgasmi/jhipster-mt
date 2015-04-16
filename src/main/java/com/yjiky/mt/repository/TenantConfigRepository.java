package com.yjiky.mt.repository;

import com.yjiky.mt.domain.TenantConfig;
import com.yjiky.mt.domain.TenantConfig;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TenantConfig entity.
 */
public interface TenantConfigRepository extends JpaRepository<TenantConfig,Long> {

}
