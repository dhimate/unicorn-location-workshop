build-UnicornLocationPostFunction:
	mvn clean package
	mvn dependency:copy-dependencies -DexcludeScope=provided
	cp -rf ./target/classes/* $(ARTIFACTS_DIR)/
build-UnicornLocationGetFunction:
	mvn clean package
	mvn dependency:copy-dependencies -DexcludeScope=provided
	cp -rf ./target/classes/* $(ARTIFACTS_DIR)/