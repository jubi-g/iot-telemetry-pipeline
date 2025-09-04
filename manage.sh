#!/usr/bin/env sh
set -e

# -----------------------------
# Config (edit once, reuse)
# -----------------------------
INFRA="kafka kafdrop iot-postgres prometheus grafana"
APPS="sensor-ingestion-service sensor-simulator aggregate-service api-service"
ALL="$INFRA $APPS"

API_URL="http://localhost:8099/itp/api/swagger-ui/index.html"
GRAFANA_URL="http://localhost:3000"
PROM_URL="http://localhost:9090/targets"
KAFDROP_URL="http://localhost:9000"

# -----------------------------
# Helpers
# -----------------------------
C_RESET="\033[0m"; C_BLUE="\033[34m"; C_GREEN="\033[32m"; C_YELLOW="\033[33m"; C_RED="\033[31m"

say()   { printf "$1%s${C_RESET}\n" "${2:-}"; }
ok()    { say "${C_GREEN}✅ %s" "$1"; }
info()  { say "${C_BLUE}ℹ️  %s" "$1"; }
warn()  { say "${C_YELLOW}⚠️  %s" "$1"; }
err()   { say "${C_RED}❌ %s" "$1"; }

need() {
  command -v "$1" >/dev/null 2>&1 || { err "Missing dependency: $1"; exit 1; }
}

usage() {
  cat <<EOF
Usage: $0 <action> [target] [--no-cache] [--pull] [--no-build]

Actions:
  run        Start containers (optionally build)
  stop       Stop containers
  down       Remove containers (and optionally volumes with 'down all')
  restart    Restart target (stop + run)
  logs       Tail logs for target (Ctrl-C to exit)
  ps         Show 'docker compose ps'

Targets:
  infra      $INFRA
  apps       $APPS
  ingest     sensor-ingestion-service only
  simulator  sensor-simulator only
  aggregate  aggregate-service only
  api        api-service only
  all        infra + apps (default)

Examples:
  $0 run infra --pull
  $0 run apps --no-cache
  $0 run api --no-build
  $0 logs simulator
  $0 down all
EOF
  exit 1
}

# Parse args
ACTION="$1"; TARGET="${2:-all}"; shift 2 || true
NO_CACHE=""; PULL=""; NO_BUILD=""
while [ $# -gt 0 ]; do
  case "$1" in
    --no-cache) NO_CACHE="--no-cache";;
    --pull)     PULL="--pull";;
    --no-build) NO_BUILD="1";;
    *) usage;;
  esac
  shift
done

need docker

# Compose wrapper
DC="docker compose"

# Detect if '--wait' is supported
if $DC up --help 2>/dev/null | grep -q -- '--wait'; then
  UP_WAIT="--wait"
else
  UP_WAIT=""
fi

# Resolve service list
services_for() {
  case "$1" in
    infra)     echo "$INFRA" ;;
    apps)      echo "$APPS" ;;
    ingest)    echo "sensor-ingestion-service" ;;
    simulator) echo "sensor-simulator" ;;
    aggregate) echo "aggregate-service" ;;
    api)       echo "api-service" ;;
    all)       echo "$ALL" ;;
    *)         err "Unknown target: $1"; usage ;;
  esac
}

# Build (optional)
do_build() {
  [ -n "$NO_BUILD" ] && { info "Skipping build (requested)"; return; }
  SVC="$(services_for "$1")"
  info "Building: $SVC $([ -n "$NO_CACHE" ] && printf '(no-cache)') $([ -n "$PULL" ] && printf '(pull)')"
  $DC build $PULL $NO_CACHE $SVC
}

# Up with wait or sleep fallback
do_up() {
  SVC="$(services_for "$1")"
  info "Starting: $SVC"
  if [ -n "$UP_WAIT" ]; then
    $DC up -d $UP_WAIT $SVC
  else
    $DC up -d $SVC
    info "Compose '--wait' not available; sleeping 15s for health..."
    sleep 15
  fi
}

do_rm() {
  SVC="$(services_for "$1")"
  info "Removing: $SVC"
  $DC rm -sf $SVC >/dev/null 2>&1 || true
}

do_stop() {
  SVC="$(services_for "$1")"
  info "Stopping: $SVC"
  $DC stop $SVC
}

do_logs() {
  SVC="$(services_for "$1")"
  info "Tailing logs: $SVC (Ctrl-C to exit)"
  $DC logs -f --tail=200 $SVC
}

do_ps() {
  $DC ps
}

print_urls() {
  cat <<EOF

Open:
- API (Swagger):   $API_URL
- Grafana:         $GRAFANA_URL  (admin / admin)
- Prometheus:      $PROM_URL
- Kafka UI:        $KAFDROP_URL

EOF
}

# -----------------------------
# Actions
# -----------------------------
case "$ACTION" in
  run)
    case "$TARGET" in
      api|ingest|simulator|aggregate)
        say "${C_BLUE}🚀 Starting $TARGET...${C_RESET}"
        do_rm "$TARGET"
        do_build "$TARGET"
        do_up "$TARGET"
        ;;
      infra)
        say "${C_BLUE}🚀 Starting infra...${C_RESET}"
        do_build infra
        do_up infra
        ;;
      apps)
        say "${C_BLUE}🚀 Starting apps...${C_RESET}"
        do_rm apps
        do_build apps
        do_up apps
        ;;
      all)
        say "${C_BLUE}🚀 Starting infra + apps...${C_RESET}"
        do_build infra
        do_up infra
        do_rm apps
        do_build apps
        do_up apps
        ;;
      *) usage ;;
    esac
    ok "Done."
    print_urls
    ;;
  restart)
    do_stop "$TARGET" || true
    $0 run "$TARGET" $([ -n "$NO_CACHE" ] && printf -- "--no-cache ") $([ -n "$PULL" ] && printf -- "--pull ")
    ;;
  stop)
    say "${C_BLUE}🛑 Stopping $TARGET...${C_RESET}"
    do_stop "$TARGET"
    ok "Stopped."
    ;;
  down)
    case "$TARGET" in
      infra|apps|api|ingest|simulator|aggregate)
        say "${C_BLUE}💣 Removing $TARGET containers...${C_RESET}"
        do_rm "$TARGET"
        ok "Removed."
        ;;
      all)
        say "${C_BLUE}💣 Removing ALL containers, networks, and volumes...${C_RESET}"
        $DC rm -sf iot-postgres >/dev/null 2>&1 || true
        $DC down -v --remove-orphans
        ok "Everything removed."
        ;;
      *) usage ;;
    esac
    ;;
  logs)
    do_logs "$TARGET"
    ;;
  ps)
    do_ps
    ;;
  *)
    usage
    ;;
esac
