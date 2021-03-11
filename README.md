# Validation POC

This is a proof of concept for validation / filtering of rdf data based on shacl shapes.

This POC allows to validate / generate a report (in turtle) and filter invalid properties.

## HOW TO

- `cd example`
- Optionally, change the default application profile in `./example/config`
- `docker-compose up`

### Request examples:

#### 1. Input data

```
@prefix ex: <http://example.com/ns#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

ex:Alice
    a ex:Person ;
    ex:ssn "987-65-432A" .

ex:Bob
    a ex:Person ;
    ex:ssn "123-45-6789" ;
    ex:ssn "124-35-6789" .

ex:Calvin
    a ex:Person ;
    ex:birthDate "1971-07-07"^^xsd:date ;
    ex:ssn "987-65-4321";
    ex:www "kekee";
    ex:worksFor ex:UntypedCompany .

ex:Momo
    a ex:Person ;
    ex:ssn "987-65-4321" .

```

#### 1. Validate (default application profile):

`POST http://localhost:8090/validate` 

`Content-Type: text/turtle`

`Body: (input data)`

#### 2. Validate file (default application profile):

`POST http://localhost:8090/validate/file` 

`Content-Type: multipart/form-data`

`Form data: data => data-to-validate.ttl`

#### 3. Validate file using a custom application profile file:

`POST http://localhost:8090/validate/file-with-shacl` 

`Content-Type: multipart/form-data`

`Form data: shapes=> shapes-file.ttl, data => data-to-validate.ttl`

#### 4. Filter (default application profile):

`POST http://localhost:8090/filter`

`Content-Type: text/turtle`

`Body: (input data)`

#### 5. Filter file (default application profile):

`POST http://localhost:8090/filter/file`

`Content-Type: multipart/form-data`

`Form data: data => data-to-filter.ttl`

#### 6. Filter file using a custom application profile file:

`POST http://localhost:8090/filter/file-with-shacl`

`Content-Type: multipart/form-data`

`Form data: shapes=> shapes-file.ttl, data => data-to-validate.ttl`

#### 7. Difference between two rdf files:

`POST http://localhost:8090/compare/difference`

`Content-Type: multipart/form-data`

`Form data: first=> first.ttl, second => second.ttl`

#### 8. Intersection between two rdf files:

`POST http://localhost:8090/compare/intersection`

`Content-Type: multipart/form-data`

`Form data: first=> first.ttl, second => second.ttl`

#### 9. Equality between two rdf files:

`POST http://localhost:8090/compare/equals`

`Content-Type: multipart/form-data`

`Form data: first=> first.ttl, second => second.ttl`

#### 10. Query validation report

```
PREFIX sh: <http://www.w3.org/ns/shacl#>

SELECT * WHERE {
 ?s
    a sh:ValidationReport;
   	sh:conforms ?conforms.
s
 OPTIONAL {
   ?s sh:result ?result.
   OPTIONAL {
    	?result sh:detail ?detail.
    	?detail ?p_detail ?o_detail.
   }
   OPTIONAL {
    	?result sh:focusNode ?focusNode.
   }
   OPTIONAL {
    	?result sh:resultMessage ?resultMessage.
   }
   OPTIONAL {
    	?result sh:resultPath ?resultPath.
   }
   OPTIONAL {
    	?result sh:resultSeverity ?resultSeverity.
    	?resultSeverity ?p_resultSeverity ?o_resultSeverity.
   }
   OPTIONAL {
    	?result sh:sourceConstraint ?sourceConstraint.
   }
   OPTIONAL {
    	?result sh:sourceShape ?sourceShape.
    	?sourceShape ?p_sourceShape ?o_sourceShape.
   }
   OPTIONAL {
    	?result sh:sourceConstraintComponent ?sourceConstraintComponent.
    	?sourceConstraintComponent ?p_sourceConstraintComponent ?o_sourceConstraintComponent.
   }
   OPTIONAL {
    	?result sh:value ?value.
   }
 }
}
```
#### 11. Validation samples

##### Conforms = False

SPARQL endpoint

`https://codex.opendata.api.vlaanderen.be:8888/sparql`

SPARQL query

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX eli: <http://data.europa.eu/eli/ontology#>

SELECT * WHERE {
   ?legalResourceSubdivision a eli:LegalResourceSubdivision;
       eli:is_part_of ?isDeelVan .
   ?isDeelVan a eli:LegalResource ;
        eli:has_part ?heeftDeel ;
}
LIMIT 1
```

Data to validate:

```
@prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix ns1:	<http://data.europa.eu/eli/ontology#> .

<https://codex.vlaanderen.be/id/artikel/1265444>
    rdf:type	ns1:LegalResourceSubdivision ;
	ns1:is_part_of	<http://www.ejustice.just.fgov.be/eli/besluit/2019/7/19/2019013900> .
<http://www.ejustice.just.fgov.be/eli/besluit/2019/7/19/2019013900>
    rdf:type	ns1:LegalResource ;
	ns1:has_part	<https://codex.vlaanderen.be/id/artikel/1265444> .
```
##### Conforms = True

Refer to the [validation](#3-validate-file-using-a-custom-application-profile-file) using the custom application profile:

`shapes => ./example/config/applicationProfile-workaround.ttl`
`data => ./example/config/sparql.ttl`

In details the following properties have been made `OPTIONAL`:

```
sh:targetClass <http://data.europa.eu/eli/ontology#LegalResource>
    sh:path <http://data.europa.eu/eli/ontology#passed_by> 
    sh:path <http://data.europa.eu/eli/ontology#first_date_entry_in_force>
    sh:path <http://data.europa.eu/eli/ontology#type_document>
```

SPARQL query:

```
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX eli: <http://data.europa.eu/eli/ontology#>

SELECT * WHERE {
   ?legalResourceSubdivision a eli:LegalResourceSubdivision;
       eli:is_part_of ?isDeelVan .
   ?isDeelVan a eli:LegalResource ;
        eli:has_part ?heeftDeel ;
        eli:type_document ?typeDocument .

    OPTIONAL {
        ?isDeelVan eli:passed_by ?aangenomenDoor.
    }
    OPTIONAL {
        ?isDeelVan eli:first_date_entry_in_force ?inwerkingtreding.
    }
    OPTIONAL {
        ?isDeelVan eli:type_document ?typeDocument.
    }
}
LIMIT 1
```

