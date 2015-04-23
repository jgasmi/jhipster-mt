package com.yjiky.mt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yjiky.mt.domain.DbType;
import com.yjiky.mt.domain.Tenant;
import com.yjiky.mt.multitenancy.ConnectionProviderFactory;
import com.yjiky.mt.multitenancy.ConnectionProviderHolder;
import com.yjiky.mt.multitenancy.SpringLiquibaseUpdater;
import com.yjiky.mt.repository.DbTypeRepository;
import com.yjiky.mt.repository.TenantRepository;
import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
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

    @Inject
    private DbTypeRepository dbTypeRepository;

    @Autowired
    private ResourceLoader resourceLoader;

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
        DbType dbType = dbTypeRepository.findOne(tenant.getDbtype().getId());
        tenant.setDbtype(dbType);

        tenantRepository.save(tenant);

        if (!tenant.hasDatabase()) {
            try {
                ConnectionProviderFactory.getInstance().createDatabase(tenant);
                tenant.setHasDatabase(true);
                tenantRepository.save(tenant);
            } catch (SQLException e) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            } catch (ClassNotFoundException e) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }
        ConnectionProviderFactory.getInstance().cacheTenant(tenant);
        ConnectionProviderHolder connectionProviderHolder = ConnectionProviderFactory.getInstance().resolveConnectionProviderForTenant(tenant.getId()+"_"+tenant.getTenantName());

        //Liquibase DB Generation
        SpringLiquibaseUpdater liquibaseUpdater = new SpringLiquibaseUpdater(connectionProviderHolder, "classpath:config/liquibase/master.xml", resourceLoader);
        try {
            liquibaseUpdater.update();
            tenant.setHasGeneratedSchema(true);
            tenantRepository.save(tenant);
        } catch (LiquibaseException e) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

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
