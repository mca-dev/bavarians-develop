# Postgres 13 dump
docker run postgres:13.10 pg_dump "host=57.128.201.126 port=5432 dbname=bavarians user=bavuser password=1234567890" > /Users/magdalenaciezka/deployments/bav2024/ovh_backup_$(date +"%F").dump
# Postgres restore
docker exec some-postgres psql -U postgres -c "CREATE USER bavuser WITH PASSWORD '1234567890';" 2>&1 || echo "User may already exist"
docker exec some-postgres psql -U postgres -c "CREATE DATABASE bavarians OWNER bavuser;"
docker exec -i some-postgres psql -U bavuser -d bavarians < "/Users/magdalena.dabrowska/Documents/repos/bavarians-develop/src/main/resources/last_dump/ovh_backup_2026-01-03.dump"
docker exec some-postgres psql -U bavuser -d bavarians -c "\dt"

# Running PostgreSQL 18 with Docker
docker run -d --name some-postgres -e POSTGRES_PASSWORD=1234567890 -p 5432:5432 postgres

# Useful commands
ps xw
sudo kill -9 24197
start app:
nohup java -jar bavarians-3.0.0-SNAPSHOT.jar &

# OVH Deployment
Dane dostępowe:
Nazwa serwera VPS: vps-e9e8e568.vps.ovh.net
- adres IPv4: 57.128.201.126
- adres IPv6: 2001:41d0:601:1100::341e
- nazwa użytkownika: ubuntu
  OVH: haslo  rUSrCdMGaBZW
  ssh ubuntu@57.128.201.126
  root TjpuXXH416G4
  rescue mode:
  ssh root@57.128.201.126
  TjpuXXH416G4

# Connect to OVH server
ssh ubuntu@57.128.201.126
OVH: haslo  rUSrCdMGaBZW

# Stop running application
ubuntu@vps-e9e8e568:~$ ps -elf | grep -v grep | grep java
ubuntu@vps-e9e8e568:~$ sudo ps xw
65794 ?        S      0:00 sudo nohup java -jar bavarians-3.0.3-SNAPSHOT.jar
65795 pts/0    Ss+    0:00 sudo nohup java -jar bavarians-3.0.3-SNAPSHOT.jar
65796 pts/0    Sl   502:41 java -jar bavarians-3.0.3-SNAPSHOT.jar

sudo kill -9 65794
sudo kill -9 65795
sudo kill -9 65796

# Copy new jar file
magdalenaciezka@MacBook-Pro-Magdalena deployments % scp bavarians-3.0.4-SNAPSHOT.jar ubuntu@57.128.201.126:/home/ubuntu

# Start application
sudo nohup java -jar bavarians-3.0.7-SNAPSHOT.jar &
sudo java -jar bavarians-3.0.7-SNAPSHOT.jar
