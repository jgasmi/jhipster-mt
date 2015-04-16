package com.yjiky.mt.web.rest;

import com.yjiky.mt.domain.TenantConfig;
import com.yjiky.mt.Application;
import com.yjiky.mt.domain.TenantConfig;
import com.yjiky.mt.repository.TenantConfigRepository;

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
 * Test class for the TenantConfigResource REST controller.
 *
 * @see TenantConfigResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TenantConfigResourceTest {

    private static final String DEFAULT_URL = "SAMPLE_TEXT";
    private static final String UPDATED_URL = "UPDATED_TEXT";
    private static final String DEFAULT_USERNAME = "SAMPLE_TEXT";
    private static final String UPDATED_USERNAME = "UPDATED_TEXT";
    private static final String DEFAULT_PASSWORD = "SAMPLE_TEXT";
    private static final String UPDATED_PASSWORD = "UPDATED_TEXT";

    @Inject
    private TenantConfigRepository tenantConfigRepository;

    private MockMvc restTenantConfigMockMvc;

    private TenantConfig tenantConfig;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TenantConfigResource tenantConfigResource = new TenantConfigResource();
        ReflectionTestUtils.setField(tenantConfigResource, "tenantConfigRepository", tenantConfigRepository);
        this.restTenantConfigMockMvc = MockMvcBuilders.standaloneSetup(tenantConfigResource).build();
    }

    @Before
    public void initTest() {
        tenantConfig = new TenantConfig();
        tenantConfig.setUrl(DEFAULT_URL);
        tenantConfig.setUsername(DEFAULT_USERNAME);
        tenantConfig.setPassword(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void createTenantConfig() throws Exception {
        // Validate the database is empty
        assertThat(tenantConfigRepository.findAll()).hasSize(0);

        // Create the TenantConfig
        restTenantConfigMockMvc.perform(post("/api/tenantConfigs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenantConfig)))
                .andExpect(status().isCreated());

        // Validate the TenantConfig in the database
        List<TenantConfig> tenantConfigs = tenantConfigRepository.findAll();
        assertThat(tenantConfigs).hasSize(1);
        TenantConfig testTenantConfig = tenantConfigs.iterator().next();
        assertThat(testTenantConfig.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testTenantConfig.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testTenantConfig.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void getAllTenantConfigs() throws Exception {
        // Initialize the database
        tenantConfigRepository.saveAndFlush(tenantConfig);

        // Get all the tenantConfigs
        restTenantConfigMockMvc.perform(get("/api/tenantConfigs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(tenantConfig.getId().intValue()))
                .andExpect(jsonPath("$.[0].url").value(DEFAULT_URL.toString()))
                .andExpect(jsonPath("$.[0].username").value(DEFAULT_USERNAME.toString()))
                .andExpect(jsonPath("$.[0].password").value(DEFAULT_PASSWORD.toString()));
    }

    @Test
    @Transactional
    public void getTenantConfig() throws Exception {
        // Initialize the database
        tenantConfigRepository.saveAndFlush(tenantConfig);

        // Get the tenantConfig
        restTenantConfigMockMvc.perform(get("/api/tenantConfigs/{id}", tenantConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tenantConfig.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTenantConfig() throws Exception {
        // Get the tenantConfig
        restTenantConfigMockMvc.perform(get("/api/tenantConfigs/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTenantConfig() throws Exception {
        // Initialize the database
        tenantConfigRepository.saveAndFlush(tenantConfig);

        // Update the tenantConfig
        tenantConfig.setUrl(UPDATED_URL);
        tenantConfig.setUsername(UPDATED_USERNAME);
        tenantConfig.setPassword(UPDATED_PASSWORD);
        restTenantConfigMockMvc.perform(put("/api/tenantConfigs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tenantConfig)))
                .andExpect(status().isOk());

        // Validate the TenantConfig in the database
        List<TenantConfig> tenantConfigs = tenantConfigRepository.findAll();
        assertThat(tenantConfigs).hasSize(1);
        TenantConfig testTenantConfig = tenantConfigs.iterator().next();
        assertThat(testTenantConfig.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testTenantConfig.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testTenantConfig.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void deleteTenantConfig() throws Exception {
        // Initialize the database
        tenantConfigRepository.saveAndFlush(tenantConfig);

        // Get the tenantConfig
        restTenantConfigMockMvc.perform(delete("/api/tenantConfigs/{id}", tenantConfig.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TenantConfig> tenantConfigs = tenantConfigRepository.findAll();
        assertThat(tenantConfigs).hasSize(0);
    }
}
