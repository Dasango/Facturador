package com.uce.emprendimiento.backend.service.impl;

import com.uce.emprendimiento.backend.entity.Product;
import com.uce.emprendimiento.backend.entity.User;
import com.uce.emprendimiento.backend.repository.ProductRepository;
import com.uce.emprendimiento.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUsuario_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductByIdAndUser(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!product.getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acceso denegado: No eres el dueño de este producto");
        }
        return product;
    }

    @Override
    @Transactional
    public Product createProduct(Product product, Long userId) {

        User userRef = new User();
        userRef.setId(userId);

        product.setUsuario(userRef);

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, Product productDetails, Long userId) {
        Product existingProduct = getProductByIdAndUser(productId, userId);

        existingProduct.setCodigoPrincipal(productDetails.getCodigoPrincipal());
        existingProduct.setCodigoAuxiliar(productDetails.getCodigoAuxiliar());
        existingProduct.setNombre(productDetails.getNombre());
        existingProduct.setValorUnitario(productDetails.getValorUnitario());
        existingProduct.setIva(productDetails.getIva());
        existingProduct.setIce(productDetails.getIce());

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, Long userId) {
        Product existingProduct = getProductByIdAndUser(productId, userId);

        productRepository.delete(existingProduct);
    }

}