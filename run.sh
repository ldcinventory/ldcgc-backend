git pull
docker compose down -v
docker compose rm -f
docker rmi ldcgc-backend -f
docker compose up
