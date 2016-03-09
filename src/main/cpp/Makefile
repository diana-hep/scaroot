all: RootAccess.cpp
	mkdir -p ../../../target/native/linux-x86-64
	g++ RootAccess.cpp -o ../../../target/native/linux-x86-64/libRootAccess.so \
		-fPIC -shared \
		-Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs)
