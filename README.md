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

