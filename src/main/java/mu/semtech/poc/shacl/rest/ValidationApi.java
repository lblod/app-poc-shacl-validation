package mu.semtech.poc.shacl.rest;

import mu.semtech.poc.shacl.rdf.ModelUtils;
import mu.semtech.poc.shacl.rdf.ShaclService;
import mu.semtech.poc.shacl.rdf.SparqlService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static mu.semtech.poc.shacl.rdf.ModelUtils.CONTENT_TYPE_TURTLE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/validate")
@CrossOrigin("*")
public class ValidationApi {
    private final ShaclService service;
    private final SparqlService sparqlService;

    public ValidationApi(ShaclService service, SparqlService sparqlService) {
        this.service = service;
        this.sparqlService = sparqlService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = CONTENT_TYPE_TURTLE)
    public ResponseEntity<Void> validate(@RequestBody String dataModel) {
        ValidationReport report = service.validate(dataModel, Lang.TURTLE);
        Model model = report.getModel();
        sparqlService.addUUID(model);
        sparqlService.persist(model);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/file-with-shacl", produces = CONTENT_TYPE_TURTLE, consumes = MULTIPART_FORM_DATA_VALUE)
    public String validateFileWithShacl(@RequestPart("shapes") MultipartFile shapesFile,
                                                        @RequestPart("data") MultipartFile dataFile) {
        ValidationReport report = service.validate(shapesFile, dataFile);
        return ModelUtils.toString(report.getModel(), Lang.TURTLE);
    }

    @PostMapping(value = "/file", produces = CONTENT_TYPE_TURTLE, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> validateFile(@RequestPart("data") MultipartFile dataFile) {
        ValidationReport report = service.validate(dataFile);
        return ResponseEntity.ok(ModelUtils.toString(report.getModel(), Lang.TURTLE));
    }
}
