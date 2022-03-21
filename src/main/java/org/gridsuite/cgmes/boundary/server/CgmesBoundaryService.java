/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import com.powsybl.cgmes.model.FullModel;
import com.powsybl.commons.PowsyblException;
import org.gridsuite.cgmes.boundary.server.dto.BoundaryContent;
import org.gridsuite.cgmes.boundary.server.dto.BoundaryInfo;
import org.gridsuite.cgmes.boundary.server.repositories.BoundaryEntity;
import org.gridsuite.cgmes.boundary.server.repositories.BoundaryRepository;
import org.gridsuite.cgmes.boundary.server.repositories.BusinessProcessesListEntity;
import org.gridsuite.cgmes.boundary.server.repositories.BusinessProcessesRepository;
import org.gridsuite.cgmes.boundary.server.repositories.TsosListEntity;
import org.gridsuite.cgmes.boundary.server.repositories.TsosRepository;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 * @author Etienne Homer <etienne.homer at rte-france.com>
 */
@Service
class CgmesBoundaryService {
    private static final String REGEX = "^(.*(__ENTSOE_%sBD_).*(.xml))$";

    private BoundaryRepository boundaryRepository;
    private TsosRepository tsosRepository;
    private BusinessProcessesRepository businessProcessesRepository;

    private static final String TSOS_LIST_NAME = "tsos";
    private static final String BUSINESS_PROCESS_LIST_NAME = "businessProcesses";

    public CgmesBoundaryService(BoundaryRepository boundaryRepository,
                                TsosRepository tsosRepository,
                                BusinessProcessesRepository businessProcessesRepository) {
        this.boundaryRepository = boundaryRepository;
        this.tsosRepository = tsosRepository;
        this.businessProcessesRepository = businessProcessesRepository;
    }

    Optional<BoundaryContent> getBoundary(String boundaryId) {
        Optional<BoundaryEntity> boundary = boundaryRepository.findById(boundaryId);
        return boundary.map(b -> new BoundaryContent(b.getId(), b.getFilename(), b.getScenarioTime(), new String(b.getBoundary(), StandardCharsets.UTF_8)));
    }

    BoundaryContent getLastBoundary(String profile) {
        List<BoundaryContent> boundaries = getBoundariesList();
        final String regex = String.format(REGEX, profile);
        Optional<BoundaryContent> firstBoundary = boundaries.stream().filter(boundaryInfo -> boundaryInfo.getFilename().matches(regex)).findFirst();
        if (firstBoundary.isEmpty()) {
            throw new PowsyblException("Boundary not found for profile " + profile);
        }
        BoundaryContent mostRecentBoundary = firstBoundary.get();
        for (BoundaryContent boundary : boundaries) {
            if (boundary.getFilename().matches(regex) && boundary.getScenarioTime().isAfter(mostRecentBoundary.getScenarioTime())) {
                mostRecentBoundary = boundary;
            }
        }
        return mostRecentBoundary;
    }

    String importBoundary(MultipartFile mpfFile) {
        String id;
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(mpfFile.getBytes()))) {
            FullModel fullModel = FullModel.parse(reader);
            id = fullModel.getId();

            String filename = mpfFile.getOriginalFilename();
            LocalDateTime scenarioTime = fullModel.getScenarioTime().toLocalDateTime();

            BoundaryEntity entity = new BoundaryEntity(fullModel.getId(), filename, mpfFile.getBytes(), scenarioTime);
            boundaryRepository.save(entity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return id;
    }

    List<BoundaryContent> getBoundariesList() {
        List<BoundaryEntity> boundaries = boundaryRepository.findAll();
        return boundaries.stream().map(b -> {
            String boundaryXml = new String(b.getBoundary(), StandardCharsets.UTF_8);
            return new BoundaryContent(b.getId(), b.getFilename(), b.getScenarioTime(), boundaryXml);
        }).collect(Collectors.toList());
    }

    List<BoundaryInfo> getBoundariesInfosList() {
        List<BoundaryEntity> boundaries = boundaryRepository.findAll();
        return boundaries.stream().map(b -> new BoundaryInfo(b.getId(), b.getFilename(), b.getScenarioTime())).collect(Collectors.toList());
    }

    Boolean boundaryExists(String boundaryId) {
        return boundaryRepository.findById(boundaryId).isPresent();
    }

    Optional<Set<String>> getTsos() {
        Optional<TsosListEntity> tsosList = tsosRepository.findById(TSOS_LIST_NAME);
        return tsosList.map(t -> {
            Set<String> res = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            JSONArray array = new JSONArray(new String(t.getTsos(), StandardCharsets.UTF_8));
            for (int i = 0; i < array.length(); ++i) {
                res.add(array.getString(i));
            }
            return res;
        });
    }

    Optional<Set<String>> getBusinessProcesses() {
        Optional<BusinessProcessesListEntity> businessProcessesList = businessProcessesRepository.findById(BUSINESS_PROCESS_LIST_NAME);
        return businessProcessesList.map(t -> {
            Set<String> res = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            JSONArray array = new JSONArray(new String(t.getBusinessProcesses(), StandardCharsets.UTF_8));
            for (int i = 0; i < array.length(); ++i) {
                res.add(array.getString(i));
            }
            return res;
        });
    }

    void importTsos(MultipartFile tsosFile) {
        try {
            TsosListEntity entity = new TsosListEntity(TSOS_LIST_NAME, tsosFile.getBytes());
            tsosRepository.save(entity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void importBusinessProcesses(MultipartFile businessProcessesFile) {
        try {
            BusinessProcessesListEntity entity = new BusinessProcessesListEntity(BUSINESS_PROCESS_LIST_NAME, businessProcessesFile.getBytes());
            businessProcessesRepository.save(entity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    void deleteBoundary(String boundaryId) {
        if (boundaryRepository.existsById(boundaryId)) {
            boundaryRepository.deleteById(boundaryId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
