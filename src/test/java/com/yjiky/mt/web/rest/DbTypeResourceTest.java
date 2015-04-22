package com.yjiky.mt.web.rest;

import com.yjiky.mt.Application;
import com.yjiky.mt.domain.DbType;
import com.yjiky.mt.repository.DbTypeRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
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
 * Test class for the DbTypeResource REST controller.
 *
 * @see DbTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DbTypeResourceTest {

    private static final String DEFAULT_DB_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_DB_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_DRIVER = "SAMPLE_TEXT";
    private static final String UPDATED_DRIVER = "UPDATED_TEXT";
    private static final String DEFAULT_URL = "SAMPLE_TEXT";
    private static final String UPDATED_URL = "UPDATED_TEXT";

    @Inject
    private DbTypeRepository dbTypeRepository;

    private MockMvc restDbTypeMockMvc;

    private DbType dbType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DbTypeResource dbTypeResource = new DbTypeResource();
        ReflectionTestUtils.setField(dbTypeResource, "dbTypeRepository", dbTypeRepository);
        this.restDbTypeMockMvc = MockMvcBuilders.standaloneSetup(dbTypeResource).build();
    }

    @Before
    public void initTest() {
        dbType = new DbType();
        dbType.setDbType(DEFAULT_DB_TYPE);
        dbType.setDriver(DEFAULT_DRIVER);
        dbType.setUrl(DEFAULT_URL);
    }

    @Test
    @Transactional
    public void createDbType() throws Exception {
        int databaseSizeBeforeCreate = dbTypeRepository.findAll().size();

        // Create the DbType
        restDbTypeMockMvc.perform(post("/api/dbTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dbType)))
                .andExpect(status().isCreated());

        // Validate the DbType in the database
        List<DbType> dbTypes = dbTypeRepository.findAll();
        assertThat(dbTypes).hasSize(databaseSizeBeforeCreate + 1);
        DbType testDbType = dbTypes.get(dbTypes.size() - 1);
        assertThat(testDbType.getDbType()).isEqualTo(DEFAULT_DB_TYPE);
        assertThat(testDbType.getDriver()).isEqualTo(DEFAULT_DRIVER);
        assertThat(testDbType.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    public void getAllDbTypes() throws Exception {
        // Initialize the database
        dbTypeRepository.saveAndFlush(dbType);

        // Get all the dbTypes
        restDbTypeMockMvc.perform(get("/api/dbTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(dbType.getId().intValue())))
                .andExpect(jsonPath("$.[*].dbType").value(hasItem(DEFAULT_DB_TYPE.toString())))
                .andExpect(jsonPath("$.[*].driver").value(hasItem(DEFAULT_DRIVER.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())));
    }

    @Test
    @Transactional
    public void getDbType() throws Exception {
        // Initialize the database
        dbTypeRepository.saveAndFlush(dbType);

        // Get the dbType
        restDbTypeMockMvc.perform(get("/api/dbTypes/{id}", dbType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dbType.getId().intValue()))
            .andExpect(jsonPath("$.dbType").value(DEFAULT_DB_TYPE.toString()))
            .andExpect(jsonPath("$.driver").value(DEFAULT_DRIVER.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDbType() throws Exception {
        // Get the dbType
        restDbTypeMockMvc.perform(get("/api/dbTypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDbType() throws Exception {
        // Initialize the database
        dbTypeRepository.saveAndFlush(dbType);
		
		int databaseSizeBeforeUpdate = dbTypeRepository.findAll().size();

        // Update the dbType
        dbType.setDbType(UPDATED_DB_TYPE);
        dbType.setDriver(UPDATED_DRIVER);
        dbType.setUrl(UPDATED_URL);
        restDbTypeMockMvc.perform(put("/api/dbTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dbType)))
                .andExpect(status().isOk());

        // Validate the DbType in the database
        List<DbType> dbTypes = dbTypeRepository.findAll();
        assertThat(dbTypes).hasSize(databaseSizeBeforeUpdate);
        DbType testDbType = dbTypes.get(dbTypes.size() - 1);
        assertThat(testDbType.getDbType()).isEqualTo(UPDATED_DB_TYPE);
        assertThat(testDbType.getDriver()).isEqualTo(UPDATED_DRIVER);
        assertThat(testDbType.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    public void deleteDbType() throws Exception {
        // Initialize the database
        dbTypeRepository.saveAndFlush(dbType);
		
		int databaseSizeBeforeDelete = dbTypeRepository.findAll().size();

        // Get the dbType
        restDbTypeMockMvc.perform(delete("/api/dbTypes/{id}", dbType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<DbType> dbTypes = dbTypeRepository.findAll();
        assertThat(dbTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
