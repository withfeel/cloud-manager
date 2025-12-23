# 政务云资源管理系统

## 项目概述
政务云资源管理系统是一个用于管理和核算政务云资源的Web应用程序，提供资源全生命周期管理，包括开通、变更、释放，以及费用核算功能。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **持久层**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **构建工具**: Maven

### 前端
- **框架**: Vue 3
- **UI组件库**: Element Plus
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **构建工具**: Vite

### 容器化
- **Docker**: 应用容器化
- **Docker Compose**: 多服务编排

## 项目结构

```
cloud-manager/
├── backend/                    # 后端项目
│   ├── src/main/java/com/cloudmanager/  # Java源代码
│   │   ├── entity/            # 实体类
│   │   ├── mapper/            # Mapper接口
│   │   ├── service/           # Service层
│   │   ├── controller/        # Controller层
│   │   └── CloudManagerApplication.java  # 主应用类
│   ├── src/main/resources/    # 资源文件
│   │   ├── application.yml    # 配置文件
│   │   └── sql/               # 数据库脚本
│   │       └── init.sql       # 初始化脚本
│   ├── pom.xml                # Maven依赖
│   └── Dockerfile             # 后端Dockerfile
├── frontend/                   # 前端项目
│   ├── src/                   # 前端源代码
│   │   ├── views/             # 页面组件
│   │   ├── router/            # 路由配置
│   │   ├── main.js            # 入口文件
│   │   └── App.vue            # 根组件
│   ├── index.html             # HTML模板
│   ├── vite.config.js         # Vite配置
│   ├── package.json           # npm依赖
│   ├── Dockerfile             # 前端Dockerfile
│   └── nginx.conf             # Nginx配置
├── docker-compose.yml         # Docker Compose配置
├── .gitignore                 # Git忽略文件
└── README.md                  # 项目说明文档
```

## 功能特性

### 1. 云资源全生命周期管理
- **开通**: 上云单位提交资源申请，监管单位审批，运维单位发放
- **变更**: 修改已分配的资源配置（CPU、内存、硬盘）
- **释放**: 释放不再需要的资源

### 2. 多角色协作
- **上云单位**: 提出云资源申请
- **监管单位**: 审批云资源申请
- **云运维单位**: 发放和执行云资源

### 3. 资源类型
- CPU（核）
- 内存（GB）
- 硬盘（GB）

### 4. 费用核算功能
- 按项目月单价和本年自然天数计算每日单价
- 支持不同年份的费用核算
- 提供详细的核算结果展示

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker (可选，用于容器化部署)

### 安装与部署

#### 1. 传统部署方式

**后端部署**
```bash
cd backend
mvn clean package -DskipTests
java -jar target/cloud-manager-backend-1.0.0.jar
```

**前端部署**
```bash
cd frontend
npm install
npm run build
# 将dist目录部署到Nginx或其他Web服务器
```

#### 2. Docker Compose部署（推荐）
```bash
docker-compose up -d
```

### 访问方式
- **前端**: http://localhost
- **后端API**: http://localhost:8080/api
- **数据库**: localhost:3306 (用户名: root, 密码: 123456)
- **Swagger文档**: http://localhost:8080/api/swagger-ui.html

## 数据库设计

### 主要表结构
- `user`: 用户信息表
- `role`: 角色表
- `user_role`: 用户角色关联表
- `resource_type`: 云资源类型表
- `resource_config`: 云资源配置表
- `resource_application`: 云资源申请表
- `approval_record`: 审批记录表
- `resource_instance`: 云资源实例表
- `cost_calculation`: 费用核算表

详细数据库结构请查看 `backend/src/main/resources/sql/init.sql` 文件。

## API接口

### 资源申请接口
- `POST /api/resource/application/submit`: 提交资源申请
- `GET /api/resource/application/list`: 获取申请列表
- `POST /api/resource/application/approve`: 审批资源申请
- `POST /api/resource/application/allocate`: 发放资源
- `POST /api/resource/application/update`: 变更资源
- `POST /api/resource/application/release`: 释放资源

### 费用核算接口
- `POST /api/cost/calculate`: 核算年用云费用
- `GET /api/cost/days/{year}`: 获取某一年的自然天数

## 使用指南

### 1. 登录系统
使用默认账号登录：
- 用户名: admin
- 密码: admin123

### 2. 资源申请流程
1. **提交申请**: 上云单位用户登录后，进入"资源申请"页面，填写申请信息并提交
2. **审批申请**: 监管单位用户登录后，进入"审批管理"页面，审批资源申请
3. **发放资源**: 运维单位用户登录后，进入"资源分配"页面，发放已审批通过的资源

### 3. 费用核算流程
1. 进入"费用核算"页面
2. 选择申请单号和核算年份
3. 点击"开始核算"按钮
4. 查看核算结果

## Docker Compose配置

Docker Compose配置了三个服务：
1. **mysql**: MySQL数据库服务
2. **backend**: Spring Boot后端服务
3. **frontend**: Vue前端服务

### 环境变量

**MySQL**:
- `MYSQL_ROOT_PASSWORD`: 数据库密码
- `MYSQL_DATABASE`: 数据库名称

**后端**:
- `SPRING_DATASOURCE_URL`: 数据库连接URL
- `SPRING_DATASOURCE_USERNAME`: 数据库用户名
- `SPRING_DATASOURCE_PASSWORD`: 数据库密码

## 开发指南

### 后端开发
1. 安装JDK 17+
2. 安装Maven
3. 导入项目到IDE
4. 运行`CloudManagerApplication.java`

### 前端开发
1. 安装Node.js 18+
2. 安装依赖: `npm install`
3. 启动开发服务器: `npm run dev`

## 贡献指南

1. Fork本仓库
2. 创建特性分支: `git checkout -b feature/new-feature`
3. 提交更改: `git commit -m 'Add new feature'`
4. 推送分支: `git push origin feature/new-feature`
5. 提交Pull Request

## 许可证

本项目采用MIT许可证，详情请查看LICENSE文件。

## 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱: your-email@example.com
- 项目地址: https://github.com/your-username/cloud-manager
