package com.fashionstore.fashion_store_backend.service.impl;

import com.fashionstore.fashion_store_backend.dto.dashboard.*;
import com.fashionstore.fashion_store_backend.model.*;
import com.fashionstore.fashion_store_backend.repository.*;
import com.fashionstore.fashion_store_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public DashboardSummaryDto getDashboardSummary() {
        // Đếm tổng số đơn hàng
        long totalOrders = orderRepository.count();

        // Tính tổng doanh thu từ tất cả đơn hàng
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(Order::getTotal)
                .sum();

        // Đếm tổng số sản phẩm chưa xóa
        long totalProducts = productRepository.findAll().stream()
                .filter(product -> !product.isDeleted())
                .count();

        // Đếm tổng số khách hàng (user có role USER)
        long totalCustomers = userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && "USER".equals(user.getRole().getName()))
                .count();

        // Tính giá trị trung bình mỗi đơn hàng
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        // Đếm số đơn hàng đang chờ xử lý
        // Giả sử trạng thái "PENDING" có mã là "P"
        long pendingOrders = orderRepository.findAll().stream()
                .filter(order -> {
                    if (order.getOrderStatusDetails() == null || order.getOrderStatusDetails().isEmpty()) {
                        return false;
                    }

                    // Lấy trạng thái đang hoạt động
                    Optional<OrderStatusDetail> activeStatus = order.getOrderStatusDetails().stream()
                            .filter(OrderStatusDetail::isActive)
                            .findFirst();

                    return activeStatus.map(status -> "PENDING".equals(status.getOrderStatus().getCode()))
                            .orElse(false);
                })
                .count();

        return new DashboardSummaryDto(
                totalOrders,
                totalRevenue,
                totalProducts,
                totalCustomers,
                averageOrderValue,
                pendingOrders);
    }

    @Override
    public List<RevenueStatisticsDto> getRevenueStatistics(LocalDate startDate, LocalDate endDate, String period) {
        // Chuyển đổi từ LocalDate sang LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Lấy tất cả đơn hàng trong khoảng thời gian
        List<Order> orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);

        // Map để lưu trữ dữ liệu thống kê theo thời gian
        Map<LocalDate, RevenueStatisticsDto> revenueMap = new HashMap<>();

        // Xử lý dữ liệu dựa trên period
        for (Order order : orders) {
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            LocalDate periodKey = orderDate;

            // Điều chỉnh periodKey dựa trên period
            switch (period.toLowerCase()) {
                case "week":
                    // Lấy ngày đầu tuần
                    periodKey = orderDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                    break;
                case "month":
                    // Lấy ngày đầu tháng
                    periodKey = orderDate.withDayOfMonth(1);
                    break;
                case "year":
                    // Lấy ngày đầu năm
                    periodKey = orderDate.withDayOfYear(1);
                    break;
                default:
                    // Giữ nguyên ngày nếu period là "day"
                    break;
            }

            // Cập nhật hoặc tạo mới dữ liệu thống kê
            RevenueStatisticsDto stats = revenueMap.getOrDefault(periodKey,
                    new RevenueStatisticsDto(periodKey, period, 0, 0));

            stats.setRevenue(stats.getRevenue() + order.getTotal());
            stats.setOrderCount(stats.getOrderCount() + 1);

            revenueMap.put(periodKey, stats);
        }

        // Chuyển map thành list và sắp xếp theo ngày
        return revenueMap.values().stream()
                .sorted(Comparator.comparing(RevenueStatisticsDto::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderStatusStatisticsDto> getOrderStatusStatistics(LocalDate startDate, LocalDate endDate) {
        // Nếu startDate và endDate không được cung cấp, lấy tất cả đơn hàng
        List<Order> orders;
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);
        } else {
            orders = orderRepository.findAll();
        }

        // Map để đếm số lượng đơn hàng theo mã trạng thái
        Map<String, Long> statusCountMap = new HashMap<>();

        // Đếm số lượng đơn hàng theo trạng thái hoạt động
        for (Order order : orders) {
            if (order.getOrderStatusDetails() != null && !order.getOrderStatusDetails().isEmpty()) {
                Optional<OrderStatusDetail> activeStatus = order.getOrderStatusDetails().stream()
                        .filter(OrderStatusDetail::isActive)
                        .findFirst();

                if (activeStatus.isPresent()) {
                    String statusCode = activeStatus.get().getOrderStatus().getCode();
                    statusCountMap.put(statusCode, statusCountMap.getOrDefault(statusCode, 0L) + 1);
                }
            }
        }

        // Tổng số đơn hàng
        long totalOrders = orders.size();

        // Lấy tất cả trạng thái từ cơ sở dữ liệu
        List<OrderStatus> allStatuses = orderStatusRepository.findAll();

        // Tạo danh sách kết quả
        List<OrderStatusStatisticsDto> result = new ArrayList<>();
        for (OrderStatus status : allStatuses) {
            long count = statusCountMap.getOrDefault(status.getCode(), 0L);
            double percentage = totalOrders > 0 ? (double) count / totalOrders * 100 : 0;

            result.add(new OrderStatusStatisticsDto(
                    status.getCode(),
                    status.getStatusName(),
                    count,
                    percentage));
        }

        // Sắp xếp theo số lượng giảm dần
        return result.stream()
                .sorted(Comparator.comparing(OrderStatusStatisticsDto::getCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<TopSellingProductDto> getTopSellingProducts(int limit, LocalDate startDate, LocalDate endDate) {
        // Lấy tất cả chi tiết đơn hàng trong khoảng thời gian
        List<OrderDetail> orderDetails;
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            orderDetails = orderDetailRepository.findAll().stream()
                    .filter(detail -> detail.getOrder() != null &&
                            detail.getOrder().getOrderDate() != null &&
                            !detail.getOrder().getOrderDate().isBefore(startDateTime) &&
                            !detail.getOrder().getOrderDate().isAfter(endDateTime))
                    .collect(Collectors.toList());
        } else {
            orderDetails = orderDetailRepository.findAll();
        }

        // Map để lưu trữ thống kê theo sản phẩm
        Map<Long, TopSellingProductDto> productMap = new HashMap<>();

        // Tính tổng số lượng bán và doanh thu theo sản phẩm
        for (OrderDetail detail : orderDetails) {
            if (detail.getProduct() == null)
                continue;

            Long productId = detail.getProduct().getId();
            String productName = detail.getProduct().getName();
            double revenue = detail.getPrice() * detail.getQuantity();

            // Lấy URL ảnh đầu tiên của sản phẩm (nếu có)
            String imageUrl = "";
            if (detail.getProduct().getImages() != null && !detail.getProduct().getImages().isEmpty()) {
                imageUrl = detail.getProduct().getImages().get(0).getUrl();
            }

            // Lấy tồn kho hiện tại
            int currentStock = detail.getProduct().getQuantity();

            TopSellingProductDto productDto = productMap.getOrDefault(productId,
                    new TopSellingProductDto(productId, productName, 0, 0, imageUrl, currentStock));

            productDto.setTotalQuantitySold(productDto.getTotalQuantitySold() + detail.getQuantity());
            productDto.setTotalRevenue(productDto.getTotalRevenue() + revenue);

            productMap.put(productId, productDto);
        }

        // Chuyển map thành list, sắp xếp theo số lượng bán giảm dần và giới hạn số
        // lượng
        return productMap.values().stream()
                .sorted(Comparator.comparing(TopSellingProductDto::getTotalQuantitySold).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryStatisticsDto> getInventoryStatistics(int limit, String sortBy) {
        // Lấy tất cả sản phẩm không bị xóa
        List<Product> products = productRepository.findAll().stream()
                .filter(product -> !product.isDeleted())
                .collect(Collectors.toList());

        // Thiết lập ngưỡng tồn kho thấp
        int lowStockThreshold = 10;

        // Chuyển đổi thành DTO
        List<InventoryStatisticsDto> inventoryStats = products.stream()
                .map(product -> {
                    // Lấy URL ảnh đầu tiên của sản phẩm (nếu có)
                    String imageUrl = "";
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        imageUrl = product.getImages().get(0).getUrl();
                    }

                    boolean isLowStock = product.getQuantity() <= lowStockThreshold;

                    return new InventoryStatisticsDto(
                            product.getId(),
                            product.getName(),
                            product.getQuantity(),
                            imageUrl,
                            product.getPrice(),
                            lowStockThreshold,
                            isLowStock);
                })
                .collect(Collectors.toList());

        // Sắp xếp theo yêu cầu
        Comparator<InventoryStatisticsDto> comparator;
        if ("quantity_desc".equals(sortBy)) {
            comparator = Comparator.comparing(InventoryStatisticsDto::getQuantity).reversed();
        } else {
            // Mặc định là quantity_asc
            comparator = Comparator.comparing(InventoryStatisticsDto::getQuantity);
        }

        // Áp dụng sắp xếp và giới hạn số lượng
        return inventoryStats.stream()
                .sorted(comparator)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategorySalesDto> getSalesByCategory(LocalDate startDate, LocalDate endDate) {
        // Lấy tất cả đơn hàng trong khoảng thời gian
        List<Order> orders;
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);
        } else {
            orders = orderRepository.findAll();
        }

        // Map để lưu trữ thống kê theo danh mục
        Map<Long, CategorySalesDto> categoryMap = new HashMap<>();

        // Tổng doanh thu thực tế không phân chia
        double totalActualRevenue = 0;

        // Xử lý dữ liệu
        for (Order order : orders) {
            if (order.getOrderDetails() == null)
                continue;

            for (OrderDetail detail : order.getOrderDetails()) {
                if (detail.getProduct() == null || detail.getProduct().getCategories() == null
                        || detail.getProduct().getCategories().isEmpty())
                    continue;

                double revenue = detail.getPrice() * detail.getQuantity();
                totalActualRevenue += revenue;

                // Phân bổ doanh thu cho từng danh mục mà sản phẩm thuộc về
                int categoryCount = detail.getProduct().getCategories().size();
                double revenuePerCategory = revenue / categoryCount;

                for (Category category : detail.getProduct().getCategories()) {
                    Long categoryId = category.getId();
                    String categoryName = category.getName();

                    CategorySalesDto categoryDto = categoryMap.getOrDefault(categoryId,
                            new CategorySalesDto(categoryId, categoryName, 0, 0, 0, 0));

                    // Cộng phần doanh thu được phân bổ cho danh mục này
                    categoryDto.setTotalRevenue(categoryDto.getTotalRevenue() + revenuePerCategory);
                    categoryDto.setOrderCount(categoryDto.getOrderCount() + 1);
                    categoryDto.setItemCount(categoryDto.getItemCount() + detail.getQuantity());

                    categoryMap.put(categoryId, categoryDto);
                }
            }
        }

        // Tính phần trăm doanh thu
        if (totalActualRevenue > 0) {
            for (CategorySalesDto category : categoryMap.values()) {
                // Phần trăm dựa trên tổng doanh thu thực tế
                category.setPercentage((category.getTotalRevenue() / totalActualRevenue) * 100);
            }
        }

        // Chuyển map thành list và sắp xếp theo doanh thu giảm dần
        return categoryMap.values().stream()
                .sorted(Comparator.comparing(CategorySalesDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderTrendDto> getOrdersTrend(LocalDate startDate, LocalDate endDate, String period) {
        // Chuyển đổi từ LocalDate sang LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // Lấy tất cả đơn hàng trong khoảng thời gian
        List<Order> orders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);

        // Map để lưu trữ số lượng đơn hàng theo thời gian
        Map<LocalDate, Long> orderCountMap = new HashMap<>();

        // Tạo danh sách các ngày/tuần/tháng trong khoảng thời gian
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            LocalDate periodKey = currentDate;

            // Điều chỉnh periodKey dựa trên period
            switch (period.toLowerCase()) {
                case "week":
                    // Lấy ngày đầu tuần
                    periodKey = currentDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                    currentDate = currentDate.plusWeeks(1);
                    break;
                case "month":
                    // Lấy ngày đầu tháng
                    periodKey = currentDate.withDayOfMonth(1);
                    currentDate = currentDate.plusMonths(1);
                    break;
                default:
                    // Giữ nguyên ngày nếu period là "day"
                    currentDate = currentDate.plusDays(1);
                    break;
            }

            // Thêm vào danh sách nếu chưa có
            if (!dateList.contains(periodKey)) {
                dateList.add(periodKey);
                orderCountMap.put(periodKey, 0L);
            }
        }

        // Đếm số lượng đơn hàng theo thời gian
        for (Order order : orders) {
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            LocalDate periodKey = orderDate;

            // Điều chỉnh periodKey dựa trên period
            switch (period.toLowerCase()) {
                case "week":
                    // Lấy ngày đầu tuần
                    periodKey = orderDate.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                    break;
                case "month":
                    // Lấy ngày đầu tháng
                    periodKey = orderDate.withDayOfMonth(1);
                    break;
                default:
                    // Giữ nguyên ngày nếu period là "day"
                    break;
            }

            // Cập nhật số lượng
            orderCountMap.put(periodKey, orderCountMap.getOrDefault(periodKey, 0L) + 1);
        }

        // Tạo danh sách kết quả với tính toán tăng trưởng
        List<OrderTrendDto> result = new ArrayList<>();
        Long previousCount = null;

        // Sắp xếp dateList theo thứ tự tăng dần
        dateList.sort(Comparator.naturalOrder());

        for (LocalDate date : dateList) {
            long count = orderCountMap.get(date);
            double growth = 0;

            if (previousCount != null && previousCount > 0) {
                growth = ((double) (count - previousCount) / previousCount) * 100;
            }

            result.add(new OrderTrendDto(date, period, count, growth));
            previousCount = count;
        }

        return result;
    }
}