build-UnicornLocationPostFunction:

build-UnicornLocationGetFunction:

build-UnicornLocationDeleteFunction:

build-UnicornLocationPutFunction:
	mvn clean package
	mvn dependency:copy-dependencies -DexcludeScope=provided
	cp -rf ./target/classes/* $(ARTIFACTS_DIR)/