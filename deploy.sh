cd target
scp -i "/Users/magdalenaciezka/deployments/mcakey.pem" bavarians-2.1.2-SNAPSHOT.jar ec2-user@ec2-18-185-63-167.eu-central-1.compute.amazonaws.com:/home/ec2-user
ssh -i "/Users/magdalenaciezka/deployments/mcakey.pem" ec2-user@ec2-18-185-63-167.eu-central-1.compute.amazonaws.com
#ps xw
#sudo kill -9 5664
# sudo nohup java -jar bavarians-2.1.2-SNAPSHOT.jar &
#java -jar bavarians-2.1.1-SNAPSHOT.jar


#restoruje remote rds z dumpa z pliku
# psql -h bavarians.cd6teixsqvhv.eu-north-1.rds.amazonaws.com -p 5432 -U postgres -d bavarians  < dump-10122023.sql
ssh -i "/Users/magdalenaciezka/deployments/mcakey.pem" ec2-user@ec2-user@ec2-16-171-233-215.eu-north-1.compute.amazonaws.com

#----- zdalny dump i local restore
docker run postgres:13.10 pg_dump "host=bavarians.colj8q4bzvuk.eu-central-1.rds.amazonaws.com port=5432 dbname=bavarians user=postgres password=1234567890" > /Users/magdalenaciezka/deployments/bavarians111223.dump

psql -U postgres bavarians -f bavarians111223.dump

psql -h 57.128.201.126 -p 5432 -U postgres -d bavarians  < bavarians111223.dump




#---
ssh -i "/Users/magdalenaciezka/deployments/mcakey.pem" ec2-user@ec2-16-171-55-58.eu-north-1.compute.amazonaws.com
-----
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
ps xw
ps -elf | grep -v grep | grep java
sudo kill -9 24197
start app:
sudo nohup java -jar bavarians-3.0.3-SNAPSHOT.jar &
sudo java -jar bavarians-3.0.2-SNAPSHOT.jar

# SELECT setval('bav_seq', 10000, true);


    pg_ctlcluster 13 main start
sudo systemctl start postgresql@13-main
sudo apt-get --purge remove postgresql postgresql-15  postgresql-client  postgresql-client-15 postgresql-client-common postgresql-common
ubuntu@vps-e9e8e568:/etc/postgresql$ sudo apt-get -y install postgresql-13
    pg_ctlcluster 13 main start
sudo su - postgres
psql
---
ubuntu@vps-e9e8e568:/etc/postgresql$ ps -ef | grep postgres


scp bav-dump-111223.dump ubuntu@57.128.201.126:/home/ubuntu
scp bavarians-3.0.3-SNAPSHOT.jar ubuntu@57.128.201.126:/home/ubuntu
scp bavarians-3.0.4-SNAPSHOT.jar ubuntu@57.128.201.126:/home/ubuntu

#----trorzy plik dumpa, wykonac lokalnie
docker run postgres:13.10 pg_dump "host=bavarians.colj8q4bzvuk.eu-central-1.rds.amazonaws.com port=5432 dbname=bavarians user=postgres password=1234567890" > /Users/magdalenaciezka/deployments/bav2024/bav-dump-111223_2.dump
docker run postgres:13.10 pg_dump "host=57.128.201.126 port=5432 dbname=bavarians user=bavuser password=1234567890" > /Users/magdalenaciezka/deployments/bav2024/ovh_13Dec.dump
docker run postgres:13.10 pg_dump "host=57.128.201.126 port=5432 dbname=bavarians user=bavuser password=1234567890" > /Users/magdalenaciezka/deployments/bav2024/ovh_24March.dump
docker run postgres:13.10 pg_dump "host=57.128.201.126 port=5432 dbname=bavarians user=bavuser password=1234567890" > /Users/magdalenaciezka/deployments/bav2024/ovh_24March.dump

#restoruje baze z pliku dump, wykonac lokalnie
sudo su - postgres
psql
DROP DATABASE bavarians WITH (FORCE);
CREATE DATABASE "bavarians3jul";
CREATE USER bavuser WITH ENCRYPTED PASSWORD '1234567890';
GRANT ALL PRIVILEGES ON DATABASE bavarians TO bavuser;

psql "host=57.128.201.126 port=5432 dbname=bavarians user=bavuser password=1234567890" < bav-dump-111223_2.dump




----
dentyfikator: 50811
Hasło: qMvkecw3
Strona logowania: https://centrum.cal.pl

psql -h localhost -p 5432 -U postgres -d bavarians  < ovh_3Jul.dump
psql -h localhost -p 5432 -U postgres -d bavarians3jul  < ovh_3Jul.dump


sudo nohup java -jar bavarians-3.0.7-SNAPSHOT.jar
 nohup java -jar bavarians-3.0.7-SNAPSHOT.jar