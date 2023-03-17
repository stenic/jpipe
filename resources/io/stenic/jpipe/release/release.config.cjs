const branch = process.env.BRANCH_NAME || process.env.CI_COMMIT_BRANCH
const releaseBranches = process.env.RELEASE_BRANCHES || 'main'
const prereleaseBranches = process.env.PRERELEASE_BRANCHES || 'develop'

const config = {
  repositoryUrl: process.env.GIT_URL,
  branches: [].concat(
    releaseBranches.split(",").map(branchname => ({ name: branchname })),
    prereleaseBranches.split(",").map(branchname => ({ name: branchname, prerelease: true }))
  ),
  plugins: [
    '@semantic-release/commit-analyzer',
    '@semantic-release/release-notes-generator', 
    ['@semantic-release/exec', {
      verifyReleaseCmd: 'echo ${nextRelease.version} > VERSION'
    }]
  ]
}

if (config.branches.some(it => (it.name === branch && !it.prerelease))) {
  config.plugins.push(['@semantic-release/changelog', {
    changelogTitle: '# Changelog',
  }], ['@semantic-release/git', {
    assets: ['CHANGELOG.md'],
    message: 'chore(release): ${nextRelease.version} [skip ci]\n\n${nextRelease.notes}'
  }])
} else {
  config.plugins.push('@semantic-release/git')
}

module.exports = config