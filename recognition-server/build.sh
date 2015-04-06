# Build and tag the the current with commit.
git tag $1
echo "Set tag $1."
mvn package
cp target/recognition-server.war target/recognition-server-$1.war
echo "Build complete! War located at target/recognition-server-$1.war"
