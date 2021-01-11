/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDateTime;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ApiModel("Boundary attributes")
public class BoundaryInfo {

    private String id;

    private String filename;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime scenarioTime;

    private String boundary;
}
