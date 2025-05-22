package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.*;
import com.fashionstore.fashion_store_backend.model.*;
import com.fashionstore.fashion_store_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;;

    // Tìm sản phẩm theo ID
    public Product findById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }

    // Hàm tạo mới sản phẩm
    public Long createProduct(ProductCreateDto productDTO) throws Exception {
        // Tạo sản phẩm mới
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSalePrice(productDTO.getSalePrice() > 0 ? productDTO.getSalePrice() : 0);
        product.setCreatedAt(LocalDateTime.now());
        product.setDeleted(false);

        // Lấy danh sách Category từ slug
        List<Category> categories = categoryRepository.findBySlugIn(productDTO.getCategorySlugs());
        if (categories.isEmpty()) {
            throw new Exception("Danh mục không tồn tại");
        }
        product.setCategories(categories);

        // Lấy danh sách Color từ tên màu
        List<Color> colors = colorRepository.findByNameIn(productDTO.getColorNames());
        product.setColors(colors);

        // Lấy danh sách Size từ tên kích thước
        List<Size> sizes = sizeRepository.findByNameIn(productDTO.getSizeNames());
        product.setSizes(sizes);

        // Lưu sản phẩm vào cơ sở dữ liệu
        return productRepository.save(product).getId();
    }

    @Transactional
    public void softDeleteProduct(Long productId) {
        // Lấy sản phẩm theo ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        // Cập nhật số lượng của sản phẩm về 0
        product.setQuantity(0);

        // Cập nhật số lượng của tất cả các biến thể của sản phẩm về 0
        for (ProductVariant variant : product.getVariants()) {
            variant.setQuantity(0);
            productVariantRepository.save(variant); // Lưu lại biến thể sau khi thay đổi số lượng
        }

        // Đánh dấu sản phẩm là đã xóa
        product.setDeleted(true);

        // Lưu sản phẩm sau khi thay đổi
        productRepository.save(product);
    }

    @Transactional
    public void softDeleteManyProducts(List<Long> productIds) {
        // Lấy tất cả sản phẩm theo danh sách IDs
        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            // Cập nhật số lượng của sản phẩm về 0
            product.setQuantity(0);

            // Cập nhật số lượng của tất cả các biến thể của sản phẩm về 0
            for (ProductVariant variant : product.getVariants()) {
                variant.setQuantity(0);
                productVariantRepository.save(variant);
            }

            // Đánh dấu sản phẩm là đã xóa
            product.setDeleted(true);

            // Lưu sản phẩm sau khi thay đổi
            productRepository.save(product);
        }
    }

    public ProductResponseDto getProductById(Long productId) throws Exception {
        // Lấy sản phẩm từ cơ sở dữ liệu
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        // Xây dựng danh sách tên các category slug
        List<String> categorySlugs = product.getCategories().stream()
                .map(category -> category.getSlug())
                .collect(Collectors.toList());

        // Xây dựng danh sách tên các màu sắc
        List<String> colorNames = product.getColors().stream()
                .map(color -> color.getName())
                .collect(Collectors.toList());

        // Xây dựng danh sách tên các size
        List<String> sizeNames = product.getSizes().stream()
                .map(size -> size.getName())
                .collect(Collectors.toList());

        // Lấy danh sách các variant của sản phẩm
        // Lấy danh sách các variant của sản phẩm
        List<ProductVariantResponseDto> variants = product.getVariants().stream()
                .map(variant -> {
                    // Kiểm tra nếu size và color không null thì lấy tên, nếu null thì gán "Chưa có
                    // size" hoặc "Chưa có màu"
                    String sizeName = (variant.getSize() != null) ? variant.getSize().getName() : null;
                    String colorName = (variant.getColor() != null) ? variant.getColor().getName() : null;

                    return new ProductVariantResponseDto(
                            colorName, // Nếu color là null, sẽ trả về "Chưa có màu"
                            sizeName, // Nếu size là null, sẽ trả về "Chưa có size"
                            variant.getQuantity());
                })
                .collect(Collectors.toList());

        // Trả về thông tin sản phẩm dưới dạng DTO
        return new ProductResponseDto(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getSalePrice() > 0 ? product.getSalePrice() : 0, // nếu giá khuyến mãi không có thì = 0
                categorySlugs,
                colorNames,
                sizeNames,
                variants);
    }

    public Long updateProduct(Long productId, ProductCreateDto productDTO) throws Exception {
        // Tìm sản phẩm theo ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Sản phẩm không tồn tại"));

        // Cập nhật các trường thông tin của sản phẩm
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setSalePrice(productDTO.getSalePrice() > 0 ? productDTO.getSalePrice() : 0);
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false); // Đảm bảo trạng thái không bị xóa

        // Xóa danh mục cũ và cập nhật danh mục mới
        product.setCategories(new ArrayList<>());
        List<Category> categories = categoryRepository.findBySlugIn(productDTO.getCategorySlugs());
        if (categories.isEmpty()) {
            throw new Exception("Danh mục không tồn tại");
        }
        product.setCategories(categories);

        // Xóa màu sắc cũ và cập nhật màu sắc mới
        product.setColors(new ArrayList<>());
        List<Color> colors = colorRepository.findByNameIn(productDTO.getColorNames());
        product.setColors(colors);

        // Xóa kích thước cũ và cập nhật kích thước mới
        product.setSizes(new ArrayList<>());
        List<Size> sizes = sizeRepository.findByNameIn(productDTO.getSizeNames());
        product.setSizes(sizes);

        // Lưu lại sản phẩm đã được cập nhật
        return productRepository.save(product).getId();
    }

    public Page<ProductFilteredResponseDto> getFilteredProducts(
            List<String> categorySlugs,
            List<String> sizeNames,
            List<String> colorNames,
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sortBy) {

        // Tính số lượng category, size, color cần thiết
        int categorySlugsCount = (categorySlugs != null) ? categorySlugs.size() : 0;
        int sizeNamesCount = (sizeNames != null) ? sizeNames.size() : 0;
        int colorNamesCount = (colorNames != null) ? colorNames.size() : 0;

        // Chọn kiểu sắp xếp
        Sort sort = getSortByCriteria(sortBy);

        // Sử dụng PageRequest với Sort
        Pageable pageRequest = PageRequest.of(page, size, sort);

        // Lọc theo các điều kiện
        Page<Product> productsPage = productRepository.findFilteredProducts(
                categorySlugs,
                sizeNames,
                colorNames,
                minPrice,
                maxPrice,
                pageRequest,
                categorySlugsCount,
                colorNamesCount,
                sizeNamesCount);

        // Kiểm tra nếu danh sách sản phẩm trống
        if (productsPage.isEmpty()) {
            // Trả về một Page rỗng nếu không tìm thấy sản phẩm
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }

        // Chuyển đổi từ Product entity sang ProductFilteredResponseDto
        List<ProductFilteredResponseDto> productDtos = productsPage.stream()
                .map(product -> new ProductFilteredResponseDto(product))
                .collect(Collectors.toList());

        // Trả về một Page với kết quả phân trang
        return new PageImpl<>(productDtos, pageRequest, productsPage.getTotalElements());
    }

    private Sort getSortByCriteria(String sortBy) {
        // Nếu sortBy null, trả về mặc định: không sắp xếp
        if (sortBy == null) {
            return Sort.unsorted();
        }

        // Dùng Map để quản lý các kiểu sắp xếp
        Map<String, Sort> sortMap = new HashMap<>();
        sortMap.put("priceAsc", Sort.by(
                Sort.Order.asc("salePrice").nullsLast(),
                Sort.Order.asc("price")));
        sortMap.put("priceDesc", Sort.by(
                Sort.Order.desc("salePrice").nullsLast(),
                Sort.Order.desc("price")));
        sortMap.put("newest", Sort.by(
                Sort.Order.desc("updatedAt"),
                Sort.Order.desc("createdAt")));

        // Nếu sortBy có trong Map, trả về giá trị tương ứng, nếu không trả về mặc định
        return sortMap.getOrDefault(sortBy, Sort.unsorted());
    }

    public Page<ProductFilteredRspDto> getProductList(
            int page, int limit, String categorySlug, String title, String status) {
        // Tạo Pageable object để phân trang
        Pageable pageable = PageRequest.of(page, limit);

        // Xây dựng Specification để lọc động
        Specification<Product> spec = Specification.where(null);

        // Lọc theo category slug
        if (categorySlug != null && !categorySlug.isEmpty()) {
            Category category = categoryRepository.findBySlug(categorySlug)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
            spec = spec.and((root, query, cb) -> cb.isMember(category, root.get("categories")));
        }

        // Lọc theo title (tên sản phẩm)
        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + title.toLowerCase() + "%"));
        }

        // Lọc theo status (còn hàng, hết hàng, đã xóa)
        if (status != null && !status.isEmpty()) {
            if (status.equalsIgnoreCase("con_hang")) {
                spec = spec.and((root, query, cb) -> cb.greaterThan(root.get("quantity"), 0));
                spec = spec.and((root, query, cb) -> cb.isFalse(root.get("deleted")));
            } else if (status.equalsIgnoreCase("het_hang")) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("quantity"), 0));
                spec = spec.and((root, query, cb) -> cb.isFalse(root.get("deleted")));
            } else if (status.equalsIgnoreCase("da_xoa")) {
                spec = spec.and((root, query, cb) -> cb.isTrue(root.get("deleted")));
            } else {
                throw new IllegalArgumentException(
                        "Giá trị status không hợp lệ. Chỉ chấp nhận 'con_hang', 'het_hang' hoặc 'da_xoa'.");
            }
        } else {
            // Chỉ lấy các sản phẩm chưa bị xóa nếu không truyền status
            spec = spec.and((root, query, cb) -> cb.isFalse(root.get("deleted")));
        }

        // Thực hiện truy vấn với Specification và Pageable
        Page<Product> productsPage = productRepository.findAll(spec, pageable);

        // Chuyển đổi từ Product entity sang ProductFilteredRspDto
        List<ProductFilteredRspDto> productDtos = productsPage.stream()
                .map(ProductFilteredRspDto::new)
                .collect(Collectors.toList());

        // Trả về Page với kết quả phân trang
        return new PageImpl<>(productDtos, pageable, productsPage.getTotalElements());
    }

}
