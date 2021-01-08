/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import com.powsybl.cgmes.model.FullModel;
import com.powsybl.commons.PowsyblException;
import org.gridsuite.cgmes.boundary.server.dto.BoundaryInfo;
import org.gridsuite.cgmes.boundary.server.repositories.BoundaryEntity;
import org.gridsuite.cgmes.boundary.server.repositories.BoundaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@Service
class CgmesBoundaryService {
    private static final String REGEX = "^(.*?(__ENTSOE_%sBD_).*(.xml))$";

    private BoundaryRepository boundaryRepository;

    public CgmesBoundaryService(BoundaryRepository boundaryRepository) {
        this.boundaryRepository = boundaryRepository;
    }

    Optional<BoundaryInfo> getBoundary(String boundaryId) {
        Optional<BoundaryEntity> boundary = boundaryRepository.findById(boundaryId);
        return boundary.map(b -> new BoundaryInfo(b.getId(), b.getFilename(), new String(b.getBoundary().array(), StandardCharsets.UTF_8), b.getScenarioTime()));
    }

    BoundaryInfo getLastBoundary(String profile) {
        List<BoundaryInfo> boundaries = getBoundariesList();
        final String regex = String.format(REGEX, profile);
        BoundaryInfo lastBoundary = getFirstBoundaryOfProfile(boundaries, regex);
        if (lastBoundary == null) {
            throw new PowsyblException("Boundary not found for profile " + profile);
        }
        for (BoundaryInfo boundary : boundaries) {
            if (boundary.getFilename().matches(regex) && boundary.getScenarioTime().isAfter(lastBoundary.getScenarioTime())) {
                lastBoundary = boundary;
            }
        }
        return lastBoundary;
    }

    private static BoundaryInfo getFirstBoundaryOfProfile(List<BoundaryInfo> boundaries, String regex) {
        Iterator<BoundaryInfo> it = boundaries.iterator();
        BoundaryInfo elem;
        while (it.hasNext()) {
            elem = it.next();
            if (elem.getFilename().matches(regex)) {
                return  elem;
            }
        }
        return null;
    }

    String importBoundary(MultipartFile mpfFile) {
        String id;
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(mpfFile.getBytes()))) {
            FullModel fullModel = FullModel.parse(reader);
            id = fullModel.getId();

            ByteBuffer buf = ByteBuffer.wrap(mpfFile.getBytes());
            String filename = mpfFile.getOriginalFilename();
            LocalDateTime scenarioTime = fullModel.getScenarioTime().toLocalDateTime();

            BoundaryEntity entity = new BoundaryEntity(fullModel.getId(), filename, buf, scenarioTime);
            boundaryRepository.insert(entity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return id;
    }

    List<BoundaryInfo> getBoundariesList() {
        List<BoundaryEntity> boundaries = boundaryRepository.findAll();
        return boundaries.stream().map(b -> {
            String boundaryXml = new String(b.getBoundary().array(), StandardCharsets.UTF_8);
            return new BoundaryInfo(b.getId(), b.getFilename(), boundaryXml, b.getScenarioTime());
        }).collect(Collectors.toList());
    }
}
