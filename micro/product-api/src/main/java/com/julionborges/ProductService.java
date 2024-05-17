package com.julionborges;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    public List<ProductDTO> listAll() {
        return Product
                .<Product>listAll()
                .stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getQuantity()))
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) {
        Product product = Product.<Product>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }

    @Transactional
    public ProductDTO newProduct(ProductDTO newProduct) {
        Product product = new Product(newProduct.id(), newProduct.name(), newProduct.price(), newProduct.quantity());
        product.setId(null);
        product.persist();

        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO updateProduct) {
        Product product = Product.<Product>findByIdOptional(updateProduct.id())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        product.setName(updateProduct.name());
        product.setPrice(updateProduct.price());
        product.setQuantity(updateProduct.quantity());
        product.persist();

        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }

    @Transactional
    public Long deleteById(Long id) {
        Product product = Product.<Product>findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        product.delete();

        return product.getId();
    }
}
