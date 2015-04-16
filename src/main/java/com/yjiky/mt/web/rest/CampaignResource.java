package com.yjiky.mt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yjiky.mt.domain.Campaign;
import com.yjiky.mt.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Campaign.
 */
@RestController
@RequestMapping("/api")
public class CampaignResource {

    private final Logger log = LoggerFactory.getLogger(CampaignResource.class);

    @Inject
    private CampaignRepository campaignRepository;

    /**
     * POST  /campaigns -> Create a new campaign.
     */
    @RequestMapping(value = "/campaigns",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Campaign campaign) throws URISyntaxException {
        log.debug("REST request to save Campaign : {}", campaign);
        if (campaign.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new campaign cannot already have an ID").build();
        }
        campaignRepository.save(campaign);
        return ResponseEntity.created(new URI("/api/campaigns/" + campaign.getId())).build();
    }

    /**
     * PUT  /campaigns -> Updates an existing campaign.
     */
    @RequestMapping(value = "/campaigns",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Campaign campaign) throws URISyntaxException {
        log.debug("REST request to update Campaign : {}", campaign);
        if (campaign.getId() == null) {
            return create(campaign);
        }
        campaignRepository.save(campaign);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /campaigns -> get all the campaigns.
     */
    @RequestMapping(value = "/campaigns",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Campaign> getAll() {
        log.debug("REST request to get all Campaigns");
        return campaignRepository.findAll();
    }

    /**
     * GET  /campaigns/:id -> get the "id" campaign.
     */
    @RequestMapping(value = "/campaigns/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Campaign> get(@PathVariable Long id) {
        log.debug("REST request to get Campaign : {}", id);
        return Optional.ofNullable(campaignRepository.findOne(id))
            .map(campaign -> new ResponseEntity<>(
                campaign,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /campaigns/:id -> delete the "id" campaign.
     */
    @RequestMapping(value = "/campaigns/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Campaign : {}", id);
        campaignRepository.delete(id);
    }
}
