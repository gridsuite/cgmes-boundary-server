/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import org.gridsuite.cgmes.boundary.server.dto.BoundaryInfo;
import org.gridsuite.cgmes.boundary.server.fullmodel.FullModel;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */
@Service
class CgmesBoundaryService {

    private BoundaryRepository boundaryRepository;

    public CgmesBoundaryService(BoundaryRepository boundaryRepository) {
        this.boundaryRepository = boundaryRepository;
    }

    Optional<BoundaryInfo> getBoundary(String boundaryId) {
        Optional<BoundaryEntity> boundary = boundaryRepository.findById(boundaryId);
        return boundary.map(b -> new BoundaryInfo(b.getId(), b.getFilename(), new String(b.getBoundary().array(), StandardCharsets.UTF_8)));
    }

    String importBoundary(MultipartFile mpfFile) {
        String id;
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(mpfFile.getBytes()))) {
            FullModel fullModel = FullModel.parse(reader);
            id = fullModel.getId();

            ByteBuffer buf = ByteBuffer.wrap(mpfFile.getBytes());
            String filename = mpfFile.getOriginalFilename();

            BoundaryEntity entity = new BoundaryEntity(fullModel.getId(), filename, buf);
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
            return new BoundaryInfo(b.getId(), b.getFilename(), boundaryXml);
        }).collect(Collectors.toList());
    }
}