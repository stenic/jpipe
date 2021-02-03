FROM node:14-alpine

RUN apk add --no-cache \
	git \
	openssh-client

RUN npm install -g \
	semantic-release \
	@semantic-release/commit-analyzer \
	@semantic-release/release-notes-generator \
	@semantic-release/changelog \
	@semantic-release/git \
	@semantic-release/exec

WORKDIR /app

RUN mkdir -p /home/node/.ssh/ \
	&& ssh-keyscan -t rsa github.com >> /home/node/.ssh/known_hosts \
	&& ssh-keyscan -t rsa gitlab.com >> /home/node/.ssh/known_hosts \
	&& ssh-keyscan -t rsa bitbucket.org >> /home/node/.ssh/known_hosts

ENTRYPOINT [""]
