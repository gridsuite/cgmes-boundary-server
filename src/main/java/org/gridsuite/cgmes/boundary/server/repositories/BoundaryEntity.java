/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server.repositories;

import lombok.Getter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com
 */
@Getter
@Table("boundaries")
public class BoundaryEntity {

    @PrimaryKey
    private String id;

    private String filename;

    private ByteBuffer boundary;

    private LocalDateTime scenarioTime;

    public BoundaryEntity(String id, String filename, ByteBuffer boundary, LocalDateTime scenarioTime) {
        this.id = id;
        this.filename = filename;
        this.boundary = boundary;
        this.scenarioTime = scenarioTime;
    }
}
