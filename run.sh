git pull
docker compose down
docker compose rm -f
docker rmi ldcgc-backend -f
docker volume rm ldcgc-backend_postgresql
docker compose up
