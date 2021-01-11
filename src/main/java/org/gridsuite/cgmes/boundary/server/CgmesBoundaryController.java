/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import com.powsybl.commons.PowsyblException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.gridsuite.cgmes.boundary.server.dto.BoundaryInfo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */
@RestController
@RequestMapping(value = "/" + CgmesBoundaryApi.API_VERSION + "/")
@Api(tags = "cgmes-boundary-server")
@ComponentScan(basePackageClasses = CgmesBoundaryService.class)
public class CgmesBoundaryController {
    private static final List<String> BOUNDARY_PROFILES = List.of("EQ", "TP");

    @Inject
    private CgmesBoundaryService cgmesBoundaryService;

    @GetMapping(value = "/boundaries", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all boundaries", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "The list of all boundaries")})
    public ResponseEntity<List<BoundaryInfo>> getBoundariesList() {
        List<BoundaryInfo> boundaries = cgmesBoundaryService.getBoundariesList();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundaries);
    }

    @GetMapping(value = "/boundaries/last", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get last boundary", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "The last EQ and TP boundaries")})
    public ResponseEntity<List<BoundaryInfo>> getLastBoundaries() {
        List<BoundaryInfo> boundaries = new ArrayList<>();
        BOUNDARY_PROFILES.stream().forEach(profile -> boundaries.add(cgmesBoundaryService.getLastBoundary(profile)));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundaries);
    }

    @GetMapping(value = "/boundaries/{boundaryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a boundary", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "The boundary identified by boundaryId")})
    public ResponseEntity<BoundaryInfo> getBoundary(@PathVariable("boundaryId") String boundaryId) {
        Optional<BoundaryInfo> boundary = cgmesBoundaryService.getBoundary(boundaryId);
        if (!boundary.isPresent()) {
            throw new PowsyblException("Boundary not found for id " + boundaryId);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(boundary.get());
    }

    @PostMapping(value = "/boundaries")
    @ApiOperation(value = "import a boundary file in the database")
    public ResponseEntity<String> importBoundary(@RequestParam("file") MultipartFile boundaryFile) {
        String id = cgmesBoundaryService.importBoundary(boundaryFile);
        return ResponseEntity.ok().body(id);
    }
}
