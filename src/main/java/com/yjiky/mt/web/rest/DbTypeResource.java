package com.yjiky.mt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yjiky.mt.domain.DbType;
import com.yjiky.mt.repository.DbTypeRepository;
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
 * REST controller for managing DbType.
 */
@RestController
@RequestMapping("/api")
public class DbTypeResource {

    private final Logger log = LoggerFactory.getLogger(DbTypeResource.class);

    @Inject
    private DbTypeRepository dbTypeRepository;

    /**
     * POST  /dbTypes -> Create a new dbType.
     */
    @RequestMapping(value = "/dbTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody DbType dbType) throws URISyntaxException {
        log.debug("REST request to save DbType : {}", dbType);
        if (dbType.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new dbType cannot already have an ID").build();
        }
        dbTypeRepository.save(dbType);
        return ResponseEntity.created(new URI("/api/dbTypes/" + dbType.getId())).build();
    }

    /**
     * PUT  /dbTypes -> Updates an existing dbType.
     */
    @RequestMapping(value = "/dbTypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody DbType dbType) throws URISyntaxException {
        log.debug("REST request to update DbType : {}", dbType);
        if (dbType.getId() == null) {
            return create(dbType);
        }
        dbTypeRepository.save(dbType);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /dbTypes -> get all the dbTypes.
     */
    @RequestMapping(value = "/dbTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<DbType> getAll() {
        log.debug("REST request to get all DbTypes");
        return dbTypeRepository.findAll();
    }

    /**
     * GET  /dbTypes/:id -> get the "id" dbType.
     */
    @RequestMapping(value = "/dbTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DbType> get(@PathVariable Long id) {
        log.debug("REST request to get DbType : {}", id);
        return Optional.ofNullable(dbTypeRepository.findOne(id))
            .map(dbType -> new ResponseEntity<>(
                dbType,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /dbTypes/:id -> delete the "id" dbType.
     */
    @RequestMapping(value = "/dbTypes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete DbType : {}", id);
        dbTypeRepository.delete(id);
    }
}
