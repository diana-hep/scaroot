all: RootBridge.cpp
	mkdir -p ../../../target/native/linux-x86-64
	g++ RootBridge.cpp -o ../../../target/native/linux-x86-64/libRootBridge.so \
		-fPIC -shared \
		-Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs)
