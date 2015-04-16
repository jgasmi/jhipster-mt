package com.yjiky.mt.repository;

import com.yjiky.mt.domain.Campaign;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Campaign entity.
 */
public interface CampaignRepository extends JpaRepository<Campaign,Long> {

}
