# AI Hub — Local Development with Docker Compose

Runs the AI Hub release-candidate Liferay image with PostgreSQL so you can iterate on the AI Hub modules without building the whole workspace. The portal runs continuously; you build and hot-deploy only the module you are changing.

## Start

```bash
docker compose up -d
docker compose logs -f liferay
```

The portal is ready when the log shows `Server startup in ... ms`. Open http://localhost:8080 and log in with `test@liferay.com` / `test`.

The first boot is slower because Liferay creates its schema in the empty PostgreSQL database.

## Deploy a Module You Changed

```bash
./deploy.sh ai-hub-impl
```

This builds only that module and drops its jar into `liferay/deploy/`, which Liferay hot-deploys. Because you never build the whole workspace, the modules that do not build standalone (the `*-rest-test` integration bundles and `ai-hub-cell-js-components-web`) never block you.

You can also pass a full Gradle path:

```bash
./deploy.sh :modules:dxp:apps:ai-hub:ai-hub-rest-impl
```

## Stop

```bash
docker compose down      # keeps the database
docker compose down -v   # also wipes the PostgreSQL volume
```

## Ports

| Port | Purpose |
| --- | --- |
| 8080 | HTTP |
| 8000 | JPDA remote debug |
| 11311 | Gogo shell (`telnet localhost 11311`) |

## Notes

- The image is set in `.env` (`LIFERAY_IMAGE`). Bump the tag there to change the runtime.
- The RC image ships a trial license, so no license file is needed. If Liferay ever reports a missing or expired license, drop a developer `.xml` license into `liferay/deploy/`.
- `liferay/files/portal-ext.properties` overlays onto the container's `LIFERAY_HOME` (`/opt/liferay`) at boot; that is where the PostgreSQL connection is configured.
- Anything placed under `liferay/scripts/` runs before Tomcat starts.
