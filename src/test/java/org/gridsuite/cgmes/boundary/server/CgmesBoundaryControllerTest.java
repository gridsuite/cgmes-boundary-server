/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.cgmes.boundary.server;

import org.apache.commons.io.IOUtils;
import org.gridsuite.cgmes.boundary.server.repositories.BoundaryRepository;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


/**
 * @author Franck Lecuyer <franck.lecuyer at rte-france.com>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {CgmesBoundaryApplication.class})
public class CgmesBoundaryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    BoundaryRepository boundaryRepository;

    @Autowired
    private CgmesBoundaryService cgmesBoundaryService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("file", "20191106T0930Z__ENTSOE_EQBD_001.xml",
                "text/xml", new FileInputStream(ResourceUtils.getFile("classpath:20191106T0930Z__ENTSOE_EQBD_001.xml")));

        MockMultipartFile file2 = new MockMultipartFile("file", "20191106T0930Z__ENTSOE_TPBD_001.xml",
                "text/xml", new FileInputStream(ResourceUtils.getFile("classpath:20191106T0930Z__ENTSOE_TPBD_001.xml")));

        MockMultipartFile file3 = new MockMultipartFile("file", "20201106T0930Z__ENTSOE_EQBD_001.xml",
                "text/xml", new FileInputStream(ResourceUtils.getFile("classpath:20201106T0930Z__ENTSOE_EQBD_001.xml")));

        MockMultipartFile file4 = new MockMultipartFile("file", "20201106T0930Z__ENTSOE_TPBD_001.xml",
                "text/xml", new FileInputStream(ResourceUtils.getFile("classpath:20201106T0930Z__ENTSOE_TPBD_001.xml")));

        MockMultipartHttpServletRequestBuilder builderOk1 = MockMvcRequestBuilders.multipart("/v1/boundaries");
        builderOk1.with(request -> {
            request.setMethod("POST");
            return request;
        });

        MockMultipartHttpServletRequestBuilder builderOk2 = MockMvcRequestBuilders.multipart("/v1/boundaries");
        builderOk2.with(request -> {
            request.setMethod("POST");
            return request;
        });

        MockMultipartHttpServletRequestBuilder builderOk3 = MockMvcRequestBuilders.multipart("/v1/boundaries");
        builderOk3.with(request -> {
            request.setMethod("POST");
            return request;
        });

        MockMultipartHttpServletRequestBuilder builderOk4 = MockMvcRequestBuilders.multipart("/v1/boundaries");
        builderOk4.with(request -> {
            request.setMethod("POST");
            return request;
        });

        // import first boundary
        MvcResult result = mvc.perform(builderOk1
                .file(file1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();
        assertEquals("urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71", result.getResponse().getContentAsString());

        // import second boundary
        result = mvc.perform(builderOk2
                .file(file2))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();
        assertEquals("urn:uuid:f1582c44-d9e2-4ea0-afdc-dba189ab4358", result.getResponse().getContentAsString());

        // get list of boundary infos
        mvc.perform(get("/v1/boundaries/infos")
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andExpect(content().json("[{\"id\":\"urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71\",\"filename\":\"20191106T0930Z__ENTSOE_EQBD_001.xml\",\"scenarioTime\":\"2020-06-29T00:00:00\"},{\"id\":\"urn:uuid:f1582c44-d9e2-4ea0-afdc-dba189ab4358\",\"filename\":\"20191106T0930Z__ENTSOE_TPBD_001.xml\",\"scenarioTime\":\"2020-06-29T00:00:00\"}]"));

        // get list of boundary set
        mvc.perform(get("/v1/boundaries")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value("urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71"))
                .andExpect(jsonPath("[1].id").value("urn:uuid:f1582c44-d9e2-4ea0-afdc-dba189ab4358"))
                .andExpect(jsonPath("[0].filename").value("20191106T0930Z__ENTSOE_EQBD_001.xml"))
                .andExpect(jsonPath("[1].filename").value("20191106T0930Z__ENTSOE_TPBD_001.xml"));

        // check existence of boundary set
        result = mvc.perform(get("/v1/boundaries/urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71/exists")
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andReturn();
        assertTrue(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        // check non existence of boundary set
        result = mvc.perform(get("/v1/boundaries/urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f72/exists")
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andReturn();
        assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));

        // get one existing boundary set
        result = mvc.perform(get("/v1/boundaries/urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("id").value("urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71"))
                .andExpect(jsonPath("filename").value("20191106T0930Z__ENTSOE_EQBD_001.xml"))
                .andReturn();
        JSONObject obj = new JSONObject(result.getResponse().getContentAsString());
        String boundaryXml = obj.getString("boundary");

        StringWriter writer = new StringWriter();
        IOUtils.copy(new FileInputStream(ResourceUtils.getFile("classpath:20191106T0930Z__ENTSOE_EQBD_001.xml")), writer, Charset.forName("UTF-8"));
        String xmlExpected = writer.toString();
        assertEquals(xmlExpected, boundaryXml);

        // get one non existing boundary set
        assertTrue(assertThrows(Exception.class, () -> mvc.perform(get("/v1/boundaries/urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f70")
                .contentType(APPLICATION_JSON)))
                .getMessage().matches("(.*)Boundary not found for id(.*)"));

        // import first boundary
        result = mvc.perform(builderOk3
                .file(file3))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        // import second boundary
        result = mvc.perform(builderOk4
                .file(file4))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        result = mvc.perform(get("/v1/boundaries/last")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value("urn:uuid:58c98e13-d37f-4f0b-82c9-066deda4cd20"))
                .andExpect(jsonPath("[0].filename").value("20201106T0930Z__ENTSOE_EQBD_001.xml"))
                .andExpect(jsonPath("[0].scenarioTime").value("2020-11-29T00:00:00"))
                .andExpect(jsonPath("[1].id").value("urn:uuid:55257eed-ac1d-4b99-8828-3e1b47f5e0a1"))
                .andExpect(jsonPath("[1].filename").value("20201106T0930Z__ENTSOE_TPBD_001.xml"))
                .andExpect(jsonPath("[1].scenarioTime").value("2020-11-29T00:00:04"))
                .andReturn();

        // delete non existing boundary
        result = mvc.perform(delete("/v1/boundaries/urn:uuid:11111111-aab9-4284-a965-71d5cd151f71"))
            .andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());

        // delete existing boundary
        mvc.perform(delete("/v1/boundaries/urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71"))
            .andExpect(status().isOk())
            .andReturn();

        result = mvc.perform(get("/v1/boundaries/urn:uuid:3e3f7738-aab9-4284-a965-71d5cd151f71/exists")
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andReturn();
        assertFalse(Boolean.parseBoolean(result.getResponse().getContentAsString()));
    }

    @Test
    public void testTsosList() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "tsos.json",
            "application/json", new FileInputStream(ResourceUtils.getFile("classpath:tsos.json")));

        MockMultipartHttpServletRequestBuilder builderOk = MockMvcRequestBuilders.multipart("/v1/tsos");
        builderOk.with(request -> {
            request.setMethod("POST");
            return request;
        });

        // import tsos list
        mvc.perform(builderOk
            .file(file))
            .andExpect(status().isOk())
            .andReturn();

        // get tsos list
        MvcResult result = mvc.perform(get("/v1/tsos")
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andReturn();

        StringWriter writer = new StringWriter();
        IOUtils.copy(new FileInputStream(ResourceUtils.getFile("classpath:tsos.json")), writer, Charset.forName("UTF-8"));
        String expected = writer.toString();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    public void testBusinessProcessesList() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "business_processes.json",
            "application/json", new FileInputStream(ResourceUtils.getFile("classpath:business_processes.json")));

        MockMultipartHttpServletRequestBuilder builderOk = MockMvcRequestBuilders.multipart("/v1/business-processes");
        builderOk.with(request -> {
            request.setMethod("POST");
            return request;
        });

        // import business processes list
        mvc.perform(builderOk
            .file(file))
            .andExpect(status().isOk())
            .andReturn();

        // get business processes list
        MvcResult result = mvc.perform(get("/v1/business-processes")
            .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
            .andReturn();

        StringWriter writer = new StringWriter();
        IOUtils.copy(new FileInputStream(ResourceUtils.getFile("classpath:business_processes.json")), writer, Charset.forName("UTF-8"));
        String expected = writer.toString();
        assertEquals(expected, result.getResponse().getContentAsString());
    }
}
