package com.example.lab05_j2ee.controller;

import com.example.lab05_j2ee.model.Product;
import com.example.lab05_j2ee.service.CategoryService;
import com.example.lab05_j2ee.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Hiển thị danh sách tất cả sản phẩm
     */
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product/list";
    }

    /**
     * Hiển thị form thêm sản phẩm mới
     */
    @GetMapping("/products/add")
    public String showAddForm(Model model) {
        prepareForm(model, new Product());
        return "product/add";
    }

    /**
     * Xử lý thêm sản phẩm mới
     */
    @PostMapping("/products/add")
    public String addProduct(@Valid Product product, BindingResult result,
                             @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            prepareForm(model, product);
            return "product/add";
        }

        try {
            productService.saveProduct(product, imageFile);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            model.addAttribute("uploadError", exception.getMessage());
            prepareForm(model, product);
            return "product/add";
        }

        return "redirect:/products";
    }

    /**
     * Hiển thị form chỉnh sửa sản phẩm
     */
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }
        prepareForm(model, product);
        return "product/edit";
    }

    /**
     * Xử lý cập nhật sản phẩm
     */
    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, @Valid Product product,
                              BindingResult result,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return "redirect:/products";
        }

        if (result.hasErrors()) {
            product.setId(id);
            product.setImage(existingProduct.getImage());
            product.setImageContentType(existingProduct.getImageContentType());
            prepareForm(model, product);
            return "product/edit";
        }

        try {
            productService.updateProduct(id, product, imageFile);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            product.setId(id);
            product.setImage(existingProduct.getImage());
            product.setImageContentType(existingProduct.getImageContentType());
            model.addAttribute("uploadError", exception.getMessage());
            prepareForm(model, product);
            return "product/edit";
        }

        return "redirect:/products";
    }

    /**
     * Trả dữ liệu hình ảnh của sản phẩm
     */
    @GetMapping("/products/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        if (product == null || product.getImageData() == null || product.getImageContentType() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getImageContentType()))
                .body(product.getImageData());
    }

    /**
     * Xóa sản phẩm theo ID
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }

    private void prepareForm(Model model, Product product) {
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
    }
}
