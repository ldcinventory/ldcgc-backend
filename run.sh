n=0
#Open Docker, only if is not running
if (! docker stats --no-stream ); then
  read -p 'Docker is not started, do you want for this script to try to start it? [Y/n] ' start_docker
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

git pull
docker compose down -v
docker compose rm -f
docker rm ldcgc-backend -f
docker rmi ldcgc-backend -f

DB_START_MODE=create
LOAD_INITIAL_DATA=false
if [ -z "$1" ]; then
  echo "No option selected. Use: $0 {create|create-test-data|restart|purge}"
else
  case "$1" in
    create)
      #docker rmi postgres -f
      #docker rmi maildev/maildev -f
      DB_START_MODE=create
      LOAD_INITIAL_DATA=true
      ;;
    create-test-data)
      #docker rmi postgres -f
      #docker rmi maildev/maildev -f
      DB_START_MODE=create
      LOAD_INITIAL_DATA=true
      TEST_DATA=true
      ;;
    restart)
      #docker-compose up maildev
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
      echo "Network rm: " $EXIT_CODE
      set +e
      exit 1
      ;;
  esac

  echo "DB start mode=$DB_START_MODE"
  echo "Load initial data=$LOAD_INITIAL_DATA"
  echo "Load test data for tools and consumables=$TEST_DATA"

  DB_START_MODE=$DB_START_MODE LOAD_INITIAL_DATA=$LOAD_INITIAL_DATA TEST_DATA=$TEST_DATA docker-compose up -d

  echo "ready to work with API @ http://localhost:8080/api, better from postman hehe"
  echo "check api is alive in http://localhost:8080/api/alive"
  echo "check swagger @ https://localhost:8080/api/swagger-ui/index.html"

fi
