/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import com.powsybl.commons.PowsyblException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gridsuite.cgmes.boundary.server.dto.BoundaryContent;
import org.gridsuite.cgmes.boundary.server.dto.BoundaryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + CgmesBoundaryApi.API_VERSION + "/")
@Tag(name = "cgmes-boundary-server")
@ComponentScan(basePackageClasses = CgmesBoundaryService.class)
public class CgmesBoundaryController {
    private static final List<String> BOUNDARY_PROFILES = List.of("EQ", "TP");

    @Autowired
    private CgmesBoundaryService cgmesBoundaryService;

    @GetMapping(value = "/boundaries", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all boundaries")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The list of all boundaries")})
    public ResponseEntity<List<BoundaryContent>> getBoundariesList() {
        List<BoundaryContent> boundaries = cgmesBoundaryService.getBoundariesList();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundaries);
    }

    @GetMapping(value = "/boundaries/infos", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all boundaries infos")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The list of all boundaries infos")})
    public ResponseEntity<List<BoundaryInfo>> getBoundariesInfosList() {
        List<BoundaryInfo> boundaries = cgmesBoundaryService.getBoundariesInfosList();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundaries);
    }

    @GetMapping(value = "/boundaries/last", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get last boundary")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The last EQ and TP boundaries")})
    public ResponseEntity<List<BoundaryContent>> getLastBoundaries() {
        List<BoundaryContent> boundaries = BOUNDARY_PROFILES.stream().map(profile -> cgmesBoundaryService.getLastBoundary(profile)).collect(Collectors.toList());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundaries);
    }

    @GetMapping(value = "/boundaries/{boundaryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a boundary")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The boundary identified by boundaryId")})
    public ResponseEntity<BoundaryContent> getBoundary(@PathVariable("boundaryId") String boundaryId) {
        Optional<BoundaryContent> boundary = cgmesBoundaryService.getBoundary(boundaryId);
        if (!boundary.isPresent()) {
            throw new PowsyblException("Boundary not found for id " + boundaryId);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundary.get());
    }

    @PostMapping(value = "/boundaries", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "import a boundary file in the database")
    public ResponseEntity<String> importBoundary(@RequestParam("file") MultipartFile boundaryFile) {
        String id = cgmesBoundaryService.importBoundary(boundaryFile);
        return ResponseEntity.ok().body(id);
    }

    @GetMapping(value = "/boundaries/{boundaryId}/exists")
    @Operation(summary = "Check if the boundary exists")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "If the boundary exists or not.")})
    public ResponseEntity<Boolean> boundaryExists(@PathVariable("boundaryId") String boundaryId) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(cgmesBoundaryService.boundaryExists(boundaryId));
    }

    @GetMapping(value = "/tsos", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of all available tsos")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The list of all available tsos")})
    public ResponseEntity<Set<String>> getTsos() {
        Optional<Set<String>> tsos = cgmesBoundaryService.getTsos();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(tsos.orElse(Collections.emptySet()));
    }

    @PostMapping(value = "/tsos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "import a list of all available tsos in the database")
    public ResponseEntity<Void> importTsos(@RequestParam("file") MultipartFile tsosFile) {
        cgmesBoundaryService.importTsos(tsosFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/business-processes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get list of all available business processes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The list of all available business processes")})
    public ResponseEntity<Set<String>> getBusinessProcesses() {
        Optional<Set<String>> businessProcesses = cgmesBoundaryService.getBusinessProcesses();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(businessProcesses.orElse(Collections.emptySet()));
    }

    @PostMapping(value = "/business-processes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "import a list of all available business processes in the database")
    public ResponseEntity<Void> importBusinessProcesses(@RequestParam("file") MultipartFile businessProcessesFile) {
        cgmesBoundaryService.importBusinessProcesses(businessProcessesFile);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/boundaries/{boundaryId}")
    @Operation(summary = "Delete a boundary")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "The boundary identified by boundaryId has been deleted")})
    public ResponseEntity<Void> deleteBoundary(@PathVariable("boundaryId") String boundaryId) {
        cgmesBoundaryService.deleteBoundary(boundaryId);
        return ResponseEntity.ok().build();
    }
}
