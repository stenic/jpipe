# Jpipe

> Jpipe aims to be a flexible, customizabe and reusable project containing both plugins and base pipelines.

## Usage

You can use Jpipe directly to see it in action. A good practice would be to create a new repository where you define 
your own company-wide pipelines. This allows you to tweak the setup at a later time for multiple project.

## Changes

The project uses semantic versioning to unsure a stable experience for the users. Make sure you define a specific tag
when including the library!

Additional details about the changes between releases can be found in [CHANGELOG.md](CHANGELOG.md).

## Setup

Jpipe can be configured with your project in multiple ways.

__Library__

```
library identifier: 'jpipe@main',
  retriever: modernSCM([$class: 'GitSCMSource', remote: 'https://github.com/stenic/jpipe.git']),
  changelog: false
```

__Global__

Jpipe can be set as a global shared library.

## License


