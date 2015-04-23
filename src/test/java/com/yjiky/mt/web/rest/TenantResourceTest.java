package com.yjiky.mt.web.rest;

import com.yjiky.mt.Application;
import com.yjiky.mt.domain.Tenant;
import com.yjiky.mt.repository.TenantRepository;

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
 * Test class for the TenantResource REST controller.
 *
 * @see TenantResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TenantResourceTest {

    private static final String DEFAULT_TENANT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_TENANT_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DB_HOST = "SAMPLE_TEXT";
    private static final String UPDATED_DB_HOST = "UPDATED_TEXT";

    private static final Integer DEFAULT_DB_PORT = 0;
    private static final Integer UPDATED_DB_PORT = 1;
    private static final String DEFAULT_DB_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_DB_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DB_USER_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_DB_USER_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DB_PASSWORD = "SAMPLE_TEXT";
    private static final String UPDATED_DB_PASSWORD = "UPDATED_TEXT";

    private static final Boolean DEFAULT_IS_ENABLED = false;
    private static final Boolean UPDATED_IS_ENABLED = true;

    private static final Boolean DEFAULT_HAS_DATABASE = false;
    private static final Boolean UPDATED_HAS_DATABASE = true;

    private static final Boolean DEFAULT_HAS_GENERATED_SCHEMA = false;
    private static final Boolean UPDATED_HAS_GENERATED_SCHEMA = true;

    @Inject
    private TenantRepository tenantRepository;

    private MockMvc restTenantMockMvc;

    private Tenant tenant;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TenantResource tenantResource = new TenantResource();
        ReflectionTestUtils.setField(tenantResource, "tenantRepository", tenantRepository);
        this.restTenantMockMvc = MockMvcBuilders.standaloneSetup(tenantResource).build();
    }

    @Before
    public void initTest() {
        tenant = new Tenant();
        tenant.setTenantName(DEFAULT_TENANT_NAME);
        tenant.setDbHost(DEFAULT_DB_HOST);
        tenant.setDbPort(DEFAULT_DB_PORT);
        tenant.setDbName(DEFAULT_DB_NAME);
        tenant.setDbUserName(DEFAULT_DB_USER_NAME);
        tenant.setDbPassword(DEFAULT_DB_PASSWORD);
        tenant.setIsEnabled(DEFAULT_IS_ENABLED);
        tenant.setHasDatabase(DEFAULT_HAS_DATABASE);
        tenant.setHasGeneratedSchema(DEFAULT_HAS_GENERATED_SCHEMA);
    }

    @Test
    @Transactional
    public void createTenant() throws Exception {
        int databaseSizeBeforeCreate = tenantRepository.findAll().size();

        // Create the Tenant
        restTenantMockMvc.perform(post("/api/tenants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant)))
                .andExpect(status().isCreated());

        // Validate the Tenant in the database
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeCreate + 1);
        Tenant testTenant = tenants.get(tenants.size() - 1);
        assertThat(testTenant.getTenantName()).isEqualTo(DEFAULT_TENANT_NAME);
        assertThat(testTenant.getDbHost()).isEqualTo(DEFAULT_DB_HOST);
        assertThat(testTenant.getDbPort()).isEqualTo(DEFAULT_DB_PORT);
        assertThat(testTenant.getDbName()).isEqualTo(DEFAULT_DB_NAME);
        assertThat(testTenant.getDbUserName()).isEqualTo(DEFAULT_DB_USER_NAME);
        assertThat(testTenant.getDbPassword()).isEqualTo(DEFAULT_DB_PASSWORD);
        assertThat(testTenant.isEnabled()).isEqualTo(DEFAULT_IS_ENABLED);
        assertThat(testTenant.hasDatabase()).isEqualTo(DEFAULT_HAS_DATABASE);
        assertThat(testTenant.hasGeneratedSchema()).isEqualTo(DEFAULT_HAS_GENERATED_SCHEMA);
    }

    @Test
    @Transactional
    public void getAllTenants() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        // Get all the tenants
        restTenantMockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tenant.getId().intValue())))
                .andExpect(jsonPath("$.[*].tenantName").value(hasItem(DEFAULT_TENANT_NAME.toString())))
                .andExpect(jsonPath("$.[*].dbHost").value(hasItem(DEFAULT_DB_HOST.toString())))
                .andExpect(jsonPath("$.[*].dbPort").value(hasItem(DEFAULT_DB_PORT)))
                .andExpect(jsonPath("$.[*].dbName").value(hasItem(DEFAULT_DB_NAME.toString())))
                .andExpect(jsonPath("$.[*].dbUserName").value(hasItem(DEFAULT_DB_USER_NAME.toString())))
                .andExpect(jsonPath("$.[*].dbPassword").value(hasItem(DEFAULT_DB_PASSWORD.toString())))
                .andExpect(jsonPath("$.[*].isEnabled").value(hasItem(DEFAULT_IS_ENABLED.booleanValue())))
                .andExpect(jsonPath("$.[*].hasDatabase").value(hasItem(DEFAULT_HAS_DATABASE.booleanValue())))
                .andExpect(jsonPath("$.[*].hasGeneratedSchema").value(hasItem(DEFAULT_HAS_GENERATED_SCHEMA.booleanValue())));
    }

    @Test
    @Transactional
    public void getTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

        // Get the tenant
        restTenantMockMvc.perform(get("/api/tenants/{id}", tenant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tenant.getId().intValue()))
            .andExpect(jsonPath("$.tenantName").value(DEFAULT_TENANT_NAME.toString()))
            .andExpect(jsonPath("$.dbHost").value(DEFAULT_DB_HOST.toString()))
            .andExpect(jsonPath("$.dbPort").value(DEFAULT_DB_PORT))
            .andExpect(jsonPath("$.dbName").value(DEFAULT_DB_NAME.toString()))
            .andExpect(jsonPath("$.dbUserName").value(DEFAULT_DB_USER_NAME.toString()))
            .andExpect(jsonPath("$.dbPassword").value(DEFAULT_DB_PASSWORD.toString()))
            .andExpect(jsonPath("$.isEnabled").value(DEFAULT_IS_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.hasDatabase").value(DEFAULT_HAS_DATABASE.booleanValue()))
            .andExpect(jsonPath("$.hasGeneratedSchema").value(DEFAULT_HAS_GENERATED_SCHEMA.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTenant() throws Exception {
        // Get the tenant
        restTenantMockMvc.perform(get("/api/tenants/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

		int databaseSizeBeforeUpdate = tenantRepository.findAll().size();

        // Update the tenant
        tenant.setTenantName(UPDATED_TENANT_NAME);
        tenant.setDbHost(UPDATED_DB_HOST);
        tenant.setDbPort(UPDATED_DB_PORT);
        tenant.setDbName(UPDATED_DB_NAME);
        tenant.setDbUserName(UPDATED_DB_USER_NAME);
        tenant.setDbPassword(UPDATED_DB_PASSWORD);
        tenant.setIsEnabled(UPDATED_IS_ENABLED);
        tenant.setHasDatabase(UPDATED_HAS_DATABASE);
        tenant.setHasGeneratedSchema(UPDATED_HAS_GENERATED_SCHEMA);
        restTenantMockMvc.perform(put("/api/tenants")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenant)))
                .andExpect(status().isOk());

        // Validate the Tenant in the database
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeUpdate);
        Tenant testTenant = tenants.get(tenants.size() - 1);
        assertThat(testTenant.getTenantName()).isEqualTo(UPDATED_TENANT_NAME);
        assertThat(testTenant.getDbHost()).isEqualTo(UPDATED_DB_HOST);
        assertThat(testTenant.getDbPort()).isEqualTo(UPDATED_DB_PORT);
        assertThat(testTenant.getDbName()).isEqualTo(UPDATED_DB_NAME);
        assertThat(testTenant.getDbUserName()).isEqualTo(UPDATED_DB_USER_NAME);
        assertThat(testTenant.getDbPassword()).isEqualTo(UPDATED_DB_PASSWORD);
        assertThat(testTenant.isEnabled()).isEqualTo(UPDATED_IS_ENABLED);
        assertThat(testTenant.hasDatabase()).isEqualTo(UPDATED_HAS_DATABASE);
        assertThat(testTenant.hasGeneratedSchema()).isEqualTo(UPDATED_HAS_GENERATED_SCHEMA);
    }

    @Test
    @Transactional
    public void deleteTenant() throws Exception {
        // Initialize the database
        tenantRepository.saveAndFlush(tenant);

		int databaseSizeBeforeDelete = tenantRepository.findAll().size();

        // Get the tenant
        restTenantMockMvc.perform(delete("/api/tenants/{id}", tenant.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(databaseSizeBeforeDelete - 1);
    }
}
