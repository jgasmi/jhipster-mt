package com.yjiky.mt.web.rest;

import com.yjiky.mt.Application;
import com.yjiky.mt.domain.Campaign;
import com.yjiky.mt.repository.CampaignRepository;

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
 * Test class for the CampaignResource REST controller.
 *
 * @see CampaignResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CampaignResourceTest {

    private static final String DEFAULT_ACTION = "SAMPLE_TEXT";
    private static final String UPDATED_ACTION = "UPDATED_TEXT";

    @Inject
    private CampaignRepository campaignRepository;

    private MockMvc restCampaignMockMvc;

    private Campaign campaign;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CampaignResource campaignResource = new CampaignResource();
        ReflectionTestUtils.setField(campaignResource, "campaignRepository", campaignRepository);
        this.restCampaignMockMvc = MockMvcBuilders.standaloneSetup(campaignResource).build();
    }

    @Before
    public void initTest() {
        campaign = new Campaign();
        campaign.setAction(DEFAULT_ACTION);
    }

    @Test
    @Transactional
    public void createCampaign() throws Exception {
        // Validate the database is empty
        assertThat(campaignRepository.findAll()).hasSize(0);

        // Create the Campaign
        restCampaignMockMvc.perform(post("/api/campaigns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(campaign)))
                .andExpect(status().isCreated());

        // Validate the Campaign in the database
        List<Campaign> campaigns = campaignRepository.findAll();
        assertThat(campaigns).hasSize(1);
        Campaign testCampaign = campaigns.iterator().next();
        assertThat(testCampaign.getAction()).isEqualTo(DEFAULT_ACTION);
    }

    @Test
    @Transactional
    public void getAllCampaigns() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get all the campaigns
        restCampaignMockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(campaign.getId().intValue()))
                .andExpect(jsonPath("$.[0].action").value(DEFAULT_ACTION.toString()));
    }

    @Test
    @Transactional
    public void getCampaign() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get the campaign
        restCampaignMockMvc.perform(get("/api/campaigns/{id}", campaign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(campaign.getId().intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCampaign() throws Exception {
        // Get the campaign
        restCampaignMockMvc.perform(get("/api/campaigns/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCampaign() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Update the campaign
        campaign.setAction(UPDATED_ACTION);
        restCampaignMockMvc.perform(put("/api/campaigns")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(campaign)))
                .andExpect(status().isOk());

        // Validate the Campaign in the database
        List<Campaign> campaigns = campaignRepository.findAll();
        assertThat(campaigns).hasSize(1);
        Campaign testCampaign = campaigns.iterator().next();
        assertThat(testCampaign.getAction()).isEqualTo(UPDATED_ACTION);
    }

    @Test
    @Transactional
    public void deleteCampaign() throws Exception {
        // Initialize the database
        campaignRepository.saveAndFlush(campaign);

        // Get the campaign
        restCampaignMockMvc.perform(delete("/api/campaigns/{id}", campaign.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Campaign> campaigns = campaignRepository.findAll();
        assertThat(campaigns).hasSize(0);
    }
}
