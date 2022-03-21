/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server.repositories;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.Getter;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com
 */
@NoArgsConstructor
@Getter
@Table(name = "business_processes")
@Entity
public class BusinessProcessesListEntity {
    @Id
    private String name;

    @Lob
    private byte[] businessProcesses;

    public BusinessProcessesListEntity(String name, byte[] businessProcesses) {
        this.name = name;
        this.businessProcesses = businessProcesses;
    }
}
