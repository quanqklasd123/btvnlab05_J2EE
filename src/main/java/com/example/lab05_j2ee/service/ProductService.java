package com.example.lab05_j2ee.service;

import com.example.lab05_j2ee.model.Product;
import com.example.lab05_j2ee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Lấy danh sách tất cả sản phẩm
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Lưu sản phẩm (thêm mới hoặc cập nhật)
     */
    public void saveProduct(Product product, MultipartFile imageFile) {
        applyImage(product, imageFile);
        productRepository.save(product);
    }

    /**
     * Cập nhật sản phẩm và giữ ảnh cũ nếu không upload ảnh mới
     */
    public void updateProduct(Long id, Product product, MultipartFile imageFile) {
        Product existingProduct = getProductById(id);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại");
        }

        product.setId(id);
        if (imageFile != null && !imageFile.isEmpty()) {
            applyImage(product, imageFile);
        } else {
            product.setImage(existingProduct.getImage());
            product.setImageData(existingProduct.getImageData());
            product.setImageContentType(existingProduct.getImageContentType());
        }

        productRepository.save(product);
    }

    /**
     * Tìm sản phẩm theo ID
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    /**
     * Xóa sản phẩm theo ID
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private void applyImage(Product product, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return;
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Vui lòng chọn tệp hình ảnh hợp lệ");
        }

        try {
            product.setImage(imageFile.getOriginalFilename());
            product.setImageContentType(contentType);
            product.setImageData(imageFile.getBytes());
        } catch (IOException exception) {
            throw new IllegalStateException("Không thể đọc tệp hình ảnh đã tải lên", exception);
        }
    }
}
