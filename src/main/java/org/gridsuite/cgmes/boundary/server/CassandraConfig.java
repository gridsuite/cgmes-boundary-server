/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import org.gridsuite.cgmes.boundary.server.repositories.BoundaryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */
@Configuration
@PropertySource(value = {"classpath:cassandra.properties"})
@PropertySource(value = {"file:/config/cassandra.properties"}, ignoreResourceNotFound = true)
@EnableCassandraRepositories(basePackageClasses = BoundaryRepository.class)
public class CassandraConfig extends AbstractCassandraConfiguration {

    static final String KEYSPACE_CGMES_BOUNDARY = "cgmes_boundary";

    @Override
    protected String getKeyspaceName() {
        return KEYSPACE_CGMES_BOUNDARY;
    }

    @Bean
    public CqlSessionFactoryBean cassandraSession(Environment env) {
        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints(env.getRequiredProperty("cassandra.contact-points"));
        session.setPort(Integer.parseInt(env.getRequiredProperty("cassandra.port")));
        session.setLocalDatacenter("datacenter1");
        session.setKeyspaceName(getKeyspaceName());
        return session;
    }
}
