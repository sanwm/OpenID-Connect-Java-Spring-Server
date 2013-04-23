/*******************************************************************************
 * Copyright 2012 The MITRE Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mitre.openid.connect.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;

import com.google.common.collect.Sets;

@Entity
@Table(name="approved_site")
@NamedQueries({
	@NamedQuery(name = "ApprovedSite.getAll", query = "select a from ApprovedSite a"),
	@NamedQuery(name = "ApprovedSite.getByUserId", query = "select a from ApprovedSite a where a.userId = :userId"),
	@NamedQuery(name = "ApprovedSite.getByClientId", query = "select a from ApprovedSite a where a.clientId = :clientId"),
	@NamedQuery(name = "ApprovedSite.getByClientIdAndUserId", query = "select a from ApprovedSite a where a.clientId = :clientId and a.userId = :userId")
})
public class ApprovedSite implements Serializable {

	private static final long serialVersionUID = 1L;

	// unique id
    private Long id;
    
    // which user made the approval
	private String userId;
	
	// which OAuth2 client is this tied to
	private String clientId;
	
	// when was this first approved?
	private Date creationDate;
	
	// when was this last accessed?
	private Date accessDate;
	
	// if this is a time-limited access, when does it run out?
	private Date timeoutDate;
	
	// what scopes have been allowed
	// this should include all information for what data to access
	private Set<String> allowedScopes;
	
	// If this AP is a WS, link to the WS
	private WhitelistedSite whitelistedSite;
	
	//Link to any access tokens approved through this stored decision
	private Set<OAuth2AccessTokenEntity> approvedAccessTokens = Sets.newHashSet();
	
	/**
	 * Empty constructor
	 */
	public ApprovedSite() {

    }

	/**
     * @return the id
     */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
    	return id;
    }

	/**
     * @param id the id to set
     */
    public void setId(Long id) {
    	this.id = id;
    }

	/**
     * @return the userInfo
     */
    @Basic
    @Column(name="user_id")
    public String getUserId() {
    	return userId;
    }

	/**
     * @param userInfo the userInfo to set
     */
    public void setUserId(String userId) {
    	this.userId = userId;
    }

	/**
     * @return the clientId
     */
    @Basic
    @Column(name="client_id")
    public String getClientId() {
    	return clientId;
    }

	/**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
    	this.clientId = clientId;
    }

	/**
     * @return the creationDate
     */
    @Basic
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="creation_date")
    public Date getCreationDate() {
    	return creationDate;
    }

	/**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
    	this.creationDate = creationDate;
    }

	/**
     * @return the accessDate
     */
    @Basic
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="access_date")
    public Date getAccessDate() {
    	return accessDate;
    }

	/**
     * @param accessDate the accessDate to set
     */
    public void setAccessDate(Date accessDate) {
    	this.accessDate = accessDate;
    }

	/**
     * @return the allowedScopes
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
    		name="approved_site_scope",
    		joinColumns=@JoinColumn(name="owner_id")
    )
    @Column(name="scope")
    public Set<String> getAllowedScopes() {
    	return allowedScopes;
    }

	/**
     * @param allowedScopes the allowedScopes to set
     */
    public void setAllowedScopes(Set<String> allowedScopes) {
    	this.allowedScopes = allowedScopes;
    }

	/**
     * @return the timeoutDate
     */
    @Basic
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="timeout_date")
    public Date getTimeoutDate() {
    	return timeoutDate;
    }

	/**
     * @param timeoutDate the timeoutDate to set
     */
    public void setTimeoutDate(Date timeoutDate) {
    	this.timeoutDate = timeoutDate;
    }

    /**
     * Does this AP entry correspond to a WS?
     * @return
     */
    @Transient
	public Boolean getIsWhitelisted() {
		return (whitelistedSite != null);
	}


	@ManyToOne
	@JoinColumn(name="whitelisted_site_id")
	public WhitelistedSite getWhitelistedSite() {
		return whitelistedSite;
	}

	public void setWhitelistedSite(WhitelistedSite whitelistedSite) {
		this.whitelistedSite = whitelistedSite;
	}

	/**
	 * Has this approval expired?
     * @return
     */
	@Transient
    public boolean isExpired() {
		if (getTimeoutDate() != null) {
			Date now = new Date();
			if (now.after(getTimeoutDate())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
    }

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name="approved_site_id")
	public Set<OAuth2AccessTokenEntity> getApprovedAccessTokens() {
		return approvedAccessTokens;
	}

	/**
	 * @param approvedAccessTokens the approvedAccessTokens to set
	 */
	public void setApprovedAccessTokens(Set<OAuth2AccessTokenEntity> approvedAccessTokens) {
		this.approvedAccessTokens = approvedAccessTokens;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessDate == null) ? 0 : accessDate.hashCode());
		result = prime * result
				+ ((allowedScopes == null) ? 0 : allowedScopes.hashCode());
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result
				+ ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((timeoutDate == null) ? 0 : timeoutDate.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result
				+ ((whitelistedSite == null) ? 0 : whitelistedSite.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ApprovedSite)) {
			return false;
		}
		ApprovedSite other = (ApprovedSite) obj;
		if (accessDate == null) {
			if (other.accessDate != null) {
				return false;
			}
		} else if (!accessDate.equals(other.accessDate)) {
			return false;
		}
		if (allowedScopes == null) {
			if (other.allowedScopes != null) {
				return false;
			}
		} else if (!allowedScopes.equals(other.allowedScopes)) {
			return false;
		}
		if (clientId == null) {
			if (other.clientId != null) {
				return false;
			}
		} else if (!clientId.equals(other.clientId)) {
			return false;
		}
		if (creationDate == null) {
			if (other.creationDate != null) {
				return false;
			}
		} else if (!creationDate.equals(other.creationDate)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (timeoutDate == null) {
			if (other.timeoutDate != null) {
				return false;
			}
		} else if (!timeoutDate.equals(other.timeoutDate)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		if (whitelistedSite == null) {
			if (other.whitelistedSite != null) {
				return false;
			}
		} else if (!whitelistedSite.equals(other.whitelistedSite)) {
			return false;
		}
		return true;
	}
	
}
