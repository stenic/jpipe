FROM node:alpine AS build

WORKDIR /app

RUN npx create-react-app demo-app \
	&& cd demo-app \
	&& yarn build \
	&& yarn test --watchAll=false

FROM nginxinc/nginx-unprivileged:1.18-alpine

COPY --from=build /app/demo-app/build /usr/share/nginx/html
