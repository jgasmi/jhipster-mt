package com.yjiky.mt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yjiky.mt.domain.Tenant;
import com.yjiky.mt.repository.TenantRepository;
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
 * REST controller for managing Tenant.
 */
@RestController
@RequestMapping("/api")
public class TenantResource {

    private final Logger log = LoggerFactory.getLogger(TenantResource.class);

    @Inject
    private TenantRepository tenantRepository;

    /**
     * POST  /tenants -> Create a new tenant.
     */
    @RequestMapping(value = "/tenants",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Tenant tenant) throws URISyntaxException {
        log.debug("REST request to save Tenant : {}", tenant);
        if (tenant.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new tenant cannot already have an ID").build();
        }
        tenantRepository.save(tenant);
        return ResponseEntity.created(new URI("/api/tenants/" + tenant.getId())).build();
    }

    /**
     * PUT  /tenants -> Updates an existing tenant.
     */
    @RequestMapping(value = "/tenants",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Tenant tenant) throws URISyntaxException {
        log.debug("REST request to update Tenant : {}", tenant);
        if (tenant.getId() == null) {
            return create(tenant);
        }
        tenantRepository.save(tenant);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /tenants -> get all the tenants.
     */
    @RequestMapping(value = "/tenants",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Tenant> getAll() {
        log.debug("REST request to get all Tenants");
        return tenantRepository.findAll();
    }

    /**
     * GET  /tenants/:id -> get the "id" tenant.
     */
    @RequestMapping(value = "/tenants/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Tenant> get(@PathVariable Long id) {
        log.debug("REST request to get Tenant : {}", id);
        return Optional.ofNullable(tenantRepository.findOne(id))
            .map(tenant -> new ResponseEntity<>(
                tenant,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tenants/:id -> delete the "id" tenant.
     */
    @RequestMapping(value = "/tenants/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Tenant : {}", id);
        tenantRepository.delete(id);
    }
}
