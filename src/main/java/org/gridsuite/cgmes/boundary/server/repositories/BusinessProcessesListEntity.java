/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server.repositories;

import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com
 */
@Getter
@Table("business_processes")
public class BusinessProcessesListEntity {
    @PrimaryKey
    private String name;

    private ByteBuffer businessProcesses;

    public BusinessProcessesListEntity(String name, ByteBuffer businessProcesses) {
        this.name = name;
        this.businessProcesses = businessProcesses;
    }
}
