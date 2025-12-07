FROM node:20-alpine AS build
WORKDIR /app

# Vite reads VITE_* env vars at build time. These args are passed from docker-compose.
ARG VITE_API_BASE=/api
ARG VITE_GOOGLE_MAPS_API_KEY
ENV VITE_API_BASE=${VITE_API_BASE}
ENV VITE_GOOGLE_MAPS_API_KEY=${VITE_GOOGLE_MAPS_API_KEY}

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
