package com.fashionstore.fashion_store_backend.service;

import com.fashionstore.fashion_store_backend.dto.dashboard.*;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    /**
     * Lấy tổng quan thống kê cho dashboard
     * 
     * @return DashboardSummaryDto
     */
    DashboardSummaryDto getDashboardSummary();

    /**
     * Lấy thống kê doanh thu theo khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @param period    Khoảng thời gian (day, week, month, year)
     * @return List<RevenueStatisticsDto>
     */
    List<RevenueStatisticsDto> getRevenueStatistics(LocalDate startDate, LocalDate endDate, String period);

    /**
     * Lấy thống kê đơn hàng theo trạng thái
     * 
     * @param startDate Ngày bắt đầu (có thể null)
     * @param endDate   Ngày kết thúc (có thể null)
     * @return List<OrderStatusStatisticsDto>
     */
    List<OrderStatusStatisticsDto> getOrderStatusStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Lấy danh sách sản phẩm bán chạy nhất
     * 
     * @param limit     Số lượng sản phẩm trả về
     * @param startDate Ngày bắt đầu (có thể null)
     * @param endDate   Ngày kết thúc (có thể null)
     * @return List<TopSellingProductDto>
     */
    List<TopSellingProductDto> getTopSellingProducts(int limit, LocalDate startDate, LocalDate endDate);

    /**
     * Lấy thống kê hàng tồn kho
     * 
     * @param limit  Số lượng sản phẩm trả về
     * @param sortBy Cách sắp xếp (quantity_asc, quantity_desc)
     * @return List<InventoryStatisticsDto>
     */
    List<InventoryStatisticsDto> getInventoryStatistics(int limit, String sortBy);

    /**
     * Lấy thống kê doanh thu theo danh mục
     * 
     * @param startDate Ngày bắt đầu (có thể null)
     * @param endDate   Ngày kết thúc (có thể null)
     * @return List<CategorySalesDto>
     */
    List<CategorySalesDto> getSalesByCategory(LocalDate startDate, LocalDate endDate);

    /**
     * Lấy số lượng đơn hàng mới theo thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @param period    Khoảng thời gian (day, week, month)
     * @return List<OrderTrendDto>
     */
    List<OrderTrendDto> getOrdersTrend(LocalDate startDate, LocalDate endDate, String period);
}