package com.yjiky.mt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yjiky.mt.domain.TenantConfig;
import com.yjiky.mt.domain.TenantConfig;
import com.yjiky.mt.repository.TenantConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TenantConfig.
 */
@RestController
@RequestMapping("/api")
public class TenantConfigResource {

    private final Logger log = LoggerFactory.getLogger(TenantConfigResource.class);

    @Inject
    private TenantConfigRepository tenantConfigRepository;

    /**
     * POST  /tenantConfigs -> Create a new tenantConfig.
     */
    @RequestMapping(value = "/tenantConfigs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TenantConfig tenantConfig) throws URISyntaxException {
        log.debug("REST request to save TenantConfig : {}", tenantConfig);
        if (tenantConfig.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new tenantConfig cannot already have an ID").build();
        }
        tenantConfigRepository.save(tenantConfig);
        return ResponseEntity.created(new URI("/api/tenantConfigs/" + tenantConfig.getId())).build();
    }

    /**
     * PUT  /tenantConfigs -> Updates an existing tenantConfig.
     */
    @RequestMapping(value = "/tenantConfigs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TenantConfig tenantConfig) throws URISyntaxException {
        log.debug("REST request to update TenantConfig : {}", tenantConfig);
        if (tenantConfig.getId() == null) {
            return create(tenantConfig);
        }
        tenantConfigRepository.save(tenantConfig);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /tenantConfigs -> get all the tenantConfigs.
     */
    @RequestMapping(value = "/tenantConfigs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TenantConfig> getAll() {
        log.debug("REST request to get all TenantConfigs");
        return tenantConfigRepository.findAll();
    }

    /**
     * GET  /tenantConfigs/:id -> get the "id" tenantConfig.
     */
    @RequestMapping(value = "/tenantConfigs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TenantConfig> get(@PathVariable Long id) {
        log.debug("REST request to get TenantConfig : {}", id);
        return Optional.ofNullable(tenantConfigRepository.findOne(id))
            .map(tenantConfig -> new ResponseEntity<>(
                tenantConfig,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tenantConfigs/:id -> delete the "id" tenantConfig.
     */
    @RequestMapping(value = "/tenantConfigs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete TenantConfig : {}", id);
        tenantConfigRepository.delete(id);
    }
}
