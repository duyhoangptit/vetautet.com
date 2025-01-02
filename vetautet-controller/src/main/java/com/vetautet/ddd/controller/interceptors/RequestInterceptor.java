//package com.vetautet.ddd.controller.interceptors;
//
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.util.UUID;
//
//@Slf4j
//@Component
//public class RequestInterceptor implements HandlerInterceptor {
//
//    private static final String REQUEST_ID = "x-request-id";
//    private static final String COLLECTION_ID = "COLLECTION_ID";
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        // Lấy requestId hoặc tạo mới nếu không có
//        String requestId = request.getHeader(REQUEST_ID);
//        if (requestId == null || requestId.isEmpty()) {
//            requestId = getValueUUID();
//        }
//
//        // Thêm vào MDC để log
//        MDC.put(COLLECTION_ID, requestId);
//        log.info("Request ID: {}", requestId);
//
//        // Thêm vào response để truy vết
//        response.setHeader(REQUEST_ID, requestId);
//
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
//        // Xóa MDC sau khi xử lý xong
//        MDC.remove(COLLECTION_ID);
//    }
//
//    private String getValueUUID() {
//        UUID uuid = UUID.randomUUID();
//        return uuid.toString().replaceAll("-", "");
//    }
//}
