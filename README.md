# Chess-pong
programation suibject number 2  (java swing)


# part-one : list of peice 

Classe mere : Piece.java
             
             class fille:
                
                -King.java
                -Queen.java
                -Rook.java
                -Bishop.java
                -Knight.java
                -Pawn.java

# Pong entities

ball.java
raquete.java

# GUI 

TerrainWindow.java:
    -TerrainPanel.java

# compile
javac -d out (Get-ChildItem -Recurse -Filter *.java).FullName

# run client
java -cp out net.client.GameClient


-----------------------------+
## ilay version sous reseaux | 
-----------------------------+

folder: net 
      folder: client
          -classes:
              # GameClient
              # TerrainState

      folder: core
          -classes:
              # GameLogic
              # GameState
              # PeiceState 
      folder: server
          -classes:
              # Gameserver
              # GameSetupFrame(serveur config)
              # PeiceLifeConfig
              # peicePreview (show size visualy)
              # ServerLauncher (start the server configuration before starting the server)


# EGB Structure

GameSetupFrame
   ↓
Microservice (EJB on WildFly)
   ↓
Database
   ↓
Microservice
   ↓
GameServer


# compile EJB
javac -d EJBout `
 -cp "libs\jakarta.jakartaee-api-10.0.0-sources.jar;C:\wildfly-38.0.1.Final\bin\client\jboss-client.jar" `
 net\server\EJB\GameConfigEntity.java `
 net\server\EJB\GameConfigServiceBean.java `
 net\server\EJB\GameConfigServiceRemote.java

 with java8:
                javac -source 1.8 -target 1.8 `
                -d EJBout `
                -cp "libs\javaee-api-7.0.jar;C:\wildfly-10.0.0.Final\bin\client\jboss-client.jar" `
                net\server\EJB\GameConfigEntity.java `
                net\server\EJB\GameConfigServiceBean.java `
                net\server\EJB\GameConfigServiceRemote.java
# verfy java compilation version
javap -verbose EJBout\net\server\EJB\GameConfigServiceBean.class | findstr "major"
        ✔ 52 = Java 8
 v     ❌ 65 = Java 21 (will fail on WildFly 10)

# next
 jar cf chess-pong-ejb.jar `
 -C EJBout . `
 META-INF\persistence.xml
# verify .jar
 jar tf chess-pong-ejb.jar



 # test
 javac `-cp "C:\wildfly-38.0.1.Final\bin\client\jboss-client.jar;EJBout;." TestGameConfigClient.java
 java `-cp "C:\wildfly-38.0.1.Final\bin\client\jboss-client.jar;EJBout;." TestGameConfigClient.java
 

# run evrything exept EJB
powershell -ExecutionPolicy Bypass -File compile-game.ps1










MariaDB [(none)]> CREATE DATABASE pong;
Query OK, 1 row affected (0.005 sec)

MariaDB [(none)]> CREATE USER 'dbuser'@'localhost' IDENTIFIED BY 'Andry';
Query OK, 0 rows affected (0.004 sec)

MariaDB [(none)]> GRANT ALL PRIVILEGES ON pong.* TO 'dbuser'@'localhost';
Query OK, 0 rows affected (0.004 sec)

MariaDB [(none)]> FLUSH PRIVILEGES;
Query OK, 0 rows affected (0.003 sec)

MariaDB [(none)]>