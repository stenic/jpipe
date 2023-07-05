# Changelog

All notable changes to this project will be documented in this file. See
[Conventional Commits](https://conventionalcommits.org) for commit guidelines.

## [1.3.5](https://github.com/stenic/jpipe/compare/v1.3.4...v1.3.5) (2023-07-05)


### Bug Fixes

* Add version to main build ([1861abc](https://github.com/stenic/jpipe/commit/1861abc9462d17099a56e54ef91de0472c734708))

## [1.3.4](https://github.com/stenic/jpipe/compare/v1.3.3...v1.3.4) (2023-06-21)


### Bug Fixes

* Handle bitbucket key rotation ([3caa92d](https://github.com/stenic/jpipe/commit/3caa92df6822a3066f672e6e050f6eea3d748219))

## [1.3.3](https://github.com/stenic/jpipe/compare/v1.3.2...v1.3.3) (2023-03-24)


### Bug Fixes

* github host-key entry https://github.blog/2023-03-23-we-updated-our-rsa-ssh-host-key/ ([b9a435b](https://github.com/stenic/jpipe/commit/b9a435bcdb4e42fe15f6d0f025e0b9bdf014536b))

## [1.3.2](https://github.com/stenic/jpipe/compare/v1.3.1...v1.3.2) (2023-03-24)


### Bug Fixes

* Format and rebuild image ([068d8ab](https://github.com/stenic/jpipe/commit/068d8ab0b96172ad87a0c0ac781270a43ec21c03))

## [1.3.1](https://github.com/stenic/jpipe/compare/v1.3.0...v1.3.1) (2023-03-24)


### Bug Fixes

* Format and rebuild image ([0bb9752](https://github.com/stenic/jpipe/commit/0bb9752a4ef14556b3fd87526ac7ffea702739ba))

# [1.3.0](https://github.com/stenic/jpipe/compare/v1.2.2...v1.3.0) (2023-03-16)


### Features

* **conventional-commits:** Make the version more semantic ([3d024b8](https://github.com/stenic/jpipe/commit/3d024b86ce1a62d4cf338b8eb08bd825fda890dc))

## [1.2.2](https://github.com/stenic/jpipe/compare/v1.2.1...v1.2.2) (2023-03-15)


### Bug Fixes

* Upgrade to node18 ([2f27018](https://github.com/stenic/jpipe/commit/2f27018873e4ecd408a94c3126df986b850caecb))

## [1.2.1](https://github.com/stenic/jpipe/compare/v1.2.0...v1.2.1) (2023-03-15)


### Bug Fixes

* Use github registry ([6cb4b1c](https://github.com/stenic/jpipe/commit/6cb4b1c4630b205f19725c3b832e885849dd344d))

# [1.2.0](https://github.com/stenic/jpipe/compare/v1.1.3...v1.2.0) (2023-03-15)


### Bug Fixes

* **release:** Fix build ([49c4e5c](https://github.com/stenic/jpipe/commit/49c4e5cc8f5402cfea9205ae7c818bc78575794f))
* Publish jpipe release image ([d003a72](https://github.com/stenic/jpipe/commit/d003a7291540e7430a3702eec39fb724111bde80))
* **cdInfra:** Allow empty commit ([ccf8993](https://github.com/stenic/jpipe/commit/ccf89932360ddf5eb7f7844cd541e869e1e3e292))
* **conventional-commit:** Add extraArgs ([7f647ab](https://github.com/stenic/jpipe/commit/7f647ab1f64dd66c87274f93021ae4ebe7038d5c))
* **docker:** Handle push correct ([6a8b46f](https://github.com/stenic/jpipe/commit/6a8b46fbf88feb802d18137db0de611770c92dcc))
* **plugin:** Allow yq image overwrite ([403565c](https://github.com/stenic/jpipe/commit/403565ca19523f66fad895ee56322dc58bd76f7d))
* **plugin:** Optional cleanup ([c22db82](https://github.com/stenic/jpipe/commit/c22db82bb4024e1eafa48d8f850bdadbd7d84a4c))
* **skip-ci:** Allow users to re-trigger the build ([e003227](https://github.com/stenic/jpipe/commit/e00322795389bd31925eb3fddc5a74b719b5d616))
* **sonarqube:** Cleanup workdir ([a029c8b](https://github.com/stenic/jpipe/commit/a029c8b333bb2d4e11a1f962e1ed283efeebbb1f))
* Docker tags break with uppercase branches ([08dafe1](https://github.com/stenic/jpipe/commit/08dafe1b6ffe04e46c3e0d9ecd3d1778a69faf2d))
* **skip:** Abort build ([6ea9592](https://github.com/stenic/jpipe/commit/6ea9592eb6d0f9ca3c4abd4f5dbb73f54fcdd4d5))
* **trivy:** Split reporting ([27e7df5](https://github.com/stenic/jpipe/commit/27e7df50403316b9b309b673ca30ff3be2719b9d))
* Strip weird chars from version ([e41b90f](https://github.com/stenic/jpipe/commit/e41b90f13bd74a56235f4116b4934ade2fb570eb))
* **trivy:** Report in tmp dir ([362a22d](https://github.com/stenic/jpipe/commit/362a22d33e4cc4985a367d94b3fc1f8bba986d23))
* Strip weird chars from version ([2bc708d](https://github.com/stenic/jpipe/commit/2bc708d2069e3a008b6c0137d86536a441d8cce7))
* **cdInfra:** Allow empty commit ([a48d32d](https://github.com/stenic/jpipe/commit/a48d32d58aa4d41a3ee4f75f62e40ef61e61d5ef))
* **docker:** Also push targets ([c173d30](https://github.com/stenic/jpipe/commit/c173d30678dc506a73da169c67f23845d22a1a3f))
* **docker:** Handle push correct ([2e156e2](https://github.com/stenic/jpipe/commit/2e156e2a8dcbfd3a3d3bd2ff7f31c5a09995eb3b))
* **sonarqube:** Handle allowFailure correct ([91519c7](https://github.com/stenic/jpipe/commit/91519c778b904be16684e6be1ebd59e490bf5835))
* **sonarqube:** Handle allowFailure correct ([2b15e8c](https://github.com/stenic/jpipe/commit/2b15e8cd5875204aac05a0f3cf0c30238dcd74f4))


### Features

* **argocd:** Add argoCDSync plugin ([12d7507](https://github.com/stenic/jpipe/commit/12d750702674d01ab2f86783487e342b80536845))
* **argocd:** Add argoCDSync plugin ([a9fdecb](https://github.com/stenic/jpipe/commit/a9fdecbb036b67d991a4da3c1714cdbad895681a))
* **argocd-sync-plugin:** Allow app sync options ([40e6b63](https://github.com/stenic/jpipe/commit/40e6b631513418c9565119412c0e623d285f79c8))
* **docker:** Add target and extra tags ([defaa0d](https://github.com/stenic/jpipe/commit/defaa0d7412f19ed708908f8dbf986c93d14bc02))
* **docker:** Enable docker cache fetching/pushing ([db2d9c3](https://github.com/stenic/jpipe/commit/db2d9c3387fd26c0b655946c6f1b1f749a734cbc))
* **docker:** Inject build version ([03627eb](https://github.com/stenic/jpipe/commit/03627eb2f66bbf2125247ea74919be9a9d9157fc))
* **docker:** Try to cleanup images ([79fb164](https://github.com/stenic/jpipe/commit/79fb164909ffba78fcf35dca7f16c864ea484c3f))
* **dockerPlugin:** Add buildkit support ([9ef1575](https://github.com/stenic/jpipe/commit/9ef1575bbd71625f3cc9b306f442628fe136afb9))
* **pipeline:** Don't show empty stages ([afa3a7b](https://github.com/stenic/jpipe/commit/afa3a7b0318cf98df6d4fcb7cdfa3e56adb62c68))
* **sonarqube:** Enhance sonarqube options ([5cd86cd](https://github.com/stenic/jpipe/commit/5cd86cdc905124d9f514a23a9cc72392b61c881a))
* **sonarqube:** Set project version ([010bb77](https://github.com/stenic/jpipe/commit/010bb7764efca09e2b87c555c6f3765e3ccee70a))
* **sonarqube:** Set project version ([cded4b7](https://github.com/stenic/jpipe/commit/cded4b7556532df90b9e92b89fb73b39dd6ee14c))
* **trigger:** Allow disable error propagation ([01b22da](https://github.com/stenic/jpipe/commit/01b22dac27b0682f23b607bcf985f86b98608a17))
* **trivy:** Add html report ([7a9304a](https://github.com/stenic/jpipe/commit/7a9304aee6f05e1021a9fd94a30acf005d8896f8))
* **trivy:** Add html report ([a662dcc](https://github.com/stenic/jpipe/commit/a662dccae807149cb3de83834d18abafcd7f33d5))
* Add EcrPlugin to ensure repo's exist ([c7b1ab3](https://github.com/stenic/jpipe/commit/c7b1ab32de159fd1366885a2233b5174fddb7d4f))
* Add SecretFinderPlugin ([3efce71](https://github.com/stenic/jpipe/commit/3efce715ebb45acdac04a857130a9623952c654b))
* Allow always running iac ([2c77e52](https://github.com/stenic/jpipe/commit/2c77e524a75bd515df834303bd955a84a455088f))
* **trigger:** Add triggerBuildPlugin ([e846243](https://github.com/stenic/jpipe/commit/e84624398faa80582dc7309f15ef1cf8fdbbf0e4))
* **trigger:** Allow passing custom params ([9ed5db1](https://github.com/stenic/jpipe/commit/9ed5db11c4d96ebe4f13889ac81bc27407d9a97c))
* **trivy:** Add TrivyPlugin ([b83b638](https://github.com/stenic/jpipe/commit/b83b638e8730924c02ade9425607fe252fce54bd))
* Allow always running iac ([a2dc698](https://github.com/stenic/jpipe/commit/a2dc698144b7ec10a68f8bbdca38604f3d7487a2))

# Changelog

## [1.1.3](https://github.com/stenic/jpipe/compare/v1.1.2...v1.1.3) (2021-02-06)


### Bug Fixes

* **skip-commit:** Set build result before deleting ([ced2dd6](https://github.com/stenic/jpipe/commit/ced2dd62fe07e45b138b966ccc3f41e8bea17215))

## [1.1.2](https://github.com/stenic/jpipe/compare/v1.1.1...v1.1.2) (2021-02-06)


### Bug Fixes

* **skip-commit:** Set build result before deleting ([8c9af46](https://github.com/stenic/jpipe/commit/8c9af46ec5cd18c740b5352e514774d3db694853))

## [1.1.1](https://github.com/stenic/jpipe/compare/v1.1.0...v1.1.1) (2021-02-06)


### Bug Fixes

* **security:** Upgrade dependencies ([bb48243](https://github.com/stenic/jpipe/commit/bb4824317288fe19c5965fd1e69949d1f47104b9))

# [1.1.0](https://github.com/stenic/jpipe/compare/v1.0.0...v1.1.0) (2021-02-06)


### Bug Fixes

* **ci:** Cleanup jenkinsfile ([4143328](https://github.com/stenic/jpipe/commit/414332847e892558f4a729c87f6e222e897ea849))
* **conventional-commit:** Fix host key issue ([add0798](https://github.com/stenic/jpipe/commit/add0798731e7162e563e726db9a26bc6229e7552))
* **docker:** No default for testScript ([bd8c6ea](https://github.com/stenic/jpipe/commit/bd8c6ea87d088940fb4509827cca8da73086105b))
* **docker:** Only push with credentials ([43a1bf1](https://github.com/stenic/jpipe/commit/43a1bf172b08628e0b4138e0be7c0cdd6e94cd56))


### Features

* **docs:** Add a README ([307c8a0](https://github.com/stenic/jpipe/commit/307c8a06d55bef577898e75c83d36ed4ee9733ac))

# 1.0.0 (2021-02-03)


### Features

* Init ([a236b76](https://github.com/stenic/jpipe/commit/a236b765f411f411f9a9edc6bc2be65d2c09e6dd))
