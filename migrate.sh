#!/bin/bash

# 创建工作空间和项目目录
mkdir -p workspace
cd workspace

# 创建后端项目结构
mkdir -p company-backend/src/main/java/com/wustl/company/{config,dto,entity,mapper,service/impl}
mkdir -p company-backend/src/main/resources

# 创建前端项目结构
mkdir -p company-frontend/src/{api,components,router,views}

# 移动后端文件
# Java 文件
cp ../src/main/java/com/wustl/company/config/DataSourceConfig.java company-backend/src/main/java/com/wustl/company/config/
cp ../src/main/java/com/wustl/company/dto/UserLoginDTO.java company-backend/src/main/java/com/wustl/company/dto/
cp ../src/main/java/com/wustl/company/dto/UserRegisterDTO.java company-backend/src/main/java/com/wustl/company/dto/
cp ../src/main/java/com/wustl/company/entity/User.java company-backend/src/main/java/com/wustl/company/entity/
cp ../src/main/java/com/wustl/company/mapper/UserMapper.java company-backend/src/main/java/com/wustl/company/mapper/
cp ../src/main/java/com/wustl/company/service/impl/UserServiceImpl.java company-backend/src/main/java/com/wustl/company/service/impl/

# 配置文件
cp ../src/main/resources/application.properties company-backend/src/main/resources/
cp ../pom.xml company-backend/

# 移动前端文件
cp ../src/App.vue company-frontend/src/
cp ../package.json company-frontend/

# 复制之前创建的前端文件
cp ../src/api/user.js company-frontend/src/api/
cp ../src/components/LoginForm.vue company-frontend/src/components/
cp ../src/components/RegisterForm.vue company-frontend/src/components/
cp ../src/router/index.js company-frontend/src/router/
cp ../src/views/HomeView.vue company-frontend/src/views/
cp ../src/views/LoginView.vue company-frontend/src/views/
cp ../src/views/RegisterView.vue company-frontend/src/views/
cp ../src/main.js company-frontend/src/
cp ../index.html company-frontend/
cp ../vite.config.js company-frontend/

echo "迁移完成！"
echo "后端代码已移动到: workspace/company-backend"
echo "前端代码已移动到: workspace/company-frontend" 