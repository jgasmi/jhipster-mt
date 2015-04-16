package com.yjiky.mt.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Campaign.
 */
@Entity
@Table(name = "T_CAMPAIGN")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Campaign implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "action")
    private String action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Campaign campaign = (Campaign) o;

        if ( ! Objects.equals(id, campaign.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", action='" + action + "'" +
                '}';
    }
}
