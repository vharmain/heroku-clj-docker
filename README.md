# Heroku CLJ Docker

Using [Docker builds with
heroku.yml](https://devcenter.heroku.com/articles/docker-builds-heroku-yml)
beta feature to deploy [Dockerized](https://www.docker.com/)
[Clojure](https://clojure.org/) app to [Heroku](https://heroku.com/).

## Motivation
Heroku has [great support for
Clojure](https://devcenter.heroku.com/categories/clojure-support)
using [lein](https://leiningen.org/) build tool but not all projects
want to be tied to lein or they require more flexibility with build
and deployment than Heroku standard tooling for Clojure provides. This
project is an example how to use Docker with [deps and
clj](https://clojure.org/guides/deps_and_cli) to build and run Clojure
applications in Heroku. Same strategy can be used with any other build
tool.

## Usage

### Prerequisites

* [docker](https://docs.docker.com/install/)
* [docker-compose](https://docs.docker.com/compose/install/)
* [Heroku account](https://signup.heroku.com/)
* [heroku-cli](https://devcenter.heroku.com/articles/heroku-cli)

### Setup

``` shell
heroku update beta
heroku plugins:install @heroku-cli/plugin-manifest
heroku login

git clone https://github.com/vharmain/heroku-clj-docker.git
cd heroku-clj-docker

heroku create insert-app-name-here --manifest
heroku git:remote -a heroku-clj-docker
```

### Deploy to Heroku

Commiting any change to `heroku` remote triggers deploy.

``` shell
git add -A
git commit -m "My first commit"
git push heroku master

# Watch logs
heroku logs --tail
```

### Run locally

``` shell
docker-compose up web
```

## Observations and notes

* One of the least painful Docker deployment experiences I've
  had. Heroku provides excellent ergonomics what comes to developer
  experience. Commands such as `heroku logs --tail` and `heroku run
  bash` work similarly to 'normal' buildpacks.
* I first tried running the app directly with `clj` (no
  uberjaring). During build time `.m2` folder resolves under $HOME/.m2
  but on container start clj/maven tries to lookup `.m2` relative to
  `WORKDIR` defined in Dockerfile. In practice this meant that all
  dependencies would be downloaded on each restart. Workaround to this
  is to either build uberjar on build time or place the app directly
  to users home folder and use it as the WORKDIR (see
  [Dockerfile-no-uberjar](Dockerfile-no-uberjar)) so Maven can find
  .m2. What is causing this? I'm not sure but I'm suspecting that some
  ENV var supplied by Heroku is getting Maven confused where $HOME is
  at runtime. I wasn't able to reproduce this locally with
  docker-compose.
* Heroku has [some
  limitations](https://devcenter.heroku.com/articles/container-registry-and-runtime#known-issues-and-limitations)
  with Docker.
