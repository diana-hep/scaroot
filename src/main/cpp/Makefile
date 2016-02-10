all: nativeRoot.cpp
	g++ -fPIC -shared -Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs) -o ../../../src/main/resources/native/nativeRoot.so nativeRoot.cpp
