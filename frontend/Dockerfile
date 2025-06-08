# 使用nginx作为基础镜像
FROM nginx:alpine

# 复制已编译好的web文件到nginx目录
COPY build/web /usr/share/nginx/html

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"] 