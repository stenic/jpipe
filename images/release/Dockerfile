FROM node:18-alpine

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

ADD "https://www.random.org/cgi-bin/randbyte?nbytes=10&format=h" skipcache

RUN mkdir -p /home/node/.ssh/ \
	&& ssh-keyscan -t rsa gitlab.com >> /home/node/.ssh/known_hosts \
	&& ssh-keyscan -t rsa bitbucket.org >> /home/node/.ssh/known_hosts \
	&& ssh-keyscan -t rsa github.com >> /home/node/.ssh/known_hosts \
	&& apk add --no-cache --virtual tooling jq curl \
	&& curl -L https://api.github.com/meta | jq -r '.ssh_keys | .[]' | sed -e 's/^/github.com /' >> /home/node/.ssh/known_hosts \
	&& apk del tooling

ENTRYPOINT [""]
