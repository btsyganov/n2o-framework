package net.n2oapp.framework.engine.data.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.dataprovider.N2oRestDataProvider;
import net.n2oapp.framework.engine.data.rest.json.RestEngineTimeModule;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SpringRestDataProviderEngineTest {
    @Test
    public void testSimple() {
        //самый простой случай
        TestRestTemplate restTemplate = new TestRestTemplate("");
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setQuery("http://www.example.org/{id}");
        dataProvider.setMethod(N2oRestDataProvider.Method.POST);
        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        actionEngine.invoke(dataProvider, request);
        Map<String, Object> header = (Map<String, Object>) restTemplate.getRequestHeader();
        assertThat(header.size(), is(1));
        assertThat(header.get("Content-Type"), is(Arrays.asList("application/json")));
        assertThat(restTemplate.getQuery(), is("http://www.example.org/1"));

        //случай с повторением параметра
        restTemplate = new TestRestTemplate("");
        actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        dataProvider = new N2oRestDataProvider();
        dataProvider.setQuery("http://www.example.org/{id}/{id}");
        dataProvider.setMethod(N2oRestDataProvider.Method.POST);
        actionEngine.invoke(dataProvider, request);
        assertThat(restTemplate.getQuery(), is("http://www.example.org/1/1"));

    }

    @Test
    public void testFails() {
        TestRestTemplate restTemplate = new TestRestTemplate(new MockClientHttpResponse("Error".getBytes(StandardCharsets.UTF_8), HttpStatus.INTERNAL_SERVER_ERROR));
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        N2oRestDataProvider invocation = new N2oRestDataProvider();
        invocation.setQuery("http://www.example.org/");
        invocation.setMethod(N2oRestDataProvider.Method.GET);
        Map<String, Object> request = new HashMap<>();

        try {
            actionEngine.invoke(invocation, request);
            Assert.fail();
        } catch (HttpStatusCodeException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat(e.getResponseBodyAsString(), is("Error"));
        }
    }

    @Test
    public void testResponse() throws UnsupportedEncodingException {
        //response integer
        TestRestTemplate restTemplate = new TestRestTemplate("1");
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        N2oRestDataProvider invocation = new N2oRestDataProvider();
        invocation.setQuery("http://www.example.org/");
        invocation.setMethod(N2oRestDataProvider.Method.GET);
        Map<String, Object> request = new HashMap<>();

        Object result = actionEngine.invoke(invocation, request);
        assertThat(result, is(1));

        //response map
        restTemplate = new TestRestTemplate("{\"id\" : \"1\"}");
        actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        invocation = new N2oRestDataProvider();
        invocation.setQuery("http://www.example.org/");
        invocation.setMethod(N2oRestDataProvider.Method.GET);
        request = new HashMap<>();

        result = actionEngine.invoke(invocation, request);
        assertThat(result, instanceOf(DataSet.class));
        assertThat(((DataSet)result).get("id"), is("1"));

        //response list
        restTemplate = new TestRestTemplate("[{\"id\" : \"1\"}]");
        actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        invocation = new N2oRestDataProvider();
        invocation.setQuery("http://www.example.org/");
        invocation.setMethod(N2oRestDataProvider.Method.GET);
        request = new HashMap<>();

        result = actionEngine.invoke(invocation, request);
        assertThat(result, instanceOf(List.class));
        assertThat(((List)result).get(0), instanceOf(DataSet.class));
        assertThat(((DataSet)((List)result).get(0)).get("id"), is("1"));

        //response empty
        restTemplate = new TestRestTemplate("");
        actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        invocation = new N2oRestDataProvider();
        invocation.setQuery("http://www.example.org/");
        invocation.setMethod(N2oRestDataProvider.Method.GET);
        request = new HashMap<>();

        result = actionEngine.invoke(invocation, request);
        assertThat(result, nullValue());
    }

    @Test
    public void testReplacePlaceholders() {
        TestRestTemplate restTemplate = new TestRestTemplate("");
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setMethod(N2oRestDataProvider.Method.POST);
//        dataProvider.setFiltersSeparator("&");//by default
        dataProvider.setJoinSeparator(";");
        dataProvider.setSelectSeparator(";");
        dataProvider.setSortingSeparator("&");
        dataProvider.setQuery("http://www.example.org/findAll;{select};{join}?{filters}&amp;{sorting}&amp;offset={offset}&amp;limit={limit}&amp;count={count}&amp;page={page}");

        Map<String, Object> request = new HashMap<>();
        request.put("select", Arrays.asList("id", "name"));
        request.put("join", Arrays.asList("join=table2", "join=table3"));
        request.put("filters", Arrays.asList("id={id}", "name={name}"));
        request.put("sorting", Arrays.asList("sort=id,{idSortDir}", "sort=name,{nameSortDir}"));
        request.put("limit", 1);
        request.put("offset", 2);
        request.put("count", 3);
        request.put("id", 123);
        request.put("name", "test");
        request.put("idSortDir", "ASC");
        request.put("nameSortDir", "DESC");
        request.put("page", 1);
        actionEngine.invoke(dataProvider, request);
        assertThat(restTemplate.getQuery(), is("http://www.example.org/findAll;id;name;join=table2;join=table3?id=123&name=test&sort=id,ASC&sort=name,DESC&offset=2&limit=1&count=3&page=1"));
        Map<String, Object> body = (Map<String, Object>) restTemplate.getRequestBody();
        assertThat(body.get("id"), is("123"));
        assertThat(body.get("name"), is("test"));

        restTemplate = new TestRestTemplate("");
        actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        dataProvider = new N2oRestDataProvider();
        dataProvider.setMethod(N2oRestDataProvider.Method.POST);
        dataProvider.setQuery("http://www.example.org/findAll?{filters}");
        dataProvider.setFiltersSeparator("&");
        request = new HashMap<>();
        request.put("select", Arrays.asList("id", "name"));
        request.put("join", Arrays.asList("join=table2", "join=table3"));
        request.put("filters", Arrays.asList("id={id}", "name={name}"));
        request.put("sorting", Arrays.asList("sort=id,{idSortDir}", "sort=name,{nameSortDir}"));
        request.put("limit", 1);
        request.put("offset", 2);
        request.put("count", 3);
        request.put("id", 123);
        request.put("name", "test");
        request.put("idSortDir", "ASC");
        request.put("nameSortDir", "DESC");
        request.put("page", 1);

        actionEngine.invoke(dataProvider, request);
        assertThat(restTemplate.getQuery(), is("http://www.example.org/findAll?id=123&name=test"));

        body = (Map<String, Object>) restTemplate.getRequestBody();
        assertThat(body.get("offset"), is(2));
        assertThat(body.get("idSortDir"), is("ASC"));
        assertThat(body.get("limit"), is(1));
        assertThat(body.get("count"), is(3));
        assertThat(body.get("name"), is("test"));
        assertThat(body.get("nameSortDir"), is("DESC"));
        assertThat(body.get("id"), is("123"));
        assertThat(body.get("page"), is(1));
    }

    @Test
    public void testBaseUrl() {
        DataSet res = new DataSet();
        res.put("id", 1);
        res.put("name", "test");
        TestRestTemplate restTemplate = new TestRestTemplate("");
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        actionEngine.setBaseRestUrl("http://localhost:8080");
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setMethod(N2oRestDataProvider.Method.POST);
        dataProvider.setQuery("/findAll");
        Map<String, Object> request = new HashMap<>();
        actionEngine.invoke(dataProvider, request);
        assertThat(restTemplate.getQuery(), is("http://localhost:8080/findAll"));

        //случай без / в url
        restTemplate = new TestRestTemplate("");
        actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        dataProvider = new N2oRestDataProvider();
        actionEngine.setBaseRestUrl("http://localhost:8080");
        dataProvider.setMethod(N2oRestDataProvider.Method.POST);
        dataProvider.setQuery("findAll");
        request = new HashMap<>();
        actionEngine.invoke(dataProvider, request);
        assertThat(restTemplate.getQuery(), is("http://localhost:8080/findAll"));
    }

    @Test
    public void testNoMethodSet() {
        DataSet req = new DataSet();
        req.put("id", 1);
        req.put("name", "test");
        TestRestTemplate restTemplate = new TestRestTemplate("");
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restTemplate, new ObjectMapper());
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setQuery("http://www.example.org/{id}");

        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        actionEngine.invoke(dataProvider, request);
        assertThat(restTemplate.getQuery(), is("http://www.example.org/1"));
    }

    @Test
    public void testDateSerializing() {
        TestRestTemplate restClient = new TestRestTemplate("");
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);
        objectMapper.registerModule(new JavaTimeModule());
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restClient, objectMapper);
        actionEngine.setBaseRestUrl("http://localhost:8080");
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setQuery("test/path?{filters}");
        dataProvider.setFiltersSeparator("&");

        Map<String, Object> request = new HashMap<>();

        request.put("date.begin", new Date(0));
        request.put("filters", Collections.singletonList("date={date.begin}"));
        actionEngine.invoke(dataProvider, request);
        assertThat(restClient.getQuery(), is("http://localhost:8080/test/path?date=1970-01-01T00:00:00"));
    }

    @Test
    public void testDateDeserializing() {
        TestRestTemplate restClient = new TestRestTemplate("{\"date_begin\":\"2018-11-17\"}");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new RestEngineTimeModule(new String[]{"yyyy-MM-dd'T'HH:mm:s", "yyyy-MM-dd"}));
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restClient, objectMapper);
        actionEngine.setBaseRestUrl("http://localhost:8080");
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setQuery("/test");
        Map<String, Object> request = new HashMap<>();

        DataSet result = (DataSet) actionEngine.invoke(dataProvider, request);
        assertThat(result.get("date_begin"), instanceOf(Date.class));
    }

    @Test
    public void testListParameters() {
        TestRestTemplate restClient = new TestRestTemplate("");
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restClient, new ObjectMapper());
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setFiltersSeparator("&");
        dataProvider.setQuery("http://www.example.org/path?{filters}");
        dataProvider.setMethod(N2oRestDataProvider.Method.GET);
        Map<String, Object> request = new HashMap<>();
        request.put("filters", new ArrayList<>());
        request.put("filter1*.id", Arrays.asList("1", "2", null));
        request.put("filter2*.name", Arrays.asList("a", "b"));
        request.put("filter3*.value", "testValue");
        ((List) request.get("filters")).add("filter1={filter1*.id}");
        ((List) request.get("filters")).add("filter2={filter2*.name}");
        ((List) request.get("filters")).add("filter3={filter3*.value}");

        actionEngine.invoke(dataProvider, request);

        assertThat(restClient.getQuery(), is("http://www.example.org/path?filter1=1&filter1=2&filter2=a&filter2=b&filter3=testValue"));
    }

    @Test
    public void testEncoding() {
        TestRestTemplate restClient = new TestRestTemplate("");
        ObjectMapper objectMapper = new ObjectMapper();
        SpringRestDataProviderEngine actionEngine = new SpringRestDataProviderEngine(restClient, objectMapper);
        actionEngine.setBaseRestUrl("http://localhost:8080");
        N2oRestDataProvider dataProvider = new N2oRestDataProvider();
        dataProvider.setQuery("test/path?{filters}");
        dataProvider.setFiltersSeparator("&");

        Map<String, Object> request = new HashMap<>();
        request.put("space", " ");
        request.put("cyrillic", "ы");
        request.put("quote", "\"");
        request.put("param", "{abc}");
        request.put("nullParam", null);
        request.put("filters", Arrays.asList("f1={space}", "f2={cyrillic}", "f3={quote}", "f4={param}", "f5={nullParam}"));

        actionEngine.invoke(dataProvider, request);
        assertThat(restClient.getQuery(), is("http://localhost:8080/test/path?f1=%20&f2=%D1%8B&f3=%22&f4=%7Babc%7D"));
    }
}
