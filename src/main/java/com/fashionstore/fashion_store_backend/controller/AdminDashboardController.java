package com.fashionstore.fashion_store_backend.controller;

import com.fashionstore.fashion_store_backend.dto.dashboard.*;
import com.fashionstore.fashion_store_backend.response.ApiResponse;
import com.fashionstore.fashion_store_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * API lấy tổng quan các thống kê (số đơn hàng, doanh thu, số lượng sản phẩm, số
     * khách hàng)
     * 
     * @return Tổng quan thống kê
     */
    @GetMapping("/statistics/summary")
    public ResponseEntity<ApiResponse> getDashboardSummary() {
        try {
            DashboardSummaryDto summary = dashboardService.getDashboardSummary();
            return ResponseEntity.ok(new ApiResponse("Thống kê tổng quan dashboard", true, summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * API lấy thống kê doanh thu theo khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @param period    Khoảng thời gian (day, week, month, year), mặc định là day
     * @return Danh sách doanh thu theo khoảng thời gian
     */
    @GetMapping("/sales/revenue")
    public ResponseEntity<ApiResponse> getRevenueStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "day") String period) {
        try {
            List<RevenueStatisticsDto> statistics = dashboardService.getRevenueStatistics(startDate, endDate, period);
            return ResponseEntity.ok(new ApiResponse("Thống kê doanh thu", true, statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * API lấy thống kê đơn hàng theo trạng thái
     * 
     * @param startDate Ngày bắt đầu (tùy chọn)
     * @param endDate   Ngày kết thúc (tùy chọn)
     * @return Danh sách số lượng đơn hàng theo từng trạng thái
     */
    @GetMapping("/orders/by-status")
    public ResponseEntity<ApiResponse> getOrdersByStatus(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<OrderStatusStatisticsDto> statistics = dashboardService.getOrderStatusStatistics(startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("Thống kê đơn hàng theo trạng thái", true, statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * API lấy thống kê sản phẩm bán chạy nhất
     * 
     * @param limit     Số lượng sản phẩm trả về, mặc định là 10
     * @param startDate Ngày bắt đầu (tùy chọn)
     * @param endDate   Ngày kết thúc (tùy chọn)
     * @return Danh sách sản phẩm bán chạy nhất
     */
    @GetMapping("/sales/top-products")
    public ResponseEntity<ApiResponse> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<TopSellingProductDto> topProducts = dashboardService.getTopSellingProducts(limit, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("Top sản phẩm bán chạy", true, topProducts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * API lấy thống kê hàng tồn kho
     * 
     * @param limit  Số lượng sản phẩm trả về, mặc định là 10
     * @param sortBy Sắp xếp theo (quantity_asc, quantity_desc), mặc định là
     *               quantity_asc
     * @return Danh sách sản phẩm tồn kho
     */
    @GetMapping("/inventory/stock")
    public ResponseEntity<ApiResponse> getInventoryStatistics(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "quantity_asc") String sortBy) {
        try {
            List<InventoryStatisticsDto> inventoryStats = dashboardService.getInventoryStatistics(limit, sortBy);
            return ResponseEntity.ok(new ApiResponse("Thống kê hàng tồn kho", true, inventoryStats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * API lấy thống kê doanh thu theo danh mục sản phẩm
     * 
     * @param startDate Ngày bắt đầu (tùy chọn)
     * @param endDate   Ngày kết thúc (tùy chọn)
     * @return Danh sách doanh thu theo danh mục
     */
    @GetMapping("/sales/by-category")
    public ResponseEntity<ApiResponse> getSalesByCategory(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<CategorySalesDto> categorySales = dashboardService.getSalesByCategory(startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("Doanh thu theo danh mục", true, categorySales));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * API lấy số lượng đơn hàng mới theo thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @param period    Khoảng thời gian (day, week, month), mặc định là day
     * @return Danh sách số lượng đơn hàng theo thời gian
     */
    @GetMapping("/orders/trend")
    public ResponseEntity<ApiResponse> getOrdersTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "day") String period) {
        try {
            List<OrderTrendDto> orderTrends = dashboardService.getOrdersTrend(startDate, endDate, period);
            return ResponseEntity.ok(new ApiResponse("Xu hướng đơn hàng", true, orderTrends));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }
}