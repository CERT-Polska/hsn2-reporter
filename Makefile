
COMPONENT=hsn2-reporter

all:	${COMPONENT}-package

clean:	${COMPONENT}-package-clean

${COMPONENT}-package:
	mvn clean install -U -Pbundle -Dmaven.test.skip
	mkdir -p build/${COMPONENT}
	tar xzf target/${COMPONENT}-1.0.0-SNAPSHOT.tar.gz -C build/${COMPONENT}

${COMPONENT}-package-clean:
	rm -rf build

build-local:
	mvn clean install -U -Pbundle