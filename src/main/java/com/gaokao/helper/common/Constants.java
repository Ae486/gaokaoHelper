package com.gaokao.helper.common;

/**
 * 系统常量类
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
public class Constants {

    /**
     * HTTP状态码
     */
    public static class HttpStatus {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;
    }

    /**
     * 响应消息
     */
    public static class Message {
        public static final String SUCCESS = "操作成功";
        public static final String FAILED = "操作失败";
        public static final String PARAM_ERROR = "参数错误";
        public static final String UNAUTHORIZED = "未授权访问";
        public static final String FORBIDDEN = "禁止访问";
        public static final String NOT_FOUND = "资源不存在";
        public static final String SYSTEM_ERROR = "系统异常";
    }

    /**
     * 缓存相关常量
     */
    public static class Cache {
        public static final String PROVINCE_CACHE = "province";
        public static final String SUBJECT_CATEGORY_CACHE = "subject_category";
        public static final String SCHOOL_CACHE = "school";
        public static final String RANKING_CACHE = "ranking";
        public static final String ADMISSION_SCORE_CACHE = "admission_score";
        
        // 缓存过期时间（秒）
        public static final int DEFAULT_TTL = 3600; // 1小时
        public static final int LONG_TTL = 86400; // 24小时
        public static final int SHORT_TTL = 300; // 5分钟
    }

    /**
     * JWT相关常量
     */
    public static class Jwt {
        public static final String TOKEN_HEADER = "Authorization";
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String USER_ID_CLAIM = "userId";
        public static final String USERNAME_CLAIM = "username";
    }

    /**
     * 分页相关常量
     */
    public static class Page {
        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_SIZE = 20;
        public static final int MAX_SIZE = 100;
    }

    /**
     * 预测相关常量
     */
    public static class Prediction {
        public static final String LEVEL_RUSH = "冲刺";
        public static final String LEVEL_STABLE = "稳妥";
        public static final String LEVEL_SAFE = "保底";
        public static final String LEVEL_DIFFICULT = "困难";
        
        // 预测概率阈值
        public static final double SAFE_THRESHOLD = 0.8;
        public static final double STABLE_THRESHOLD = 0.5;
        public static final double RUSH_THRESHOLD = 0.2;
    }

    /**
     * 学校相关常量
     */
    public static class School {
        // 办学层次
        public static final String LEVEL_UNDERGRADUATE = "本科";
        public static final String LEVEL_JUNIOR_COLLEGE = "专科";
        
        // 办学性质
        public static final String OWNERSHIP_PUBLIC = "公办";
        public static final String OWNERSHIP_PRIVATE = "民办";
        
        // 院校类型
        public static final String TYPE_COMPREHENSIVE = "综合";
        public static final String TYPE_SCIENCE_ENGINEERING = "理工";
        public static final String TYPE_NORMAL = "师范";
        public static final String TYPE_MEDICAL = "医药";
        public static final String TYPE_AGRICULTURAL = "农林";
        public static final String TYPE_FINANCIAL = "财经";
        
        // 院校标签
        public static final String TAG_985 = "985";
        public static final String TAG_211 = "211";
        public static final String TAG_DOUBLE_FIRST_CLASS = "双一流";
    }

    /**
     * 排名机构常量
     */
    public static class RankingProvider {
        public static final String SOFT_SCIENCE = "软科";
        public static final String QS = "QS";
        public static final String TIMES = "泰晤士";
        public static final String US_NEWS = "US News";
    }

    /**
     * 正则表达式常量
     */
    public static class Regex {
        public static final String USERNAME = "^[a-zA-Z0-9_]{4,20}$";
        public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,20}$";
        public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        public static final String PHONE = "^1[3-9]\\d{9}$";
    }
}
