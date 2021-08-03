/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */
@NoArgsConstructor
@Getter
@Schema(description = "Boundary content")
public class BoundaryContent extends BoundaryInfo {

    private String boundary;

    public BoundaryContent(String id, String filename, LocalDateTime scenarioTime, String boundary) {
        super(id, filename, scenarioTime);
        this.boundary = boundary;
    }
}
