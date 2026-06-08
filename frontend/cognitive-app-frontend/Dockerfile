# Replaced with a multi-stage Dockerfile for building an Angular app and serving with nginx
# Stage 1: build the Angular app
FROM node:18-alpine AS build
WORKDIR /app

# Install dependencies
COPY package.json package-lock.json* ./
RUN npm ci

# Copy source and build
COPY . .
# Use the production configuration by default
RUN npm run build -- --configuration=local

# Stage 2: serve with nginx
FROM nginx:stable-alpine

# Copy built assets from the build stage
COPY --from=build /app/dist/cognitive-app-frontend /usr/share/nginx/html

# Replace default nginx config with our SPA-friendly config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
