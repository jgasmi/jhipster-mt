package com.yjiky.mt.web.rest;

import com.yjiky.mt.Application;
import com.yjiky.mt.domain.Action;
import com.yjiky.mt.repository.ActionRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ActionResource REST controller.
 *
 * @see ActionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ActionResourceTest {


    @Inject
    private ActionRepository actionRepository;

    private MockMvc restActionMockMvc;

    private Action action;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActionResource actionResource = new ActionResource();
        ReflectionTestUtils.setField(actionResource, "actionRepository", actionRepository);
        this.restActionMockMvc = MockMvcBuilders.standaloneSetup(actionResource).build();
    }

    @Before
    public void initTest() {
        action = new Action();
    }

    @Test
    @Transactional
    public void createAction() throws Exception {
        // Validate the database is empty
        assertThat(actionRepository.findAll()).hasSize(0);

        // Create the Action
        restActionMockMvc.perform(post("/api/actions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(action)))
                .andExpect(status().isCreated());

        // Validate the Action in the database
        List<Action> actions = actionRepository.findAll();
        assertThat(actions).hasSize(1);
        Action testAction = actions.iterator().next();
    }

    @Test
    @Transactional
    public void getAllActions() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get all the actions
        restActionMockMvc.perform(get("/api/actions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(action.getId().intValue()));
    }

    @Test
    @Transactional
    public void getAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get the action
        restActionMockMvc.perform(get("/api/actions/{id}", action.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(action.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAction() throws Exception {
        // Get the action
        restActionMockMvc.perform(get("/api/actions/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Update the action
        restActionMockMvc.perform(put("/api/actions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(action)))
                .andExpect(status().isOk());

        // Validate the Action in the database
        List<Action> actions = actionRepository.findAll();
        assertThat(actions).hasSize(1);
        Action testAction = actions.iterator().next();
    }

    @Test
    @Transactional
    public void deleteAction() throws Exception {
        // Initialize the database
        actionRepository.saveAndFlush(action);

        // Get the action
        restActionMockMvc.perform(delete("/api/actions/{id}", action.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Action> actions = actionRepository.findAll();
        assertThat(actions).hasSize(0);
    }
}
