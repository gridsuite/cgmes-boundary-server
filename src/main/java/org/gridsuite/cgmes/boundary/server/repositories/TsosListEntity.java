/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server.repositories;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com
 */
@NoArgsConstructor
@Getter
@Table(name = "tsos")
@Entity
public class TsosListEntity {
    @Id
    private String name;

    @Column(name = "tsos", columnDefinition = "CLOB")
    private String tsos;

    public TsosListEntity(String name, String tsos) {
        this.name = name;
        this.tsos = tsos;
    }
}
