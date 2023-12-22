package br.com.likwi.awsfargate.controller;

import br.com.likwi.awsfargate.enums.EventType;
import br.com.likwi.awsfargate.model.Product;
import br.com.likwi.awsfargate.repository.ProductRepository;
import br.com.likwi.awsfargate.useCase.ProductPublisherUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductRepository productRepository;
    private ProductPublisherUseCase productPublisherUseCase;

    public ProductController(ProductRepository productRepository, ProductPublisherUseCase productPublisherUseCase) {
        this.productRepository = productRepository;
        this.productPublisherUseCase = productPublisherUseCase;
    }

    @GetMapping
    public Iterable<Product> findAll() {
        return this.productRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id) {
        final Product product = this.productRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @GetMapping(path = "/bycode")
    public ResponseEntity<Product> findByCode(@RequestParam String code) {
        final Product product = this.productRepository.findByCode(code)
                .orElseThrow(NotFoundException::new);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {

        product = this.productRepository.save(product);
        this.productPublisherUseCase.publishProductEvent(product, EventType.PRODUCT_CREATED, "joao");
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> update(@RequestBody Product product, @PathVariable("id") long id) {
        final Product productOld = this.productRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        keepIdAndCode(product, id, productOld);
        this.productRepository.save(product);
        this.productPublisherUseCase.publishProductEvent(product, EventType.PRODUCT_UPDATE, "maria");
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    private void keepIdAndCode(Product product, long id, Product productOld) {
        product.setId(id);
        product.setCode(productOld.getCode());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Product> delete(@PathVariable("id") long id) {
        final Product product = this.productRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        this.productRepository.deleteById(id);
        this.productPublisherUseCase.publishProductEvent(product, EventType.PRODUCT_DELETED, "josé");
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

}
