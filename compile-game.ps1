$src = @(
    "net\core",
    "net\server",
    "entities",
    "pong_entities",
    "Users"
)

$files = foreach ($dir in $src) {
    Get-ChildItem $dir -Recurse -Filter *.java |
    Where-Object { $_.FullName -notlike "*\net\server\EJB\*" }
}

javac `
 -cp "EJBout;libs\jakarta.jakartaee-api-10.0.0.jar" `
 -d out `
 $files.FullName
