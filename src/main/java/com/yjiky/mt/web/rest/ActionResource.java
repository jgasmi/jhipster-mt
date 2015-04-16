package com.yjiky.mt.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.yjiky.mt.domain.Action;
import com.yjiky.mt.repository.ActionRepository;
import com.yjiky.mt.domain.Action;
import com.yjiky.mt.repository.ActionRepository;
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
 * REST controller for managing Action.
 */
@RestController
@RequestMapping("/api")
public class ActionResource {

    private final Logger log = LoggerFactory.getLogger(ActionResource.class);

    @Inject
    private ActionRepository actionRepository;

    /**
     * POST  /actions -> Create a new action.
     */
    @RequestMapping(value = "/actions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Action action) throws URISyntaxException {
        log.debug("REST request to save Action : {}", action);
        if (action.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new action cannot already have an ID").build();
        }
        actionRepository.save(action);
        return ResponseEntity.created(new URI("/api/actions/" + action.getId())).build();
    }

    /**
     * PUT  /actions -> Updates an existing action.
     */
    @RequestMapping(value = "/actions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Action action) throws URISyntaxException {
        log.debug("REST request to update Action : {}", action);
        if (action.getId() == null) {
            return create(action);
        }
        actionRepository.save(action);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /actions -> get all the actions.
     */
    @RequestMapping(value = "/actions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Action> getAll() {
        log.debug("REST request to get all Actions");
        return actionRepository.findAll();
    }

    /**
     * GET  /actions/:id -> get the "id" action.
     */
    @RequestMapping(value = "/actions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Action> get(@PathVariable Long id) {
        log.debug("REST request to get Action : {}", id);
        return Optional.ofNullable(actionRepository.findOne(id))
            .map(action -> new ResponseEntity<>(
                action,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /actions/:id -> delete the "id" action.
     */
    @RequestMapping(value = "/actions/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Action : {}", id);
        actionRepository.delete(id);
    }
}
