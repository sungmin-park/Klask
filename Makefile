build: tql
	./gradlew clean jar

tql:
	cd ../tql && ./gradlew clean jar
	cp ../tql/build/libs/*.jar libs
