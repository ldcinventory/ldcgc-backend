n=0
#Open Docker, only if is not running
if (! docker ps ); then
  read -rp 'Docker is not started, do you want for this script to try to start it? [Y/n] ' start_docker
  start_docker={$start_docker:-yes}

  if [[ "$start_docker" =~ [yY].* ]]; then
    # On Mac OS this would be the terminal command to launch Docker
    open -a Docker
  else
    echo "Please, run Docker, you have 30 seconds... ;)"
  fi

  # Wait until Docker daemon is running and has completed initialisation
  while [ "$n" -le 10 ]; do
    # Docker takes a few seconds to initialize
    echo "Waiting 3s for Docker to launch... (if not launched automatically, do it manually)"
    sleep 3
    if(docker stats --no-stream ); then
      break
    fi
    n=$(( n+1 ))
  done
  if [ "$n" -eq 10 ]; then
    echo "Please run Docker first"
    exit 1
  fi
fi

if [ -z "$1" ]; then
  echo ""
  echo "-- No option selected --"
  echo "→ To run (no API+no UI) → use: $0 {create|restart}"
  echo "→ To run (just API) ----→ use: $0 {create-api|create-test-data-api|restart-api}"
  echo "→ To run (API + UI) ----→ use: $0 {create-ui|create-test-data-ui|restart-ui}"
  echo "→ To purge everything --→ use: $0 purge"
  exit 1
elif [[ "$1" == *-ui ]] && [ ! -f ../ldcgc-frontend/Dockerfile ] || [ ! -f ../ldcgc-frontend-elm/Dockerfile ] ; then
  echo "Dockerfile for UI not found!"
  exit 1
fi

if [ ! -f target ]; then
  rm -rf target
else
  echo "it exists"
fi

git pull
docker compose down -v
docker compose rm -f
docker rm ldcgc-backend -f
docker rmi ldcgc-backend -f

DB_START_MODE=false
LOAD_INITIAL_DATA=false
TEST_DATA=false

UI_DOCKERFILE="../ldcgc-frontend-elm"
case "$1" in
  create-ui|create-test-data-ui)
    regAlp='^[0-9_/a-zA-Z.]+$'
    echo "What frontend project you're using? [def: frontend-elm]"
    echo "1. frontend-elm"
    echo "2. frontend"
    read -rp "Please choose what frontend project you'regNum using, or the path to that project: " UI_OPTION

    if [[ $UI_OPTION = 2 ]] ; then
        UI_DOCKERFILE='../ldcgc-frontend'
    fi
    UI_DOCKERFILE=$(echo "$UI_DOCKERFILE" | sed "s/\/Dockerfile//g")
    if [ ! -d "$UI_DOCKERFILE" ] || [ ! -f "$UI_DOCKERFILE/Dockerfile" ] ; then
      echo "You chose invalid option, or the path you provided doesn't contain Dockerfile, or it doesn't exist"
      exit 1
    fi
  ;;
esac

case "$1" in
  create|create-api|create-ui)
    DB_START_MODE=create
    LOAD_INITIAL_DATA=true
    ;;
  create-test-data-api|create-test-data-ui)
    DB_START_MODE=create
    LOAD_INITIAL_DATA=true
    TEST_DATA=true
    ;;
  restart|restart-api|restart-ui)
    DB_START_MODE=none
    LOAD_INITIAL_DATA=false
    ;;
  purge)
    docker compose down
    docker rmi postgres -f
    docker rmi maildev/maildev -f
    rm -rf ./data
    set -e
    EXIT_CODE=0
    docker network rm ldcgc-backend_api_db_network || EXIT_CODE=$?
    docker network rm ldcgc-backend_api_smtp_network || EXIT_CODE=$?
    docker network rm ldcgc-backend_api_network || EXIT_CODE=$?
    docker network rm ldcgc-backend_ui_network || EXIT_CODE=$?
    echo "Network rm: " $EXIT_CODE
    set +e
    exit 1
    ;;
esac

echo "DB start mode = $DB_START_MODE"
echo "Load initial data = $LOAD_INITIAL_DATA"
echo "Load test data for tools and consumables = $TEST_DATA"
echo "Docker initialization mode = '$1'"
case "$1" in
  create|restart)
    DB_START_MODE=$DB_START_MODE LOAD_INITIAL_DATA=$LOAD_INITIAL_DATA TOOLS_REGISTRATION_TEST_DATA=$TEST_DATA CONSUMABLES_REGISTRATION_TEST_DATA=$TEST_DATA docker compose -f docker-compose-no-api.yml up -d
  ;;
  create-api|create-test-data-api|restart-api)
    DB_START_MODE=$DB_START_MODE LOAD_INITIAL_DATA=$LOAD_INITIAL_DATA TOOLS_REGISTRATION_TEST_DATA=$TEST_DATA CONSUMABLES_REGISTRATION_TEST_DATA=$TEST_DATA docker compose -f docker-compose-no-ui.yml up -d
  ;;
  create-ui|create-test-data-ui|restart-ui)
    DB_START_MODE=$DB_START_MODE LOAD_INITIAL_DATA=$LOAD_INITIAL_DATA TOOLS_REGISTRATION_TEST_DATA=$TEST_DATA CONSUMABLES_REGISTRATION_TEST_DATA=$TEST_DATA UI_DOCKERFILE=$UI_DOCKERFILE docker-compose up -d
  ;;
esac

case "$1" in
  create-api|create-test-data-api|restart-api|create-ui|create-test-data-ui|restart-ui)
    echo "ready to work with API @ http://localhost:8080/api, better from postman hehe"
    echo "check api is alive in http://localhost:8080/api/alive"
    echo "check swagger @ https://localhost:8080/api/swagger-ui/index.html"
  ;;
esac
case "$1" in
  create-ui|create-test-data-ui|restart-ui)
    echo "ready UI @ https://localhost:3000"
  ;;
esac
