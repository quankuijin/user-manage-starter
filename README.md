# 用户管理系统 Starter

一个基于 Spring Boot + Vue3 的用户管理系统 Starter，提供完整的用户增删改查功能。

## 项目结构

```
user-manage-starter/
├── frontend/                    # Vue3 前端项目
│   ├── src/
│   │   ├── assets/             # 静态资源和样式
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具类
│   │   └── views/              # 页面组件
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── src/                        # Java 后端项目
│   └── main/
│       ├── java/com/example/usermanage/
│       │   ├── UserManageApplication.java    # 主应用类
│       │   ├── config/                        # 配置类
│       │   ├── controller/                    # 控制器
│       │   ├── dto/                           # 数据传输对象
│       │   ├── entity/                        # 实体类
│       │   └── service/                       # 服务类
│       └── resources/
│           ├── application.properties          # 配置文件
│           └── users.json                      # 用户数据文件（自动生成）
└── pom.xml
```

## 技术栈

### 后端
- Java 8+
- Spring Boot 2.7.18
- Maven
- JSON 文件存储（无需数据库）

### 前端
- Vue 3
- Element Plus
- Vite
- Axios
- Vue Router

## 功能特性

1. **用户管理**：增删改查用户信息
   - 姓名、性别、出生日期
   - 编码、地址、电话、邮箱
   - 备注信息

2. **登录认证**：
   - 默认账号：`admin` / `123`
   - 支持通过配置文件修改账号密码

3. **数据持久化**：
   - 数据存储在 `src/main/resources/users.json`
   - 无需数据库，开箱即用

4. **RESTful API**：
   - `POST /api/auth/login` - 登录
   - `POST /api/auth/logout` - 登出
   - `GET /api/users` - 获取用户列表
   - `GET /api/users/{id}` - 获取单个用户
   - `POST /api/users` - 新增用户
   - `PUT /api/users/{id}` - 更新用户
   - `DELETE /api/users/{id}` - 删除用户

## 配置说明

在 `src/main/resources/application.properties` 中可以配置：

```properties
# 服务端口
server.port=8080

# 登录账号密码
admin.username=admin
admin.password=123

# 数据文件路径
data.file.path=users.json
```

## 独立运行

### 方式一：分别启动前后端

**1. 启动后端**

```bash
cd user-manage-starter
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

**2. 启动前端**

```bash
cd user-manage-starter/frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

访问 `http://localhost:3000` 即可使用系统。

### 方式二：打包运行

**1. 编译安装到本地仓库**

```bash
cd user-manage-starter
mvn clean install
```

**2. 运行后端**

```bash
java -jar target/user-manage-starter-1.0.0.jar
```

**3. 构建前端**

```bash
cd frontend
npm run build
```

构建产物在 `frontend/dist` 目录，可以部署到静态服务器。

## 作为依赖被其他工程使用

### 1. 先安装到本地 Maven 仓库

```bash
cd user-manage-starter
mvn clean install
```

### 2. 在其他工程中引入依赖

在目标工程的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>user-manage-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3. 在主应用类中配置扫描

由于 starter 包含 `@SpringBootApplication` 主应用类，建议在使用时：

**方式一：在主应用类中排除自动配置**

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.usermanage", "com.yourpackage"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

**方式二：创建独立的测试/使用工程**

参考 `user-manage-starter-test` 的结构：

```xml
<!-- pom.xml -->
<project>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>user-manage-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</project>
```

```java
// 主应用类
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.usermanage"})
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
```

## UI 风格

采用京东风格设计：
- 主色调：红色 `#e1251b`
- Element Plus 组件库
- 简洁实用的管理后台界面

## 验证

1. 启动后端服务后，测试登录接口：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123"}'
```

2. 登录成功后，使用返回的 token 访问用户列表：

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: <your-token>"
```

## 注意事项

1. 数据文件 `users.json` 会在首次启动后自动创建
2. 默认账号密码可通过配置文件修改
3. 前端已配置代理，开发时无需处理跨域问题
4. 作为依赖使用时，注意包扫描路径的配置
