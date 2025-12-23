-- 创建数据库
CREATE DATABASE IF NOT EXISTS cloud_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cloud_manager;

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称: 上云单位/监管单位/运维单位',
    description VARCHAR(200) COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    organization VARCHAR(100) NOT NULL COMMENT '所属单位',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    status INT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 云资源类型表
CREATE TABLE IF NOT EXISTS resource_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '资源类型名称: CPU/内存/硬盘',
    unit VARCHAR(20) NOT NULL COMMENT '单位: 核/GB/GB',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='云资源类型表';

-- 云资源配置表
CREATE TABLE IF NOT EXISTS resource_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_type_id BIGINT NOT NULL COMMENT '资源类型ID',
    specification VARCHAR(100) NOT NULL COMMENT '规格',
    monthly_price DECIMAL(10,2) NOT NULL COMMENT '月单价',
    description VARCHAR(200) COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_type_id) REFERENCES resource_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='云资源配置表';

-- 云资源申请表
CREATE TABLE IF NOT EXISTS resource_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_number VARCHAR(50) NOT NULL UNIQUE COMMENT '申请单号',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    organization VARCHAR(100) NOT NULL COMMENT '申请单位',
    project_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    total_cpu INT NOT NULL COMMENT '总CPU核数',
    total_memory INT NOT NULL COMMENT '总内存(GB)',
    total_disk INT NOT NULL COMMENT '总硬盘(GB)',
    application_reason TEXT NOT NULL COMMENT '申请理由',
    status INT DEFAULT 0 COMMENT '状态: 0-待审批, 1-审批通过, 2-审批拒绝, 3-已发放, 4-已变更, 5-已释放',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    approved_time TIMESTAMP NULL COMMENT '审批时间',
    approved_by BIGINT NULL COMMENT '审批人ID',
    approved_remark TEXT COMMENT '审批意见',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (applicant_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='云资源申请表';

-- 审批记录表
CREATE TABLE IF NOT EXISTS approval_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL COMMENT '申请表ID',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    approval_status INT NOT NULL COMMENT '审批状态: 1-通过, 2-拒绝',
    approval_remark TEXT COMMENT '审批意见',
    approval_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES resource_application(id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 云资源实例表
CREATE TABLE IF NOT EXISTS resource_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL COMMENT '申请表ID',
    instance_name VARCHAR(100) NOT NULL COMMENT '实例名称',
    resource_type_id BIGINT NOT NULL COMMENT '资源类型ID',
    specification VARCHAR(100) NOT NULL COMMENT '规格',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    status INT DEFAULT 1 COMMENT '状态: 1-运行中, 2-已停止, 3-已释放',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    release_time TIMESTAMP NULL COMMENT '释放时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES resource_application(id) ON DELETE CASCADE,
    FOREIGN KEY (resource_type_id) REFERENCES resource_type(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='云资源实例表';

-- 费用核算表
CREATE TABLE IF NOT EXISTS cost_calculation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL COMMENT '申请表ID',
    year INT NOT NULL COMMENT '核算年份',
    total_days INT NOT NULL COMMENT '本年总天数',
    daily_unit_price DECIMAL(10,2) NOT NULL COMMENT '每日单价',
    total_cost DECIMAL(10,2) NOT NULL COMMENT '总费用',
    calculation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '核算时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES resource_application(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用核算表';

-- 初始化角色数据
INSERT INTO role (name, description) VALUES
('上云单位', '需要申请云资源的单位'),
('监管单位', '负责审批云资源申请的单位'),
('运维单位', '负责发放和管理云资源的单位');

-- 初始化资源类型数据
INSERT INTO resource_type (name, unit) VALUES
('CPU', '核'),
('内存', 'GB'),
('硬盘', 'GB');

-- 初始化资源配置数据
INSERT INTO resource_config (resource_type_id, specification, monthly_price, description) VALUES
(1, '通用型CPU', 100.00, '通用型CPU，适用于一般应用'),
(2, 'DDR4内存', 50.00, 'DDR4内存，高速稳定'),
(3, 'SSD硬盘', 20.00, 'SSD固态硬盘，读写速度快');

-- 初始化管理员用户 (密码: admin123)
INSERT INTO user (username, password, real_name, organization, email, phone, status) VALUES
('admin', '$2a$10$7T9jU5F0z6c0K1D9k8B7a9Z1X2C3V4B5N6M7L8K9J0H1G2F3E4D', '管理员', '系统管理', 'admin@example.com', '13800138000', 1);

-- 给管理员分配所有角色
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),
(1, 2),
(1, 3);
